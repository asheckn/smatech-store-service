package com.asheck.smatech_store_service.cart;


import com.asheck.smatech_store_service.cart.cart_item.CartItem;
import com.asheck.smatech_store_service.cart.cart_item.CartItemDto;

import com.asheck.smatech_store_service.cart.cart_item.CartItemRepository;
import com.asheck.smatech_store_service.helper.RestTemplateService;
import com.asheck.smatech_store_service.product.Product;
import com.asheck.smatech_store_service.product.ProductService;
import com.asheck.smatech_store_service.user.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final ProductService productService;


    private final RestTemplateService restTemplateService;

    public Cart getCart(HttpServletRequest request){
        ///use token to get user from auth service
        final String authHeader = request.getHeader("Authorization");
        final String jwt = authHeader.substring(7);
        UserDto userDto = restTemplateService.getAuthenticatedUser(jwt);
        int customerId = userDto.id();
        // Try and get a cart that is not checkout out , if there is none, create a new cart and return it without items
        Cart existingCart =  cartRepository.findCartByCheckedOutAndCustomerId(false, (long) customerId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setCheckedOut(false);
            cart.setItems(new ArrayList<>());
            cart.setCustomerId(customerId);
            return cartRepository.save(cart);
        });

        calculateTotal(existingCart);
         return existingCart;


    }

    public Cart getCartById(Long cartId){
        return cartRepository.findById(cartId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
    }

    public Cart addCartItem(CartItemDto cartItemDto, Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        Product product = productService.getProductById(cartItemDto.productId());

        if(product.getStock() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is out of stock");
        }

        //Check if product is already in cart and increment it
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getId() == cartItemDto.productId()) {
                item.setQuantity(item.getQuantity() + cartItemDto.quantity());
                item.setTotal(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                item.setVatAmount(product.getVatRate().multiply(product.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())));
                calculateTotal(cart);
                return cartRepository.save(cart);
            }
        }



        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(cartItemDto.quantity());
        cartItem.setTotal(product.getPrice().multiply(BigDecimal.valueOf(cartItemDto.quantity())));
        cartItem.setVatAmount(product.getVatRate().multiply(product.getPrice()).multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        cart.getItems().add(cartItem);

        calculateTotal(cart);

        cartRepository.save(cart);

        return cartRepository.save(cart);
    }

    private static void calculateTotal(Cart cart) {
        BigDecimal subTotal  = BigDecimal.valueOf(0);
        BigDecimal vatTotal = BigDecimal.valueOf(0);
        for (CartItem item : cart.getItems()) {
            subTotal = subTotal.add(item.getTotal());
            vatTotal = vatTotal.add(item.getVatAmount());
        }
        cart.setSubTotal(subTotal);
        cart.setVatTotal(vatTotal);
        cart.setTotal(vatTotal.add(subTotal));
    }

    public Cart removeCartItem(Long cartId, Long cartItemId){
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        CartItem cartItem = cart.getItems().stream().filter(item -> item.getId() == cartItemId).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));
        cart.getItems().remove(cartItem);

        calculateTotal(cart);
        cartRepository.save(cart);
        return cart;
    }

    public Cart decreaseItem(Long cartId, Long cartItemId, int value){
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        CartItem cartItem = cart.getItems().stream().filter(item -> item.getId() == cartItemId).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        cartItem.setQuantity(cartItem.getQuantity() - value);

        if(cartItem.getQuantity() <= 0){
            cart.getItems().remove(cartItem);
        }
        //Calculate totals
        cartItem.setTotal(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        cartItem.setVatAmount(cartItem.getProduct().getVatRate().multiply(cartItem.getProduct().getPrice()).multiply(BigDecimal.valueOf(cartItem.getQuantity())));

        cartItemRepository.save(cartItem);

        calculateTotal(cart);
        cartRepository.save(cart);
        return cart;
    }

    public Cart checkOutCart(Long cartId){
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        cart.setCheckedOut(true);
        cartRepository.save(cart);
        return cart;
    }

    public List<Cart> getCartByUser(Long customerId){
        return cartRepository.findCartByCustomerId(customerId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carts not found"));
    }




}

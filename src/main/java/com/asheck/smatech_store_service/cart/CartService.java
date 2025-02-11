package com.asheck.smatech_store_service.cart;


import com.asheck.smatech_store_service.cart.cart_item.CartItem;
import com.asheck.smatech_store_service.cart.cart_item.CartItemDto;
import com.asheck.smatech_store_service.product.Product;
import com.asheck.smatech_store_service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    private final CartService cartService;

    private final ProductService productService;

    public Cart getCart(){
        ///use token to get user from auth service
        int customerId = 1;

        // Try and get a cart that is not checkout out , if there is none, create a new cart and return it without items
        return cartRepository.findCartByCheckedOut(false).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setCheckedOut(false);
            cart.setItems(new ArrayList<>());
            cart.setCustomerId(customerId);
            return cartRepository.save(cart);
        });

    }

    public Cart getCartById(long cartId){
        return cartRepository.findById(cartId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
    }

    public Cart addCartItem(CartItemDto cartItemDto, long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        Product product = productService.getProductById(cartItemDto.productId());

        if(product.getStock() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is out of stock");
        }
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(cartItemDto.quantity());
        cartItem.setTotal(product.getPrice().multiply(BigDecimal.valueOf(cartItemDto.quantity())));
        cartItem.setVatAmount(product.getVatRate().multiply(product.getPrice()).multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        cart.getItems().add(cartItem);

        BigDecimal subTotal  = BigDecimal.valueOf(0);

        BigDecimal vatTotal = BigDecimal.valueOf(0);

        for (CartItem item : cart.getItems()) {
            subTotal = subTotal.add(item.getTotal());
            vatTotal = vatTotal.add(item.getVatAmount());
        }

        cart.setSubTotal(subTotal);
        cart.setVatTotal(vatTotal);
        cart.setTotal(vatTotal.add(vatTotal));

        cartRepository.save(cart);

        return cartRepository.save(cart);
    }
}

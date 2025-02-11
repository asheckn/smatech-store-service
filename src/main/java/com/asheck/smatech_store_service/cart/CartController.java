package com.asheck.smatech_store_service.cart;


import com.asheck.smatech_store_service.cart.cart_item.CartItemDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/get-cart")
    public ResponseEntity<?> getCart(HttpServletRequest request){
        return ResponseEntity.ok(cartService.getCart(request));
    }

    @GetMapping("/get-cart-by-id/{cartId}")
    public ResponseEntity<?> getCartById(@PathVariable Long cartId){
        return ResponseEntity.ok(cartService.getCartById(cartId));
    }

    @PutMapping("/add-to-cart/{cartId}")
    public ResponseEntity<?> addToCart(@RequestBody CartItemDto request, @PathVariable Long cartId){
        return ResponseEntity.ok(cartService.addCartItem(request, cartId));
    }

    @PatchMapping("/remove-from-cart/{cartId}")
    public ResponseEntity<?> removeFromCart(@RequestParam Long cartItemId, @PathVariable Long cartId){
        return ResponseEntity.ok(cartService.removeCartItem(cartId, cartItemId));
    }
}

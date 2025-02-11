package com.asheck.smatech_store_service.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findCartByCheckedOut(Boolean checkoutOut);
}

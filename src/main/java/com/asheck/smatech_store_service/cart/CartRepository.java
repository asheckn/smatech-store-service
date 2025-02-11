package com.asheck.smatech_store_service.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findCartByCheckedOutAndCustomerId(Boolean checkoutOut, Long customerId);

    Optional<List<Cart>>  findCartByCustomerId(Long customerId);
}

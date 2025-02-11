package com.asheck.smatech_store_service.order;

import com.asheck.smatech_store_service.product.Product;
import com.asheck.smatech_store_service.product.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<StoreOrder, Long> {


    Optional<List<StoreOrder>> findStoreOrdersByCustomerId(Long customerId);

    Optional<StoreOrder> findStoreOrderByOrderCode(UUID reference);

    @Query("SELECT p FROM StoreOrder p WHERE " +
            "(:customerId IS NULL OR p.customerId = :customerId) AND " +
            "(:orderStatus IS NULL OR p.orderStatus = :orderStatus) ")
    Page<StoreOrder> findByFilters(@Param("customerId") Long customerId,
                                @Param("orderStatus") OrderStatus orderStatus,
                                Pageable pageable);
}

package com.asheck.smatech_store_service.product;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Long > {

    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR p.name LIKE :name) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:price IS NULL OR p.price = :price) AND " +
            "(:status IS NULL OR p.status = :status) " +
            "AND (p.deleted IS NULL OR p.deleted = FALSE)")
    Page<Product> findByFilters(@Param("name") String name,
                                @Param("categoryId") Long categoryId,
                                @Param("price") BigDecimal price,
                                @Param("status") ProductStatus status,
                                Pageable pageable);

    Optional<Product> findProductByProductCode(UUID fromString);
}

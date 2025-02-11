package com.asheck.smatech_store_service.product;

import java.math.BigDecimal;

public record ProductDto(
        String name,
        Long categoryId,
        String description,
        BigDecimal price,
        BigDecimal vatRate,
        String currencyCode,
        Integer stock,
        ProductStatus status
) {
}

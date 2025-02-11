package com.asheck.smatech_store_service.product;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
public class ProductDtoAttribute {
     String name;
    long categoryId;
    String description;
    BigDecimal price;
    BigDecimal vatRate;
    String currencyCode;
    Integer stock;
    ProductStatus status;
}

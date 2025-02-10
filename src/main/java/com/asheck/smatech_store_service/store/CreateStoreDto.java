package com.asheck.smatech_store_service.store;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public record CreateStoreDto(
        String storeName,
        String address,
        LocalDateTime openingTime,
        LocalDateTime closingTime,
        String description,
        String email,
        List<String> contactNumbers,
        String taxNumber,
        String registrationNumber
) {
}

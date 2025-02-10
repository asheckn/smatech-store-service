package com.asheck.smatech_store_service.store;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@RequiredArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false)
    private UUID storeCode;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private LocalDateTime openingTime;

    @Column(nullable = false)
    private LocalDateTime closingTime;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String email;

    @ElementCollection
    @CollectionTable(name = "store_contact_numbers", joinColumns = @JoinColumn(name = "store_id"))
    @Column(name = "contact_number")
    private List<String> contactNumbers;

    private String taxNumber;

    private String registrationNumber;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;








}

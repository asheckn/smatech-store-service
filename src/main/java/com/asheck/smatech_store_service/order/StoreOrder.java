package com.asheck.smatech_store_service.order;

import com.asheck.smatech_store_service.cart.Cart;
import com.asheck.smatech_store_service.order.order_item.OrderItem;
import com.asheck.smatech_store_service.store.StoreRepository;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@RequiredArgsConstructor
public class StoreOrder {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @Column(nullable = false)
    private UUID orderCode;

    @Column(nullable = false, unique = true, length = 10)
    private String invoiceNumber;

    @Column(nullable = false)
    private BigDecimal orderTotal;

    @Column(nullable = false)
    private BigDecimal vatTotal;

    @Column(nullable = false)
    private BigDecimal subTotal;

    @OneToMany(mappedBy = "storeOrder", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<OrderItem> orderItems;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @OneToOne(cascade = CascadeType.ALL)
    private Cart cart;

    @Column(nullable = false)
    private String currencyCode;


    @Column(nullable = false)
    private Long customerId;

    private Boolean deleted = false;

    private UUID reference;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @PrePersist
    public void generateOrderCode() {
        if (this.orderCode == null) {
            this.orderCode = UUID.randomUUID();
        }
    }


}

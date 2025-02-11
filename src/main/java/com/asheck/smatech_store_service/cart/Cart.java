package com.asheck.smatech_store_service.cart;

import com.asheck.smatech_store_service.cart.cart_item.CartItem;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@RequiredArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private UUID cartCode;

    @Column(nullable = false)
    private BigDecimal total = BigDecimal.valueOf(0);

    @Column(nullable = false)
    private BigDecimal vatTotal = BigDecimal.valueOf(0);

    @Column(nullable = false)
    private BigDecimal subTotal = BigDecimal.valueOf(0);

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL , orphanRemoval = true)
    private List<CartItem> items ;

    @Column(nullable = false)
    private long customerId;

    private Boolean deleted = false;

    private Boolean checkedOut = false;

    private String status = "active";

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    public void generateOrderCode() {
        if (this.cartCode == null) {
            this.cartCode = UUID.randomUUID();
        }
    }


}

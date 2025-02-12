package com.asheck.smatech_store_service.order;


import com.asheck.smatech_store_service.helper.ApiResponse;
import com.asheck.smatech_store_service.product.ProductStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(path = "api/v1/order")
@RequiredArgsConstructor
public class OrderController {


    private final OrderService orderService;

    @GetMapping("/get-orders")
    public ResponseEntity<?> getOrders(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<StoreOrder> orders =  orderService.getAllOrders(customerId, status, page, size);

        return ResponseEntity.ok(new ApiResponse<>(true, "Orders retrieved successfully", orders));
    }

    //Create Order
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(
            @RequestParam Long cartId
    ){
        StoreOrder order = orderService.createOrder(cartId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order created successfully", order));
    }

    //Get Order by Id
    @GetMapping("/get-order/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId){
        StoreOrder order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order retrieved successfully", order));
    }

    //Update Order Status
    @PutMapping("/update-order-status/{orderCode}")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable String orderCode,
            @RequestParam OrderStatus status
    ){
        StoreOrder order = orderService.updateOrderStatus(orderCode, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order status updated successfully", order));
    }

    // Cancel Order
    @PutMapping("/cancel-order/{orderId}")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long orderId
    ){
        StoreOrder order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order cancelled successfully", order));
    }

    // complete Order
    @PutMapping("/complete-order/{orderId}")
    public ResponseEntity<?> completeOrder(
            @PathVariable String orderId
    ){
        StoreOrder order = orderService.updateOrderStatus(orderId, OrderStatus.COMPLETE);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order completed successfully", order));
    }

    // Pay Order
    @PutMapping("/pay-order/{orderCode}")
    public ResponseEntity<?> payOrder(
            @PathVariable String orderCode
    ){
        StoreOrder order = orderService.updateOrderStatus(orderCode, OrderStatus.PAID);
        // Add kafka Notification later
        return ResponseEntity.ok(new ApiResponse<>(true, "Order paid successfully", order));
    }


   // Get Orders by CustomerId
    @GetMapping ("/get-orders-by-customer/{customerId}")
    public ResponseEntity<?> getOrdersByCustomerId(@PathVariable Long customerId){
        return ResponseEntity.ok(new ApiResponse<>(true, "Orders retrieved successfully", orderService.getOrdersByCustomerId(customerId)));
    }

    // Get Customers orders by token
    @GetMapping("/get-my-orders")
    public ResponseEntity<?> getCustomerOrders(HttpServletRequest request){
        return ResponseEntity.ok(new ApiResponse<>(true, "Orders retrieved successfully", orderService.getCustomerOrders(request)));
    }

    // Get order by code
    @GetMapping("/get-order-by-code")
    public ResponseEntity<?> getOrderByCode(@RequestParam String orderCode){
        return ResponseEntity.ok(new ApiResponse<>(true, "Order retrieved successfully", orderService.getOrderByCode(orderCode)));
    }

}

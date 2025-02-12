package com.asheck.smatech_store_service.order;


import com.asheck.smatech_store_service.cart.Cart;
import com.asheck.smatech_store_service.cart.CartService;
import com.asheck.smatech_store_service.cart.cart_item.CartItem;
import com.asheck.smatech_store_service.helper.RestTemplateService;
import com.asheck.smatech_store_service.order.order_item.OrderItem;
import com.asheck.smatech_store_service.product.ProductService;
import com.asheck.smatech_store_service.product.ProductStatus;
import com.asheck.smatech_store_service.user.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final CartService cartService;

    private final ProductService productService;

    private final RestTemplateService restTemplateService;

    // Creates an order from a cart
    @Transactional
    public StoreOrder createOrder(Long cartId){

        Cart cart = cartService.getCartById(cartId);
        StoreOrder order = new StoreOrder();

            Long lastId = orderRepository.findMaxId();
            String invoiceNumber = String.format("%05d", (lastId == null ? 1 : lastId + 1));

        order.setInvoiceNumber(invoiceNumber);
        order.setCart(cart);
        order.setCustomerId(cart.getCustomerId());
        order.setOrderTotal(cart.getTotal());
        order.setOrderStatus(OrderStatus.INITIATED);
        order.setReference(UUID.randomUUID());
        order.setVatTotal(cart.getVatTotal());
        order.setSubTotal(cart.getSubTotal());
        order.setCurrencyCode("USD");

        List<OrderItem> orderItemList = new ArrayList<>();;

        for(CartItem item : cart.getItems()){
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setStoreOrder(order);
            orderItem.setTotal(item.getTotal());
            orderItem.setVatAmount(item.getVatAmount());
            //Deduct from product quantity
            productService.removeStock(item.getProduct().getProductCode().toString(), item.getQuantity());
            orderItemList.add(orderItem);
        }
        Cart checkedOutCart = cartService.checkOutCart(cartId);
        order.setOrderItems(orderItemList);

        return orderRepository.save(order);
    }

    // Get order by id
    public StoreOrder getOrderById(Long orderId){
        return orderRepository.findById(orderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    ///Get all customer orders
    public List<StoreOrder> getOrdersByCustomerId(Long customerId){
        return orderRepository.findStoreOrdersByCustomerId(customerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No orders found")
        );
    }

    ///Get all customer orders by token
    public List<StoreOrder> getCustomerOrders(HttpServletRequest request){
        final String authHeader = request.getHeader("Authorization");
        final String jwt = authHeader.substring(7);
        UserDto userDto = restTemplateService.getAuthenticatedUser(jwt);
        int customerId = userDto.id();

        return orderRepository.findStoreOrdersByCustomerId((long) customerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No orders found")
        );
    }

    // Get All orders
    public Page<StoreOrder> getAllOrders( Long customerId, OrderStatus status, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderStatus").ascending());
        Page<StoreOrder> ordersPage = orderRepository.findByFilters(customerId, status, pageable);

        if (ordersPage.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No orders found");
        }
        return ordersPage;
    }

    // Get order by code
    public StoreOrder getOrderByCode(String orderCode){
        return orderRepository.findStoreOrderByOrderCode(UUID.fromString(orderCode)).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")
        );
    }

    // Update order status
    public StoreOrder updateOrderStatus(String orderCode, OrderStatus status){
        StoreOrder order = orderRepository.findStoreOrderByOrderCode(UUID.fromString(orderCode)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

    // Cancel order
    public StoreOrder cancelOrder(Long orderId){
        StoreOrder order = orderRepository.findById(orderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        order.setOrderStatus(OrderStatus.CANCELLED);
        //Add back stock to products
        for(OrderItem item : order.getOrderItems()){
            productService.addStock(item.getProduct().getProductCode().toString(), item.getQuantity());
        }
        return orderRepository.save(order);
    }



}

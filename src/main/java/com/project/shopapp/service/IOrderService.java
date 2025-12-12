package com.project.shopapp.service;

import com.project.shopapp.dto.OrderDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.model.Order;
import com.project.shopapp.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {
    Order createOrder(OrderDTO orderDTO) throws Exception;
    Order getOrderById(Long orderId);
    Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException;
    void deleteOrder(Long id);
    List<OrderResponse> findByUserId(Long userId);
    Page<Order> getOrdersByKeyword(String keyword, Pageable pageable);
    Order updateOrderStatus(Long id, String status) throws DataNotFoundException;
}

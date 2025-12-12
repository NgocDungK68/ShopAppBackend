package com.project.shopapp.controller;

import com.project.shopapp.component.LocalizationUtils;
import com.project.shopapp.dto.OrderDetailDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.model.OrderDetail;
import com.project.shopapp.response.OrderDetailResponse;
import com.project.shopapp.response.ResponseObject;
import com.project.shopapp.service.OrderDetailService;
import com.project.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> createOrderDetail(
            @Valid @RequestBody OrderDetailDTO orderDetailDTO
    ) throws Exception {
        OrderDetail newOrderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
        OrderDetailResponse orderDetailResponse = OrderDetailResponse.fromOrderDetail(newOrderDetail);
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Create order detail successfully")
                        .status(HttpStatus.CREATED)
                        .data(orderDetailResponse)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getOrderDetail(
            @Valid @PathVariable Long id
    ) throws DataNotFoundException {
        OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
        OrderDetailResponse orderDetailResponse = OrderDetailResponse.fromOrderDetail(orderDetail);
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Get order detail successfully")
                        .status(HttpStatus.OK)
                        .data(orderDetailResponse)
                        .build()
        );
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ResponseObject> getOrderDetails(@Valid @PathVariable Long orderId) {
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses = orderDetails
                .stream()
                .map(OrderDetailResponse::fromOrderDetail)
                .toList();
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Get order details by orderId successfully")
                        .status(HttpStatus.OK)
                        .data(orderDetailResponses)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> updateOrderDetail(
            @Valid @PathVariable Long id,
            @RequestBody OrderDetailDTO orderDetailDTO
    ) throws Exception {
        OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, orderDetailDTO);
        return ResponseEntity.ok().body(ResponseObject
                .builder()
                .data(orderDetail)
                .message("Update order detail successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteOrderDetail(
            @Valid @PathVariable Long id
    ) {
        orderDetailService.deleteById(id);
        return ResponseEntity.ok()
                .body(ResponseObject.builder()
                        .message(localizationUtils
                                .getLocalizedMessage(MessageKeys.DELETE_ORDER_DETAIL_SUCCESSFULLY))
                        .build());
    }
}

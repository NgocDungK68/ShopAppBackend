package com.project.shopapp.controller;

import com.project.shopapp.component.LocalizationUtils;
import com.project.shopapp.component.SecurityUtils;
import com.project.shopapp.dto.OrderDTO;
import com.project.shopapp.model.Order;
import com.project.shopapp.model.User;
import com.project.shopapp.response.OrderListResponse;
import com.project.shopapp.response.OrderResponse;
import com.project.shopapp.response.ResponseObject;
import com.project.shopapp.service.OrderService;
import com.project.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final LocalizationUtils localizationUtils;
    private final SecurityUtils securityUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> createOrder(
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult result
    ) throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(String.join(";", errorMessages))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
        User loginUser = securityUtils.getLoggedInUser();
        if (orderDTO.getUserId() == null) {
            orderDTO.setUserId(loginUser.getId());
        }
        Order orderResponse = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Insert order successfully")
                .data(orderResponse)
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<ResponseObject> getOrders(@Valid @PathVariable("user_id") Long userId) {
        User loginUser = securityUtils.getLoggedInUser();
        boolean isUserIdBlank = userId == null || userId <= 0;
        List<OrderResponse> orderResponses = orderService.findByUserId(isUserIdBlank ? loginUser.getId() : userId);
        return ResponseEntity.ok(ResponseObject
                .builder()
                .message("Get list of orders successfully")
                .data(orderResponses)
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getOrder(@Valid @PathVariable("id") Long orderId) {
        Order existingOrder = orderService.getOrderById(orderId);
        OrderResponse orderResponse = OrderResponse.fromOrder(existingOrder);
        return ResponseEntity.ok(new ResponseObject(
                "Get order successfully",
                HttpStatus.OK,
                orderResponse
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateOrder(
            @Valid @PathVariable Long id,
            @Valid @RequestBody OrderDTO orderDTO
    ) throws Exception {
        Order order = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(new ResponseObject("Update order successfully", HttpStatus.OK, order));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteOrder(@Valid @PathVariable Long id) {
        // xoá mềm
        orderService.deleteOrder(id);
        String message = localizationUtils.getLocalizedMessage(
                MessageKeys.DELETE_ORDER_SUCCESSFULLY, id);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message(message)
                        .build()
        );
    }

    @GetMapping("/get-orders-by-keyword")
    public ResponseEntity<ResponseObject> getOrdersByKeyword(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "0") int limit) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());

        Page<OrderResponse> orderPage = orderService
                .getOrdersByKeyword(keyword, pageRequest)
                .map(OrderResponse::fromOrder);

        OrderListResponse response = OrderListResponse.builder()
                .orders(orderPage.getContent())
                .totalPages(orderPage.getTotalPages())
                .currentPage(page)
                .build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get orders successfully")
                .status(HttpStatus.OK)
                .data(response)
                .build());
    }
}

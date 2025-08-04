package com.example.rest.web.controller.v2;

import com.example.rest.mapper.v2.OrderMapperV2;
import com.example.rest.model.Order;
import com.example.rest.service.OrderService;
import com.example.rest.web.model.OrderFilter;
import com.example.rest.web.model.OrderListResponse;
import com.example.rest.web.model.OrderResponse;
import com.example.rest.web.model.UpsertOrderRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v2/order")
@RequiredArgsConstructor
public class OrderControllerV2 {


    private final OrderService dataBaseOrderService;

    private final OrderMapperV2 orderMapper;

    @GetMapping("/filter")
    public ResponseEntity<OrderListResponse> filterBy(@Valid OrderFilter filter){
        return ResponseEntity.ok(
                orderMapper.orderListToOrderListResponse(
                        dataBaseOrderService.filterBy(filter)));
    }

    @GetMapping
    public ResponseEntity<OrderListResponse> findAll() {
        return ResponseEntity.ok(
                orderMapper.orderListToOrderListResponse(
                        dataBaseOrderService.findAll()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                orderMapper.orderToResponse(
                        dataBaseOrderService.findById(id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid UpsertOrderRequest request){
        Order newOrder = dataBaseOrderService.save(orderMapper.requestToOrder(request));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderMapper.orderToResponse(newOrder));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> update(@PathVariable("id") Long orderId, @RequestBody @Valid UpsertOrderRequest request){
        Order updatedOrder = dataBaseOrderService.update(orderMapper.requestToOrder(orderId, request));

        return ResponseEntity.ok(orderMapper.orderToResponse(updatedOrder));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dataBaseOrderService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}

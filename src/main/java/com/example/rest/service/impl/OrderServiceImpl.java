package com.example.rest.service.impl;

import com.example.rest.exception.EntityNotFoundException;
import com.example.rest.model.Order;
import com.example.rest.repository.OrderRepository;
import com.example.rest.service.OrderService;
import com.example.rest.web.model.OrderFilter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<Order> filterBy(OrderFilter filter) {
        throw new NotImplementedException();
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Заказ с id: {0} не найден!", id)));
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order update(Order order) {
        checkForUpdate(order.getId());
        return orderRepository.update(order);
    }

    @Override
    public void deleteById(Long id) {
        Order currentOrder = findById(id);
        currentOrder.getClient().removeOrder(id);
        orderRepository.deleteById(id);

    }

    @Override
    public void deleteByIdIn(List<Long> ids) {
        orderRepository.deleteByIdIn(ids);

    }


}

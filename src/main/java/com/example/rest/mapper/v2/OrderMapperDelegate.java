package com.example.rest.mapper.v2;

import com.example.rest.model.Order;
import com.example.rest.service.ClientService;
import com.example.rest.web.model.UpsertOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class OrderMapperDelegate implements OrderMapperV2 {

    @Autowired
    private ClientService dataBaseClientService;

    @Override
    public Order requestToOrder(UpsertOrderRequest request) {
        Order order = new Order();
        order.setCost(request.getCost());
        order.setProduct(request.getProduct());
        order.setClient(dataBaseClientService.findById(request.getClientId()));
        return order;
    }

    @Override
    public Order requestToOrder(Long id, UpsertOrderRequest request) {
        Order order = requestToOrder(request);
        order.setId(id);

        return order;
    }
}

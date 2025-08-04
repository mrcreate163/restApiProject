package com.example.rest.service;

import com.example.rest.model.Client;
import com.example.rest.model.Order;

import java.util.List;


public interface ClientService {

    List<Client> findAll();

    Client findById(Long id);

    Client save(Client client);

    Client update(Client client);

    void deleteById(Long id);

    Client saveWithOrders(Client client, List<Order> orders);
}

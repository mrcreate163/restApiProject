package com.example.rest.service.impl;

import com.example.rest.exception.EntityNotFoundException;
import com.example.rest.model.Client;
import com.example.rest.model.Order;
import com.example.rest.repository.DataBaseClientRepository;
import com.example.rest.repository.DatabaseOrderRepository;
import com.example.rest.service.ClientService;
import com.example.rest.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataBaseClientService implements ClientService {

    private final DataBaseClientRepository clientRepository;

    private final DatabaseOrderRepository orderRepository;

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        MessageFormat.format("Клиент с ID {0} не найден", id)));
    }

    @Override
    public Client save(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public Client update(Client client) {
        Client existedClient = findById(client.getId());
        BeanUtils.copyNonNullProperties(client, existedClient);

        return clientRepository.save(client);
    }

    @Override
    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }

    @Override
    public Client saveWithOrders(Client client, List<Order> orders) {
        Client savedClient = clientRepository.save(client);

        for (Order order : orders) {
            order.setClient(savedClient);
            var savedOrder = orderRepository.save(order);
            savedClient.addOrder(savedOrder);
        }

        return savedClient;
    }
}

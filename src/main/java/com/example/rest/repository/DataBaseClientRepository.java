package com.example.rest.repository;

import com.example.rest.model.Client;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DataBaseClientRepository extends JpaRepository<Client, Long> {

    @Override
    @EntityGraph(attributePaths = {"orders"})
    List<Client> findAll();
}

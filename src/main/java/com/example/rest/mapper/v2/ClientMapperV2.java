package com.example.rest.mapper.v2;

import com.example.rest.model.Client;
import com.example.rest.web.model.ClientListResponse;
import com.example.rest.web.model.ClientResponse;
import com.example.rest.web.model.UpsertClientRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {OrderMapperV2.class})
public interface ClientMapperV2 {

    Client requestToCLient(UpsertClientRequest request);

    @Mapping(source = "clientId", target = "id")
    Client requestToClient(Long clientId, UpsertClientRequest request);

    ClientResponse clientToResponse(Client client);

    default ClientListResponse clientListToClientResponseList(List<Client> clients) {
        ClientListResponse response = new ClientListResponse();

        response.setClients(clients.stream()
                .map(this::clientToResponse)
                .toList());

        return response;
    }
}

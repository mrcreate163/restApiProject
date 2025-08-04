package com.example.rest.web.controller.v1;

import com.example.rest.mapper.v1.ClientMapper;
import com.example.rest.model.Client;
import com.example.rest.service.ClientService;
import com.example.rest.web.model.ClientListResponse;
import com.example.rest.web.model.ClientResponse;
import com.example.rest.web.model.ErrorResponse;
import com.example.rest.web.model.UpsertClientRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
@Tag(name = "Client V1", description = "Client API version V1")
public class ClientController {

    private final ClientService clientServiceImpl;
    private final ClientMapper clientMapper;

    @Operation(
            summary = "Get Clients",
            description = "Get all Clients",
            tags = {"client"}
    )
    @GetMapping
    public ResponseEntity<ClientListResponse> findAll() {
        return ResponseEntity.ok(
                clientMapper.clientListToClientResponse(clientServiceImpl.findAll())
        );
    }


    @Operation(
            summary = "Get Client by ID",
            description = "Get Client by ID, return id, name and list of orders",
            tags = {"client", "id"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = ClientResponse.class), mediaType = "application/json")
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")
            )}
    )
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                clientMapper.clientToResponse(clientServiceImpl.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ClientResponse> create(@RequestBody @Valid UpsertClientRequest request) {
        Client newClient = clientServiceImpl.save(clientMapper.requestToClient(request));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clientMapper.clientToResponse(newClient));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> update(@PathVariable("id") Long clientId,
                                                 @RequestBody UpsertClientRequest request) {
        Client updatedClient = clientServiceImpl.update(clientMapper.requestToClient(clientId, request));

        return ResponseEntity.ok(clientMapper.clientToResponse(updatedClient));
    }

    @Operation(
            summary = "Delete Client by ID",
            description = "Delete Client by ID",
            tags = {"client", "id"}
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientServiceImpl.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}

package com.example.rest.web.controller.v1;

import com.example.rest.exception.EntityNotFoundException;
import com.example.rest.mapper.v1.ClientMapper;
import com.example.rest.mapper.v1.OrderMapper;
import com.example.rest.model.Client;
import com.example.rest.model.Order;
import com.example.rest.service.ClientService;
import com.example.rest.service.OrderService;
import com.example.rest.web.AbstractTestController;
import com.example.rest.web.StringTestUtils;
import com.example.rest.web.model.*;
import net.bytebuddy.utility.RandomString;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ClientControllerTest extends AbstractTestController {

    @MockBean
    private ClientService clientService;

    @MockBean
    private ClientMapper clientMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderMapper orderMapper;

    @Test
    public void whenFindAll_thenReturnAllClients() throws Exception {
        List<Client> clients = new ArrayList<>();
        clients.add(createdClient(1L, null));
        Order order = createOrder(1L, 100L, null);
        clients.add(createdClient(2L, order));

        List<ClientResponse> clientResponses = new ArrayList<>();
        clientResponses.add(createClientResponse(1L, null));
        OrderResponse orderResponse = createOrderResponse(1L, 100L);

        clientResponses.add(createClientResponse(2L, orderResponse));

        ClientListResponse clientListResponse = new ClientListResponse(clientResponses);

        Mockito.when(clientService.findAll()).thenReturn(clients);

        Mockito.when(clientMapper.clientListToClientResponse(clients)).thenReturn(clientListResponse);

        String actualResponse = mockMvc.perform(get("/api/v1/client"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = StringTestUtils.readStringFromResource("response/find_all_clients_response.json");

        Mockito.verify(clientService, Mockito.times(1)).findAll();
        Mockito.verify(clientMapper, Mockito.times(1)).clientListToClientResponse(clients);

        JsonAssertions.assertThatJson(actualResponse).isEqualTo(expectedResponse);


    }

    @Test
    public void whenGetClientById_thenReturnClientById() throws Exception {
        Client client = createdClient(1L, null);
        ClientResponse clientResponse = createClientResponse(1L, null);
        Mockito.when(clientService.findById(1L)).thenReturn(client);
        Mockito.when(clientMapper.clientToResponse(client)).thenReturn(clientResponse);

        String actualResponse = mockMvc.perform(get("/api/v1/client/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = StringTestUtils.readStringFromResource("response/find_client_by_id_response.json");

        Mockito.verify(clientService, Mockito.times(1)).findById(1L);
        Mockito.verify(clientMapper, Mockito.times(1)).clientToResponse(client);

        JsonAssertions.assertThatJson(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    public void whenCreateClient_thenReturnNewClient() throws Exception {
        Client client = new Client();
        client.setName("Client 1");
        Client createdClient = createdClient(1L, null);
        ClientResponse clientResponse = createClientResponse(1L, null);
        UpsertClientRequest request = new UpsertClientRequest("Client 1");

        Mockito.when(clientService.save(client)).thenReturn(createdClient);
        Mockito.when(clientMapper.requestToClient(request)).thenReturn(client);
        Mockito.when(clientMapper.clientToResponse(createdClient)).thenReturn(clientResponse);

        String actualResponse = mockMvc.perform(post("/api/v1/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = StringTestUtils.readStringFromResource("response/create_client_response.json");

        Mockito.verify(clientService, Mockito.times(1)).save(client);
        Mockito.verify(clientMapper, Mockito.times(1)).requestToClient(request);
        Mockito.verify(clientMapper, Mockito.times(1)).clientToResponse(createdClient);
    }

    @Test
    public void whenUpdateClient_thenReturnUpdatedClient() throws Exception {
        UpsertClientRequest upsertClientRequest = new UpsertClientRequest("New Client 1");
        Client updatedClient = new Client(1L, "New Client 1", new ArrayList<>());
        ClientResponse clientResponse = new ClientResponse(1L, "New Client 1", new ArrayList<>());

        Mockito.when(clientService.update(updatedClient)).thenReturn(updatedClient);
        Mockito.when(clientMapper.requestToClient(1L, upsertClientRequest)).thenReturn(updatedClient);
        Mockito.when(clientMapper.clientToResponse(updatedClient)).thenReturn(clientResponse);

        String actualResponse = mockMvc.perform(put("/api/v1/client/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(upsertClientRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectResponse = StringTestUtils.readStringFromResource("response/update_client_response.json");

        Mockito.verify(clientService, Mockito.times(1)).update(updatedClient);
        Mockito.verify(clientMapper, Mockito.times(1)).requestToClient(1L, upsertClientRequest);
        Mockito.verify(clientMapper, Mockito.times(1)).clientToResponse(updatedClient);

        JsonAssertions.assertThatJson(actualResponse).isEqualTo(expectResponse);
    }

    @Test
    public void whenDeleteClientById_thenReturnStatusNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/client/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(clientService, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void whenFindByIdExistedClient_thenReturnError() throws Exception {
        Mockito.when(clientService.findById(500L)).thenThrow(new EntityNotFoundException("Клиент с ID 500 не найден!"));
        var response = mockMvc.perform(get("/api/v1/client/500"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        response.setCharacterEncoding("UTF-8");

        String actualResponse = response.getContentAsString();
        String expectedResponse = StringTestUtils.readStringFromResource("response/client_by_id_not_found_response.json");

         Mockito.verify(clientService, Mockito.times(1)).findById(500L);

         JsonAssertions.assertThatJson(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    public void whenCreateClientWithEmptyName_thenReturnError() throws Exception {
        var response = mockMvc.perform(post("/api/v1/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UpsertClientRequest())))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();
        response.setCharacterEncoding("UTF-8");

        String actualResponse = response.getContentAsString();
        String expectedResponse = StringTestUtils.readStringFromResource("response/empty_client_name_response.json");

        JsonAssertions.assertThatJson(actualResponse).isEqualTo(expectedResponse);
    }

    @ParameterizedTest
    @MethodSource("invalidSizeName")
    public void whenCreateClientWithInvalidSizeName_thenReturnError(String name) throws Exception {
        var response = mockMvc.perform(post("/api/v1/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UpsertClientRequest(name))))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();
        response.setCharacterEncoding("UTF-8");

        String actualResponse = response.getContentAsString();
        String expectedResponse = StringTestUtils.readStringFromResource("response/client_name_size_exception_response.json");

        JsonAssertions.assertThatJson(actualResponse).isEqualTo(expectedResponse);
    }

    private static Stream<Arguments> invalidSizeName() {
        return Stream.of(
                Arguments.of(RandomString.make(2)),
                Arguments.of(RandomString.make(31)));

    }

    @Test
    public void whenCreateOrder_returnNewOrder()throws Exception {
        Client client = createdClient(1L, null);
        Order order = createOrder(null, 100L, client);
        Order createdOrder = createOrder(1L, 100L, client);
        OrderResponse orderResponse = createOrderResponse(1L, 100L);
        UpsertOrderRequest request = new UpsertOrderRequest(1L, "Test product 1", BigDecimal.valueOf(100L));

        Mockito.when(clientService.findById(1L)).thenReturn(client);
        Mockito.when(orderService.save(order)).thenReturn(createdOrder);
        Mockito.when(orderMapper.orderToResponse(createdOrder)).thenReturn(orderResponse);
        Mockito.when(orderMapper.requestToOrder(request)).thenReturn(order);

        String actualResponse = mockMvc.perform(post("/api/v1/order").
                contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = StringTestUtils.readStringFromResource("response/create_order_response.json");
        JsonAssertions.assertThatJson(actualResponse).isEqualTo(expectedResponse);

        Mockito.verify(orderService, Mockito.times(1)).save(order);
        Mockito.verify(orderMapper, Mockito.times(1)).requestToOrder(request);
        Mockito.verify(orderMapper, Mockito.times(1)).orderToResponse(createdOrder);
    }
}

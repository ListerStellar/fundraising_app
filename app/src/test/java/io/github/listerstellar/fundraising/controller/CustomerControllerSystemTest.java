package io.github.listerstellar.fundraising.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.listerstellar.fundraising.dto.CustomerRequestDto;
import io.github.listerstellar.fundraising.entity.Customer;
import io.github.listerstellar.fundraising.entity.Fundraising;
import io.github.listerstellar.fundraising.repository.CustomerRepository;
import io.github.listerstellar.fundraising.repository.FundraisingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerSystemTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private FundraisingRepository fundraisingRepository;

    @BeforeEach
    void setUp() {
        fundraisingRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/customers creates customer, returns 201 Created and Location header")
    void testCreateCustomer_returns201WithLocation() throws Exception {
        CustomerRequestDto request = new CustomerRequestDto(
                "mark_smith",
                "mark@example.com",
                "Mark",
                "Smith",
                "+1234567890"
        );

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/api/customers/")))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username").value("mark_smith"))
                .andExpect(jsonPath("$.email").value("mark@example.com"));
    }

    @Test
    @DisplayName("POST /api/customers with duplicate username returns 409 Conflict")
    void testCreateCustomer_duplicateUsername_returns409() throws Exception {
        Customer existing = customerRepository.save(Customer.builder()
                .username("duplicate_user")
                .email("first@example.com")
                .build());

        CustomerRequestDto duplicateRequest = new CustomerRequestDto(
                "duplicate_user",
                "second@example.com",
                "John",
                "Doe",
                "+12345"
        );

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail", containsString("duplicate_user")));
    }

    @Test
    @DisplayName("POST /api/customers with invalid email returns 400 Bad Request")
    void testCreateCustomer_invalidEmail_returns400() throws Exception {
        CustomerRequestDto invalidRequest = new CustomerRequestDto(
                "user1",
                "not-an-email",
                "First",
                "Last",
                "+12345"
        );

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    @DisplayName("GET /api/customers/{id} with non-existent ID returns 404 Not Found")
    void testGetCustomerById_notFound_returns404() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/customers/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("DELETE /api/customers/{id} deletes customer and returns 204 No Content")
    void testDeleteCustomer_returns204() throws Exception {
        Customer customer = customerRepository.save(Customer.builder()
                .username("to_delete")
                .email("delete@example.com")
                .build());

        mockMvc.perform(delete("/api/customers/{id}", customer.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/customers/{id}", customer.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/customers/{id}/favourites/{fundraisingId} adds favourite and returns 204")
    void testAddAndRemoveFavourite_returns204() throws Exception {
        Customer customer = customerRepository.save(Customer.builder()
                .username("fan")
                .email("fan@example.com")
                .build());

        Fundraising campaign = fundraisingRepository.save(Fundraising.builder()
                .name("Wildlife Sanctuary")
                .goal(new BigDecimal("1000.00"))
                .creator(customer)
                .build());

        // Add favourite
        mockMvc.perform(post("/api/customers/{id}/favourites/{fundraisingId}", customer.getId(), campaign.getId()))
                .andExpect(status().isNoContent());

        // Check favourite exists
        mockMvc.perform(get("/api/customers/{id}/favourites", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Wildlife Sanctuary"));

        // Remove favourite
        mockMvc.perform(delete("/api/customers/{id}/favourites/{fundraisingId}", customer.getId(), campaign.getId()))
                .andExpect(status().isNoContent());

        // Check empty favourites
        mockMvc.perform(get("/api/customers/{id}/favourites", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}

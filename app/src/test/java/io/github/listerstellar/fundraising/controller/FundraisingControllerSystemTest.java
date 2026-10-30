package io.github.listerstellar.fundraising.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.listerstellar.fundraising.dto.FundraisingRequestDto;
import io.github.listerstellar.fundraising.entity.Customer;
import io.github.listerstellar.fundraising.entity.Donation;
import io.github.listerstellar.fundraising.entity.Fundraising;
import io.github.listerstellar.fundraising.repository.CustomerRepository;
import io.github.listerstellar.fundraising.repository.DonationRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FundraisingControllerSystemTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private FundraisingRepository fundraisingRepository;

    @Autowired
    private DonationRepository donationRepository;

    @BeforeEach
    void setUp() {
        donationRepository.deleteAll();
        fundraisingRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/fundraisings creates campaign and returns 201 Created with Location header")
    void testCreateFundraising_returns201WithLocation() throws Exception {
        Customer creator = customerRepository.save(Customer.builder()
                .username("creator")
                .email("creator@example.com")
                .build());

        FundraisingRequestDto request = new FundraisingRequestDto(
                "School Books Drive",
                new BigDecimal("5000.00"),
                "Buying textbooks for local schools",
                creator.getId()
        );

        mockMvc.perform(post("/api/fundraisings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/api/fundraisings/")))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("School Books Drive"))
                .andExpect(jsonPath("$.goal").value(5000.00))
                .andExpect(jsonPath("$.creatorId").value(creator.getId().toString()));
    }

    @Test
    @DisplayName("GET /api/fundraisings/{id}/top-donors returns ranked top donors via HTTP")
    void testGetTopDonors_returnsRankedList() throws Exception {
        Customer creator = customerRepository.save(Customer.builder()
                .username("organizer")
                .email("organizer@example.com")
                .build());

        Fundraising campaign = fundraisingRepository.save(Fundraising.builder()
                .name("Cancer Research")
                .goal(new BigDecimal("50000.00"))
                .creator(creator)
                .build());

        Customer donor1 = customerRepository.save(Customer.builder()
                .username("top_donor")
                .email("top@example.com")
                .firstName("Elon")
                .lastName("Musk")
                .build());

        Customer donor2 = customerRepository.save(Customer.builder()
                .username("regular_donor")
                .email("regular@example.com")
                .firstName("Bill")
                .lastName("Gates")
                .build());

        // donor1 gives 1000.00
        donationRepository.save(Donation.builder()
                .amount(new BigDecimal("1000.00"))
                .customer(donor1)
                .fundraising(campaign)
                .build());

        // donor2 gives 250.00
        donationRepository.save(Donation.builder()
                .amount(new BigDecimal("250.00"))
                .customer(donor2)
                .fundraising(campaign)
                .build());

        mockMvc.perform(get("/api/fundraisings/{id}/top-donors", campaign.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value("top_donor"))
                .andExpect(jsonPath("$[0].totalDonated").value(1000.00))
                .andExpect(jsonPath("$[1].username").value("regular_donor"))
                .andExpect(jsonPath("$[1].totalDonated").value(250.00));
    }
}

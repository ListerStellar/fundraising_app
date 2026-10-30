package io.github.listerstellar.fundraising.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.listerstellar.fundraising.dto.DonationRequestDto;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DonationControllerSystemTest {

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
    @DisplayName("POST /api/donations creates donation and returns 201 Created with Location header")
    void testCreateDonation_returns201WithLocation() throws Exception {
        Customer donor = customerRepository.save(Customer.builder().username("generous_donor").email("gen@mail.com").build());
        Customer creator = customerRepository.save(Customer.builder().username("creator").email("cr@mail.com").build());
        Fundraising campaign = fundraisingRepository.save(Fundraising.builder().name("Disaster Relief").goal(new BigDecimal("10000.00")).creator(creator).build());

        DonationRequestDto request = new DonationRequestDto(
                new BigDecimal("200.00"),
                "Keep going!",
                donor.getId(),
                campaign.getId()
        );

        mockMvc.perform(post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/api/donations/")))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(200.00))
                .andExpect(jsonPath("$.customerId").value(donor.getId().toString()))
                .andExpect(jsonPath("$.fundraisingId").value(campaign.getId().toString()));
    }

    @Test
    @DisplayName("GET /api/donations filtered by customerId returns matching donations")
    void testGetDonations_filteredByCustomer() throws Exception {
        Customer donor = customerRepository.save(Customer.builder().username("donor1").email("d1@mail.com").build());
        Customer creator = customerRepository.save(Customer.builder().username("creator").email("cr@mail.com").build());
        Fundraising campaign = fundraisingRepository.save(Fundraising.builder().name("Earthquake").goal(new BigDecimal("5000.00")).creator(creator).build());

        donationRepository.save(Donation.builder()
                .amount(new BigDecimal("75.00"))
                .customer(donor)
                .fundraising(campaign)
                .build());

        mockMvc.perform(get("/api/donations").param("customerId", donor.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].amount").value(75.00));
    }
}

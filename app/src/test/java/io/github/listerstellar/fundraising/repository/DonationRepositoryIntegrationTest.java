package io.github.listerstellar.fundraising.repository;

import io.github.listerstellar.fundraising.entity.Customer;
import io.github.listerstellar.fundraising.entity.Donation;
import io.github.listerstellar.fundraising.entity.Fundraising;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DonationRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DonationRepository donationRepository;

    @Test
    @DisplayName("Find donations by customer ID and fundraising ID")
    void testFindDonations() {
        Customer donor = entityManager.persistAndFlush(Customer.builder().username("donor").email("donor@mail.com").build());
        Customer creator = entityManager.persistAndFlush(Customer.builder().username("creator").email("creator@mail.com").build());
        Fundraising campaign = entityManager.persistAndFlush(Fundraising.builder().name("Help").goal(new BigDecimal("1000")).creator(creator).build());

        Donation donation = entityManager.persistAndFlush(
                Donation.builder().amount(new BigDecimal("100.00")).customer(donor).fundraising(campaign).build()
        );

        List<Donation> byCustomer = donationRepository.findAllByCustomerId(donor.getId());
        assertThat(byCustomer).hasSize(1);
        assertThat(byCustomer.getFirst().getId()).isEqualTo(donation.getId());

        List<Donation> byCampaign = donationRepository.findAllByFundraisingId(campaign.getId());
        assertThat(byCampaign).hasSize(1);

        List<Donation> byBoth = donationRepository.findAllByCustomerIdAndFundraisingId(donor.getId(), campaign.getId());
        assertThat(byBoth).hasSize(1);
    }
}

package io.github.listerstellar.fundraising.repository;

import io.github.listerstellar.fundraising.dto.TopDonorDto;
import io.github.listerstellar.fundraising.entity.Customer;
import io.github.listerstellar.fundraising.entity.Donation;
import io.github.listerstellar.fundraising.entity.Fundraising;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FundraisingRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private FundraisingRepository fundraisingRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Test
    @DisplayName("Persist and find Fundraising entity with ManyToOne creator relationship")
    void testSaveAndFindFundraising() {
        Customer creator = Customer.builder()
                .username("creator1")
                .email("creator1@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();
        creator = entityManager.persistAndFlush(creator);

        Fundraising campaign = Fundraising.builder()
                .name("Save the Forest")
                .goal(new BigDecimal("10000.00"))
                .description("Planting trees")
                .creator(creator)
                .build();
        campaign = entityManager.persistAndFlush(campaign);

        var found = fundraisingRepository.findById(campaign.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Save the Forest");
        assertThat(found.get().getCreator().getUsername()).isEqualTo("creator1");
    }

    @Test
    @DisplayName("Verify ManyToMany favourites relationship between Customer and Fundraising")
    void testManyToManyFavouritesRelation() {
        Customer customer = entityManager.persistAndFlush(Customer.builder()
                .username("supporter")
                .email("supporter@example.com")
                .build());

        Customer creator = entityManager.persistAndFlush(Customer.builder()
                .username("organizer")
                .email("organizer@example.com")
                .build());

        Fundraising campaign = entityManager.persistAndFlush(Fundraising.builder()
                .name("Animal Shelter")
                .goal(new BigDecimal("5000.00"))
                .creator(creator)
                .build());

        customer.addFavouriteFundraising(campaign);
        entityManager.persistAndFlush(customer);
        entityManager.clear();

        Customer reloadedCustomer = customerRepository.findById(customer.getId()).orElseThrow();
        assertThat(reloadedCustomer.getFavouriteFundraisings()).hasSize(1);
        assertThat(reloadedCustomer.getFavouriteFundraisings().iterator().next().getName()).isEqualTo("Animal Shelter");
    }

    @Test
    @DisplayName("Complex JPQL Query: findTopDonorsByFundraisingId groups and ranks donors by total amount")
    void testFindTopDonorsByFundraisingId_JPQL() {
        Customer creator = entityManager.persistAndFlush(Customer.builder()
                .username("org")
                .email("org@example.com")
                .build());

        Fundraising targetCampaign = entityManager.persistAndFlush(Fundraising.builder()
                .name("Clean Water Initiative")
                .goal(new BigDecimal("20000.00"))
                .creator(creator)
                .build());

        Fundraising otherCampaign = entityManager.persistAndFlush(Fundraising.builder()
                .name("Other Campaign")
                .goal(new BigDecimal("5000.00"))
                .creator(creator)
                .build());

        Customer alice = entityManager.persistAndFlush(Customer.builder()
                .username("alice")
                .email("alice@example.com")
                .firstName("Alice")
                .lastName("Wonder")
                .build());

        Customer bob = entityManager.persistAndFlush(Customer.builder()
                .username("bob")
                .email("bob@example.com")
                .firstName("Bob")
                .lastName("Builder")
                .build());

        Customer charlie = entityManager.persistAndFlush(Customer.builder()
                .username("charlie")
                .email("charlie@example.com")
                .firstName("Charlie")
                .lastName("Brown")
                .build());

        // Alice donates twice to targetCampaign: 100.00 + 250.00 = 350.00
        entityManager.persist(Donation.builder().amount(new BigDecimal("100.00")).customer(alice).fundraising(targetCampaign).build());
        entityManager.persist(Donation.builder().amount(new BigDecimal("250.00")).customer(alice).fundraising(targetCampaign).build());

        // Bob donates once to targetCampaign: 500.00
        entityManager.persist(Donation.builder().amount(new BigDecimal("500.00")).customer(bob).fundraising(targetCampaign).build());

        // Charlie donates 1000.00 to otherCampaign (should not appear in targetCampaign's top donors!)
        entityManager.persist(Donation.builder().amount(new BigDecimal("1000.00")).customer(charlie).fundraising(otherCampaign).build());

        entityManager.flush();
        entityManager.clear();

        List<TopDonorDto> topDonors = donationRepository.findTopDonorsByFundraisingId(
                targetCampaign.getId(), PageRequest.of(0, 5)
        );

        assertThat(topDonors).hasSize(2);

        // 1st place: Bob with 500.00
        TopDonorDto rank1 = topDonors.get(0);
        assertThat(rank1.username()).isEqualTo("bob");
        assertThat(rank1.totalDonated()).isEqualByComparingTo(new BigDecimal("500.00"));

        // 2nd place: Alice with 350.00 (sum of two donations)
        TopDonorDto rank2 = topDonors.get(1);
        assertThat(rank2.username()).isEqualTo("alice");
        assertThat(rank2.totalDonated()).isEqualByComparingTo(new BigDecimal("350.00"));
    }
}

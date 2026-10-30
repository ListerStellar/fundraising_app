package io.github.listerstellar.fundraising.repository;

import io.github.listerstellar.fundraising.entity.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("Check existsByUsername and existsByEmail")
    void testExistsByUsernameAndEmail() {
        Customer customer = Customer.builder()
                .username("uniqueuser")
                .email("user@mail.com")
                .firstName("Test")
                .lastName("User")
                .build();
        entityManager.persistAndFlush(customer);

        assertThat(customerRepository.existsByUsername("uniqueuser")).isTrue();
        assertThat(customerRepository.existsByUsername("nonexistent")).isFalse();

        assertThat(customerRepository.existsByEmail("user@mail.com")).isTrue();
        assertThat(customerRepository.existsByEmail("other@mail.com")).isFalse();
    }

    @Test
    @DisplayName("Find customer by username and email")
    void testFindByUsernameAndEmail() {
        Customer customer = Customer.builder()
                .username("findme")
                .email("findme@mail.com")
                .build();
        entityManager.persistAndFlush(customer);

        var byUsername = customerRepository.getCustomerByUsername("findme");
        assertThat(byUsername).isPresent();
        assertThat(byUsername.get().getEmail()).isEqualTo("findme@mail.com");

        var byEmail = customerRepository.getCustomerByEmail("findme@mail.com");
        assertThat(byEmail).isPresent();
        assertThat(byEmail.get().getUsername()).isEqualTo("findme");
    }
}

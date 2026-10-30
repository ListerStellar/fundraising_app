package io.github.listerstellar.fundraising.repository;

import io.github.listerstellar.fundraising.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> getCustomerByUsername(String username);

    Optional<Customer> getCustomerByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}

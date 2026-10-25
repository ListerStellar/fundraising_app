package io.github.listerstellar.fundrising.repository;

import com.fasterxml.jackson.databind.introspect.AnnotationCollector;
import io.github.listerstellar.fundrising.entity.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> getCustomerByUsername(String username);

    Optional<Customer> getCustomerByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}

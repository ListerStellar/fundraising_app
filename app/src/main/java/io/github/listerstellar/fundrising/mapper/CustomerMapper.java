package io.github.listerstellar.fundrising.mapper;

import io.github.listerstellar.fundrising.dto.CustomerRequestDto;
import io.github.listerstellar.fundrising.dto.CustomerResponseDto;
import io.github.listerstellar.fundrising.entity.Customer;
import io.github.listerstellar.fundrising.entity.Fundraising;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {

    public CustomerResponseDto toDto(Customer customer) {
        if (customer == null) { return null; }

        Set<UUID> favouriteFundraisings = customer.getFavouriteFundraisings().isEmpty()
                ? new HashSet<>()
                : customer.getFavouriteFundraisings().stream()
                        .map(Fundraising::getId)
                        .collect(Collectors.toSet());

        return new CustomerResponseDto(
                customer.getId(),
                customer.getUsername(),
                customer.getEmail(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhoneNumber(),
                favouriteFundraisings
        );
    }

    public Customer toEntity(CustomerRequestDto customerRequestDto) {
        if (customerRequestDto == null) { return null; }
        return Customer.builder()
                .username(customerRequestDto.username())
                .email(customerRequestDto.email())
                .firstName(customerRequestDto.firstName())
                .lastName(customerRequestDto.lastName())
                .phoneNumber(customerRequestDto.phoneNumber())
                .build();
    }
}

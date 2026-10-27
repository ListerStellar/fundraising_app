package io.github.listerstellar.fundrising.service;

import io.github.listerstellar.fundrising.dto.CustomerRequestDto;
import io.github.listerstellar.fundrising.dto.CustomerResponseDto;
import io.github.listerstellar.fundrising.dto.FundraisingResponseDto;
import io.github.listerstellar.fundrising.entity.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    List<CustomerResponseDto> getCustomers();

    Customer getCustomerEntityById(UUID customerId);

    CustomerResponseDto getCustomerById(UUID customerId);

    CustomerResponseDto getCustomerByUsername(String username);

    CustomerResponseDto getCustomerByEmail(String email);

    CustomerResponseDto createCustomer(CustomerRequestDto customerRequestDto);

    CustomerResponseDto updateCustomer(UUID customerId, CustomerRequestDto customerRequestDto);

    CustomerResponseDto partialUpdateCustomer(UUID customerId, CustomerRequestDto customerRequestDto);

    void deleteCustomerById(UUID customerId);

    List<FundraisingResponseDto> getFavouriteFundraisingsById(UUID customerId);

    void addFavouriteFundraising(UUID customerId, UUID fundraisingId);

    void removeFavouriteFundraising(UUID customerId, UUID fundraisingId);
}

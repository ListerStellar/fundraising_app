package io.github.listerstellar.fundraising.service;

import io.github.listerstellar.fundraising.dto.CustomerRequestDto;
import io.github.listerstellar.fundraising.dto.CustomerResponseDto;
import io.github.listerstellar.fundraising.entity.Customer;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing customer (user) accounts.
 */
public interface CustomerService {

    /**
     * Retrieves all registered customers.
     *
     * @return list of customer response DTOs
     */
    List<CustomerResponseDto> getCustomers();

    /**
     * Retrieves a customer entity by unique identifier.
     *
     * @param customerId UUID of the customer
     * @return Customer entity
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if customer is not found
     */
    Customer getCustomerEntityById(UUID customerId);

    /**
     * Retrieves a customer by unique identifier.
     *
     * @param customerId UUID of the customer
     * @return customer response DTO
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if customer is not found
     */
    CustomerResponseDto getCustomerById(UUID customerId);

    /**
     * Retrieves a customer by username.
     *
     * @param username unique username
     * @return customer response DTO
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if customer is not found
     */
    CustomerResponseDto getCustomerByUsername(String username);

    /**
     * Retrieves a customer by email.
     *
     * @param email unique email address
     * @return customer response DTO
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if customer is not found
     */
    CustomerResponseDto getCustomerByEmail(String email);

    /**
     * Creates a new customer account.
     *
     * @param customerRequestDto customer registration data
     * @return created customer response DTO
     * @throws io.github.listerstellar.fundraising.exception.CustomerAlreadyExistsException if username or email is already taken
     */
    CustomerResponseDto createCustomer(CustomerRequestDto customerRequestDto);

    /**
     * Fully updates an existing customer profile.
     *
     * @param customerId UUID of the customer to update
     * @param customerRequestDto new profile data
     * @return updated customer response DTO
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if customer is not found
     * @throws io.github.listerstellar.fundraising.exception.CustomerAlreadyExistsException if new username/email is taken by another customer
     */
    CustomerResponseDto updateCustomer(UUID customerId, CustomerRequestDto customerRequestDto);

    /**
     * Partially updates an existing customer profile.
     *
     * @param customerId UUID of the customer to update
     * @param customerRequestDto partial profile data
     * @return updated customer response DTO
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if customer is not found
     * @throws io.github.listerstellar.fundraising.exception.CustomerAlreadyExistsException if new username/email is taken by another customer
     */
    CustomerResponseDto partialUpdateCustomer(UUID customerId, CustomerRequestDto customerRequestDto);

    /**
     * Deletes a customer by unique identifier.
     *
     * @param customerId UUID of the customer to delete
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if customer is not found
     */
    void deleteCustomerById(UUID customerId);
}

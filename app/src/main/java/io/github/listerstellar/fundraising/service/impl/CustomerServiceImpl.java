package io.github.listerstellar.fundraising.service.impl;

import io.github.listerstellar.fundraising.dto.CustomerRequestDto;
import io.github.listerstellar.fundraising.dto.CustomerResponseDto;
import io.github.listerstellar.fundraising.entity.Customer;
import io.github.listerstellar.fundraising.exception.CustomerAlreadyExistsException;
import io.github.listerstellar.fundraising.exception.EntityNotFoundException;
import io.github.listerstellar.fundraising.mapper.CustomerMapper;
import io.github.listerstellar.fundraising.repository.CustomerRepository;
import io.github.listerstellar.fundraising.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerEntityById(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer", customerId.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerById(UUID customerId) {
        return customerMapper.toDto(getCustomerEntityById(customerId));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerByUsername(String username) {
        return customerMapper.toDto(
                customerRepository.getCustomerByUsername(username)
                        .orElseThrow(() -> new EntityNotFoundException("Customer", username))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerByEmail(String email) {
        return customerMapper.toDto(
                customerRepository.getCustomerByEmail(email)
                        .orElseThrow(() -> new EntityNotFoundException("Customer", email))
        );
    }

    @Override
    public CustomerResponseDto createCustomer(CustomerRequestDto customerRequestDto) {
        if (customerRepository.existsByUsername(customerRequestDto.username())) {
            throw new CustomerAlreadyExistsException(customerRequestDto.username());
        }
        return customerMapper.toDto(customerRepository.save(customerMapper.toEntity(customerRequestDto)));
    }

    @Override
    public CustomerResponseDto updateCustomer(UUID customerId, CustomerRequestDto customerRequestDto) {
        Customer customer = getCustomerEntityById(customerId);

        if (!customer.getUsername().equals(customerRequestDto.username())
                && customerRepository.existsByUsername(customerRequestDto.username())) {
            throw new CustomerAlreadyExistsException(customerRequestDto.username());
        }
        if (!customer.getEmail().equals(customerRequestDto.email())
                && customerRepository.existsByEmail(customerRequestDto.email())) {
            throw new CustomerAlreadyExistsException(customerRequestDto.email());
        }

        customer.setUsername(customerRequestDto.username());
        customer.setEmail(customerRequestDto.email());
        customer.setFirstName(customerRequestDto.firstName());
        customer.setLastName(customerRequestDto.lastName());
        customer.setPhoneNumber(customerRequestDto.phoneNumber());

        return customerMapper.toDto(customerRepository.save(customer));
    }

    @Override
    public CustomerResponseDto partialUpdateCustomer(UUID customerId, CustomerRequestDto customerRequestDto) {
        Customer customer = getCustomerEntityById(customerId);

        if (customerRequestDto.username() != null
                && !customer.getUsername().equals(customerRequestDto.username())
                && customerRepository.existsByUsername(customerRequestDto.username())) {
            throw new CustomerAlreadyExistsException(customerRequestDto.username());
        }
        if (customerRequestDto.email() != null
                && !customer.getEmail().equals(customerRequestDto.email())
                && customerRepository.existsByEmail(customerRequestDto.email())) {
            throw new CustomerAlreadyExistsException(customerRequestDto.email());
        }

        if (customerRequestDto.username() != null) customer.setUsername(customerRequestDto.username());
        if (customerRequestDto.email() != null) customer.setEmail(customerRequestDto.email());
        if (customerRequestDto.firstName() != null) customer.setFirstName(customerRequestDto.firstName());
        if (customerRequestDto.lastName() != null) customer.setLastName(customerRequestDto.lastName());
        if (customerRequestDto.phoneNumber() != null) customer.setPhoneNumber(customerRequestDto.phoneNumber());

        return customerMapper.toDto(customerRepository.save(customer));
    }

    @Override
    public void deleteCustomerById(UUID customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException("Customer", customerId.toString());
        }

        customerRepository.deleteById(customerId);
    }
}

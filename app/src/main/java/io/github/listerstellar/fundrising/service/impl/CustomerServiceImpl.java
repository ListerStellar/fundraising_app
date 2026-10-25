package io.github.listerstellar.fundrising.service.impl;

import io.github.listerstellar.fundrising.dto.CustomerRequestDto;
import io.github.listerstellar.fundrising.dto.CustomerResponseDto;
import io.github.listerstellar.fundrising.entity.Customer;
import io.github.listerstellar.fundrising.entity.Fundraising;
import io.github.listerstellar.fundrising.exception.CustomerAlreadyExistsException;
import io.github.listerstellar.fundrising.exception.EntityNotFoundException;
import io.github.listerstellar.fundrising.mapper.CustomerMapper;
import io.github.listerstellar.fundrising.repository.CustomerRepository;
import io.github.listerstellar.fundrising.service.CustomerService;
import io.github.listerstellar.fundrising.service.FundraisingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    private final FundraisingService fundraisingService;

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
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer", customerId.toString()));

        if (!customer.getUsername().equals(customerRequestDto.username())
                && customerRepository.existsByUsername(customerRequestDto.username())) {
            throw new CustomerAlreadyExistsException(customerRequestDto.username());
        }
        if (!customer.getEmail().equals(customerRequestDto.email())
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

    @Override
    public void addFavouriteFundraising(UUID customerId, UUID fundraisingId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer", customerId.toString()));

        Fundraising fundraising = fundraisingService.getFundraisingEntityById(fundraisingId);

        customer.addFavouriteFundraising(fundraising);
    }

    @Override
    public void removeFavouriteFundraising(UUID customerId, UUID fundraisingId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer", customerId.toString()));

        Fundraising fundraising = fundraisingService.getFundraisingEntityById(fundraisingId);

        customer.removeFavouriteFundraising(fundraising);
    }
}

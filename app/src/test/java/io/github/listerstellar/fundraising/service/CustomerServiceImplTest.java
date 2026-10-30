package io.github.listerstellar.fundraising.service;

import io.github.listerstellar.fundraising.dto.CustomerRequestDto;
import io.github.listerstellar.fundraising.dto.CustomerResponseDto;
import io.github.listerstellar.fundraising.entity.Customer;
import io.github.listerstellar.fundraising.exception.CustomerAlreadyExistsException;
import io.github.listerstellar.fundraising.exception.EntityNotFoundException;
import io.github.listerstellar.fundraising.mapper.CustomerMapper;
import io.github.listerstellar.fundraising.repository.CustomerRepository;
import io.github.listerstellar.fundraising.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    @DisplayName("getCustomers returns mapped customer DTO list")
    void getCustomers_returnsList() {
        Customer customer = Customer.builder().id(UUID.randomUUID()).username("john").email("john@example.com").build();
        CustomerResponseDto dto = new CustomerResponseDto(customer.getId(), "john", "john@example.com", "John", "Doe", "+123456", Collections.emptySet());

        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(customerMapper.toDto(customer)).thenReturn(dto);

        List<CustomerResponseDto> result = customerService.getCustomers();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().username()).isEqualTo("john");
    }

    @Test
    @DisplayName("getCustomerById returns customer when found")
    void getCustomerById_found() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder().id(id).username("john").email("john@example.com").build();
        CustomerResponseDto dto = new CustomerResponseDto(id, "john", "john@example.com", "John", "Doe", "+123456", Collections.emptySet());

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerMapper.toDto(customer)).thenReturn(dto);

        CustomerResponseDto result = customerService.getCustomerById(id);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
    }

    @Test
    @DisplayName("getCustomerById throws EntityNotFoundException when not found")
    void getCustomerById_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    @DisplayName("createCustomer succeeds when username is unique")
    void createCustomer_success() {
        CustomerRequestDto requestDto = new CustomerRequestDto("john", "john@example.com", "John", "Doe", "+123456");
        Customer entity = Customer.builder().id(UUID.randomUUID()).username("john").email("john@example.com").build();
        CustomerResponseDto responseDto = new CustomerResponseDto(entity.getId(), "john", "john@example.com", "John", "Doe", "+123456", Set.of());

        when(customerRepository.existsByUsername("john")).thenReturn(false);
        when(customerMapper.toEntity(requestDto)).thenReturn(entity);
        when(customerRepository.save(entity)).thenReturn(entity);
        when(customerMapper.toDto(entity)).thenReturn(responseDto);

        CustomerResponseDto result = customerService.createCustomer(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("john");
        verify(customerRepository).save(entity);
    }

    @Test
    @DisplayName("createCustomer throws CustomerAlreadyExistsException when username already taken")
    void createCustomer_duplicateUsername_throwsException() {
        CustomerRequestDto requestDto = new CustomerRequestDto("john", "john@example.com", "John", "Doe", "+123456");
        when(customerRepository.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> customerService.createCustomer(requestDto))
                .isInstanceOf(CustomerAlreadyExistsException.class)
                .hasMessageContaining("john");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateCustomer throws CustomerAlreadyExistsException when email already taken by other")
    void updateCustomer_duplicateEmail_throwsException() {
        UUID id = UUID.randomUUID();
        Customer existing = Customer.builder().id(id).username("john").email("old@example.com").build();
        CustomerRequestDto updateDto = new CustomerRequestDto("john", "taken@example.com", "John", "Doe", "+123456");

        when(customerRepository.findById(id)).thenReturn(Optional.of(existing));
        when(customerRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.updateCustomer(id, updateDto))
                .isInstanceOf(CustomerAlreadyExistsException.class)
                .hasMessageContaining("taken@example.com");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteCustomerById deletes when customer exists")
    void deleteCustomerById_success() {
        UUID id = UUID.randomUUID();
        when(customerRepository.existsById(id)).thenReturn(true);

        customerService.deleteCustomerById(id);

        verify(customerRepository).deleteById(id);
    }

    @Test
    @DisplayName("deleteCustomerById throws EntityNotFoundException when customer does not exist")
    void deleteCustomerById_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(customerRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> customerService.deleteCustomerById(id))
                .isInstanceOf(EntityNotFoundException.class);

        verify(customerRepository, never()).deleteById(any());
    }
}


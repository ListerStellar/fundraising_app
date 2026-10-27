package io.github.listerstellar.fundrising.controller;

import io.github.listerstellar.fundrising.dto.CustomerRequestDto;
import io.github.listerstellar.fundrising.dto.CustomerResponseDto;
import io.github.listerstellar.fundrising.dto.FundraisingResponseDto;
import io.github.listerstellar.fundrising.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerResponseDto>> getCustomers() {
        return ResponseEntity.ok(customerService.getCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<CustomerResponseDto> searchCustomer(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email
    ) {
        if (username != null) {
            return ResponseEntity.ok(customerService.getCustomerByUsername(username));
        }
        if (email != null) {
            return ResponseEntity.ok(customerService.getCustomerByEmail(email));
        }
        throw new IllegalArgumentException("Either 'username' or 'email' must be provided");
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDto> updateCustomer(@Valid @RequestBody CustomerRequestDto customerRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(customerRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> updateCustomer(@PathVariable UUID id,
                                                              @Valid @RequestBody CustomerRequestDto customerRequestDto) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customerRequestDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> partialUpdateCustomer(@PathVariable UUID id,
                                                                     @RequestBody CustomerRequestDto customerRequestDto) {
        return ResponseEntity.ok(customerService.partialUpdateCustomer(id, customerRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomerById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/favourite")
    public ResponseEntity<List<FundraisingResponseDto>> getFavouriteFundraisingsById(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.getFavouriteFundraisingsById(id));
    }

    @PostMapping("/{id}/favourite/{fundraisingId}")
    public ResponseEntity<Void> addFavouriteFundraising(@PathVariable UUID id, @PathVariable UUID fundraisingId) {
        customerService.addFavouriteFundraising(id, fundraisingId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/favourite/{fundraisingId}")
    public ResponseEntity<Void> deleteFavouriteFundraising(@PathVariable UUID id, @PathVariable UUID fundraisingId) {
        customerService.removeFavouriteFundraising(id, fundraisingId);
        return ResponseEntity.noContent().build();
    }
}

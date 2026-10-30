package io.github.listerstellar.fundraising.controller;

import io.github.listerstellar.fundraising.dto.CustomerRequestDto;
import io.github.listerstellar.fundraising.dto.CustomerResponseDto;
import io.github.listerstellar.fundraising.dto.FundraisingResponseDto;
import io.github.listerstellar.fundraising.service.CustomerService;
import io.github.listerstellar.fundraising.service.FavouriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Operations for managing customer accounts and favourite campaigns")
public class CustomerController {

    private final CustomerService customerService;
    private final FavouriteService favouriteService;

    private URI createLocationUri(UUID id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieves a list of all registered customers")
    @ApiResponse(responseCode = "200", description = "List of customers retrieved successfully")
    public ResponseEntity<List<CustomerResponseDto>> getCustomers() {
        return ResponseEntity.ok(customerService.getCustomers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieves a customer by their unique UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerResponseDto> getCustomerById(
            @Parameter(description = "Customer UUID") @PathVariable UUID id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search customer by username or email", description = "Finds customer by unique username or email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "400", description = "Neither username nor email provided"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerResponseDto> searchCustomer(
            @Parameter(description = "Customer username") @RequestParam(required = false) String username,
            @Parameter(description = "Customer email") @RequestParam(required = false) String email
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
    @Operation(summary = "Create a customer", description = "Registers a new customer account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created successfully",
                    headers = @Header(name = "Location", description = "URI of the newly created customer")),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "409", description = "Username or email already exists")
    })
    public ResponseEntity<CustomerResponseDto> createCustomer(
            @Valid @RequestBody CustomerRequestDto customerRequestDto) {
        CustomerResponseDto created = customerService.createCustomer(customerRequestDto);
        return ResponseEntity.created(createLocationUri(created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Fully updates an existing customer profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "409", description = "Username or email conflict with another account")
    })
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            @Parameter(description = "Customer UUID") @PathVariable UUID id,
            @Valid @RequestBody CustomerRequestDto customerRequestDto) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customerRequestDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update customer", description = "Partially updates an existing customer profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer partially updated"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "409", description = "Username or email conflict with another account")
    })
    public ResponseEntity<CustomerResponseDto> partialUpdateCustomer(
            @Parameter(description = "Customer UUID") @PathVariable UUID id,
            @RequestBody CustomerRequestDto customerRequestDto) {
        return ResponseEntity.ok(customerService.partialUpdateCustomer(id, customerRequestDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Deletes a customer account by UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "409", description = "Customer cannot be deleted due to existing campaigns or donations")
    })
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer UUID") @PathVariable UUID id) {
        customerService.deleteCustomerById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/favourites")
    @Operation(summary = "Get favourite campaigns", description = "Retrieves all campaigns favourited by customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of favourite campaigns retrieved"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<List<FundraisingResponseDto>> getFavouriteFundraisingsById(
            @Parameter(description = "Customer UUID") @PathVariable UUID id) {
        return ResponseEntity.ok(favouriteService.getFavouriteFundraisingsByCustomerId(id));
    }

    @PostMapping("/{id}/favourites/{fundraisingId}")
    @Operation(summary = "Add campaign to favourites", description = "Adds a campaign to customer favourites (M:N link)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Campaign added to favourites"),
            @ApiResponse(responseCode = "404", description = "Customer or campaign not found")
    })
    public ResponseEntity<Void> addFavouriteFundraising(
            @Parameter(description = "Customer UUID") @PathVariable UUID id,
            @Parameter(description = "Fundraising campaign UUID") @PathVariable UUID fundraisingId) {
        favouriteService.addFavourite(id, fundraisingId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/favourites/{fundraisingId}")
    @Operation(summary = "Remove campaign from favourites", description = "Removes a campaign from customer favourites (M:N unlink)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Campaign removed from favourites"),
            @ApiResponse(responseCode = "404", description = "Customer or campaign not found")
    })
    public ResponseEntity<Void> deleteFavouriteFundraising(
            @Parameter(description = "Customer UUID") @PathVariable UUID id,
            @Parameter(description = "Fundraising campaign UUID") @PathVariable UUID fundraisingId) {
        favouriteService.removeFavourite(id, fundraisingId);
        return ResponseEntity.noContent().build();
    }
}

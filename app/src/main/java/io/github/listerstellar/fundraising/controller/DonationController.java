package io.github.listerstellar.fundraising.controller;

import io.github.listerstellar.fundraising.dto.DonationRequestDto;
import io.github.listerstellar.fundraising.dto.DonationResponseDto;
import io.github.listerstellar.fundraising.service.DonationService;
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
@RequestMapping("/api/donations")
@RequiredArgsConstructor
@Tag(name = "Donation Management", description = "Operations for registering, querying, and managing donations")
public class DonationController {

    private final DonationService donationService;

    private URI createLocationUri(UUID id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    @GetMapping
    @Operation(summary = "Get donations", description = "Retrieves all donations, with optional filtering by customer or fundraising")
    @ApiResponse(responseCode = "200", description = "Donations retrieved successfully")
    public ResponseEntity<List<DonationResponseDto>> getDonations(
            @Parameter(description = "Filter by donor customer UUID") @RequestParam(required = false) UUID customerId,
            @Parameter(description = "Filter by fundraising campaign UUID") @RequestParam(required = false) UUID fundraisingId) {
        if (customerId != null && fundraisingId != null) {
            return ResponseEntity.ok(donationService.getDonationsByCustomerIdAndFundraisingId(customerId, fundraisingId));
        }
        if (customerId != null) {
            return ResponseEntity.ok(donationService.getDonationsByCustomerId(customerId));
        }
        if (fundraisingId != null) {
            return ResponseEntity.ok(donationService.getDonationsByFundraisingId(fundraisingId));
        }
        return ResponseEntity.ok(donationService.getDonations());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get donation by ID", description = "Retrieves a single donation record by UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donation found"),
            @ApiResponse(responseCode = "404", description = "Donation not found")
    })
    public ResponseEntity<DonationResponseDto> getDonationById(
            @Parameter(description = "Donation UUID") @PathVariable UUID id) {
        return ResponseEntity.ok(donationService.getDonationById(id));
    }

    @PostMapping
    @Operation(summary = "Create donation", description = "Registers a new donation for a fundraising campaign")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Donation processed successfully",
                    headers = @Header(name = "Location", description = "URI of the newly created donation")),
            @ApiResponse(responseCode = "400", description = "Invalid donation payload"),
            @ApiResponse(responseCode = "404", description = "Donor customer or campaign not found")
    })
    public ResponseEntity<DonationResponseDto> createDonation(
            @Valid @RequestBody DonationRequestDto donationRequestDto) {
        DonationResponseDto created = donationService.createDonation(donationRequestDto);
        return ResponseEntity.created(createLocationUri(created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update donation", description = "Fully updates an existing donation record")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donation updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Donation, donor or campaign not found")
    })
    public ResponseEntity<DonationResponseDto> updateDonation(
            @Parameter(description = "Donation UUID") @PathVariable UUID id,
            @Valid @RequestBody DonationRequestDto donationRequestDto) {
        return ResponseEntity.ok(donationService.updateDonation(id, donationRequestDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update donation", description = "Partially updates an existing donation record")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Donation partially updated"),
            @ApiResponse(responseCode = "404", description = "Donation, donor or campaign not found")
    })
    public ResponseEntity<DonationResponseDto> partialUpdateDonation(
            @Parameter(description = "Donation UUID") @PathVariable UUID id,
            @RequestBody DonationRequestDto donationRequestDto) {
        return ResponseEntity.ok(donationService.partialUpdateDonation(id, donationRequestDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete donation", description = "Deletes a donation record by UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Donation deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Donation not found")
    })
    public ResponseEntity<Void> deleteDonation(
            @Parameter(description = "Donation UUID") @PathVariable UUID id) {
        donationService.deleteDonation(id);
        return ResponseEntity.noContent().build();
    }
}

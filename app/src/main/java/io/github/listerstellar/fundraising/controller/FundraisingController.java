package io.github.listerstellar.fundraising.controller;

import io.github.listerstellar.fundraising.dto.CustomerResponseDto;
import io.github.listerstellar.fundraising.dto.FundraisingRequestDto;
import io.github.listerstellar.fundraising.dto.FundraisingResponseDto;
import io.github.listerstellar.fundraising.dto.TopDonorDto;
import io.github.listerstellar.fundraising.service.DonationService;
import io.github.listerstellar.fundraising.service.FavouriteService;
import io.github.listerstellar.fundraising.service.FundraisingService;
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
@RequestMapping("/api/fundraisings")
@RequiredArgsConstructor
@Tag(name = "Fundraising Management", description = "Operations for managing charitable campaigns and analytics")
public class FundraisingController {

    private final FundraisingService fundraisingService;
    private final FavouriteService favouriteService;
    private final DonationService donationService;

    private URI createLocationUri(UUID id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }

    @GetMapping
    @Operation(summary = "Get all fundraising campaigns", description = "Retrieves a list of all existing fundraising campaigns")
    @ApiResponse(responseCode = "200", description = "List of campaigns retrieved successfully")
    public ResponseEntity<List<FundraisingResponseDto>> getFundraisings() {
        return ResponseEntity.ok(fundraisingService.getFundraisings());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get fundraising campaign by ID", description = "Retrieves details of a fundraising campaign by its UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Campaign found"),
            @ApiResponse(responseCode = "404", description = "Campaign not found")
    })
    public ResponseEntity<FundraisingResponseDto> getFundraising(
            @Parameter(description = "Fundraising UUID") @PathVariable UUID id) {
        return ResponseEntity.ok(fundraisingService.getFundraisingById(id));
    }

    @PostMapping
    @Operation(summary = "Create a fundraising campaign", description = "Creates a new charitable fundraising campaign")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Campaign created successfully",
                    headers = @Header(name = "Location", description = "URI of the newly created campaign")),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Creator customer not found")
    })
    public ResponseEntity<FundraisingResponseDto> createFundraising(
            @Valid @RequestBody FundraisingRequestDto fundraisingRequestDto) {
        FundraisingResponseDto created = fundraisingService.createFundraising(fundraisingRequestDto);
        return ResponseEntity.created(createLocationUri(created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update fundraising campaign", description = "Fully updates an existing fundraising campaign")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Campaign updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "404", description = "Campaign or creator not found")
    })
    public ResponseEntity<FundraisingResponseDto> updateFundraising(
            @Parameter(description = "Fundraising UUID") @PathVariable UUID id,
            @Valid @RequestBody FundraisingRequestDto fundraisingRequestDto) {
        return ResponseEntity.ok(fundraisingService.updateFundraising(id, fundraisingRequestDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial update fundraising campaign", description = "Partially updates an existing fundraising campaign")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Campaign partially updated"),
            @ApiResponse(responseCode = "404", description = "Campaign or creator not found")
    })
    public ResponseEntity<FundraisingResponseDto> partialUpdateFundraising(
            @Parameter(description = "Fundraising UUID") @PathVariable UUID id,
            @RequestBody FundraisingRequestDto fundraisingRequestDto) {
        return ResponseEntity.ok(fundraisingService.partialUpdateFundraising(id, fundraisingRequestDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete fundraising campaign", description = "Deletes a fundraising campaign by UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Campaign deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Campaign not found"),
            @ApiResponse(responseCode = "409", description = "Cannot delete campaign with recorded donations")
    })
    public ResponseEntity<Void> deleteFundraising(
            @Parameter(description = "Fundraising UUID") @PathVariable UUID id) {
        fundraisingService.deleteFundraisingById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/favoured")
    @Operation(summary = "Get customers who favourited campaign", description = "Retrieves customers who added this campaign to favourites")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of customers retrieved"),
            @ApiResponse(responseCode = "404", description = "Campaign not found")
    })
    public ResponseEntity<List<CustomerResponseDto>> getFavouredByCustomersById(
            @Parameter(description = "Fundraising UUID") @PathVariable UUID id) {
        return ResponseEntity.ok(favouriteService.getFavouredByCustomersByFundraisingId(id));
    }

    @GetMapping("/{id}/top-donors")
    @Operation(summary = "Get top donors for campaign",
            description = "Calculates and ranks top donors for a specific campaign by total donated sum")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Top donors ranked successfully"),
            @ApiResponse(responseCode = "404", description = "Campaign not found")
    })
    public ResponseEntity<List<TopDonorDto>> getTopDonors(
            @Parameter(description = "Fundraising UUID") @PathVariable UUID id,
            @Parameter(description = "Number of top donors to return (default: 5)")
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(donationService.getTopDonorsByFundraisingId(id, limit));
    }
}

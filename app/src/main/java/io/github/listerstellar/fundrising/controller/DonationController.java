package io.github.listerstellar.fundrising.controller;

import io.github.listerstellar.fundrising.dto.DonationRequestDto;
import io.github.listerstellar.fundrising.dto.DonationResponseDto;
import io.github.listerstellar.fundrising.service.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @GetMapping
    public ResponseEntity<List<DonationResponseDto>> getDonations(@RequestParam(required = false) UUID customerId,
                                                                  @RequestParam(required = false) UUID fundraisingId) {
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
    public ResponseEntity<DonationResponseDto> getDonationById(@PathVariable UUID id) {
        return ResponseEntity.ok(donationService.getDonationById(id));
    }

    @PostMapping
    public ResponseEntity<DonationResponseDto> createDonation(@Valid @RequestBody DonationRequestDto donationRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(donationService.createDonation(donationRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DonationResponseDto> updateDonation(@PathVariable UUID id,
                                                              @Valid @RequestBody DonationRequestDto donationRequestDto) {
        return ResponseEntity.ok(donationService.updateDonation(id, donationRequestDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DonationResponseDto> partialUpdateDonation(@PathVariable UUID id,
                                                                     @RequestBody DonationRequestDto donationRequestDto) {
        return ResponseEntity.ok(donationService.partialUpdateDonation(id, donationRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable UUID id) {
        donationService.deleteDonation(id);
        return ResponseEntity.noContent().build();
    }
}

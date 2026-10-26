package io.github.listerstellar.fundrising.service;

import io.github.listerstellar.fundrising.dto.DonationRequestDto;
import io.github.listerstellar.fundrising.dto.DonationResponseDto;
import io.github.listerstellar.fundrising.entity.Donation;

import java.util.List;
import java.util.UUID;

public interface DonationService {

    List<DonationResponseDto> getDonations();

    List<DonationResponseDto> getDonationsByCustomerId(UUID CustomerId);

    List<DonationResponseDto> getDonationsByFundraisingId(UUID fundraisingId);

    Donation getDonationEntityById(UUID donationId);

    DonationResponseDto getDonationById(UUID donationId);

    DonationResponseDto createDonation(DonationRequestDto donationRequestDto);

    DonationResponseDto updateDonation(UUID donationId, DonationRequestDto donationRequestDto);

    void deleteDonation(UUID donationId);
}

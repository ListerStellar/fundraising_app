package io.github.listerstellar.fundraising.service;

import io.github.listerstellar.fundraising.dto.DonationRequestDto;
import io.github.listerstellar.fundraising.dto.DonationResponseDto;
import io.github.listerstellar.fundraising.dto.TopDonorDto;
import io.github.listerstellar.fundraising.entity.Donation;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing donations.
 */
public interface DonationService {

    /**
     * Retrieves all recorded donations.
     *
     * @return list of donation response DTOs
     */
    List<DonationResponseDto> getDonations();

    /**
     * Retrieves all donations made by a specific customer.
     *
     * @param customerId UUID of the donor customer
     * @return list of donation response DTOs
     */
    List<DonationResponseDto> getDonationsByCustomerId(UUID customerId);

    /**
     * Retrieves all donations made to a specific fundraising campaign.
     *
     * @param fundraisingId UUID of the fundraising campaign
     * @return list of donation response DTOs
     */
    List<DonationResponseDto> getDonationsByFundraisingId(UUID fundraisingId);

    /**
     * Retrieves donations filtered by both customer and fundraising campaign.
     *
     * @param customerId UUID of the donor customer
     * @param fundraisingId UUID of the fundraising campaign
     * @return list of matching donation response DTOs
     */
    List<DonationResponseDto> getDonationsByCustomerIdAndFundraisingId(UUID customerId, UUID fundraisingId);

    /**
     * Retrieves a donation entity by its unique identifier.
     *
     * @param donationId UUID of the donation
     * @return Donation entity
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if donation is not found
     */
    Donation getDonationEntityById(UUID donationId);

    /**
     * Retrieves a donation by its unique identifier.
     *
     * @param donationId UUID of the donation
     * @return donation response DTO
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if donation is not found
     */
    DonationResponseDto getDonationById(UUID donationId);

    /**
     * Creates a new donation.
     *
     * @param donationRequestDto donation creation data
     * @return created donation response DTO
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if donor or campaign is not found
     */
    DonationResponseDto createDonation(DonationRequestDto donationRequestDto);

    /**
     * Fully updates an existing donation record.
     *
     * @param donationId UUID of the donation to update
     * @param donationRequestDto new donation data
     * @return updated donation response DTO
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if donation, donor, or campaign is not found
     */
    DonationResponseDto updateDonation(UUID donationId, DonationRequestDto donationRequestDto);

    /**
     * Partially updates an existing donation record.
     *
     * @param donationId UUID of the donation to update
     * @param donationRequestDto partial donation data
     * @return updated donation response DTO
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if donation, donor, or campaign is not found
     */
    DonationResponseDto partialUpdateDonation(UUID donationId, DonationRequestDto donationRequestDto);

    /**
     * Deletes a donation by its unique identifier.
     *
     * @param donationId UUID of the donation to delete
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if donation is not found
     */
    void deleteDonation(UUID donationId);

    /**
     * Retrieves the top donors for a specific fundraising campaign, ranked by total donated amount.
     *
     * @param fundraisingId UUID of the fundraising campaign
     * @param limit maximum number of top donors to return
     * @return list of top donor DTOs ordered by total amount descending
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if campaign not found
     */
    List<TopDonorDto> getTopDonorsByFundraisingId(UUID fundraisingId, int limit);
}

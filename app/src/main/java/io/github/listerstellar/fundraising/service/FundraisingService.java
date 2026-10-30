package io.github.listerstellar.fundraising.service;

import io.github.listerstellar.fundraising.dto.FundraisingRequestDto;
import io.github.listerstellar.fundraising.dto.FundraisingResponseDto;
import io.github.listerstellar.fundraising.entity.Fundraising;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing charitable fundraising campaigns.
 */
public interface FundraisingService {

    /**
     * Retrieves all fundraising campaigns.
     *
     * @return list of fundraising response DTOs
     */
    List<FundraisingResponseDto> getFundraisings();

    /**
     * Retrieves a fundraising entity by its unique identifier.
     *
     * @param fundraisingId UUID of the fundraising
     * @return found Fundraising entity
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if campaign is not found
     */
    Fundraising getFundraisingEntityById(UUID fundraisingId);

    /**
     * Retrieves a fundraising campaign by its unique identifier.
     *
     * @param fundraisingId UUID of the fundraising
     * @return fundraising response DTO
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if campaign is not found
     */
    FundraisingResponseDto getFundraisingById(UUID fundraisingId);

    /**
     * Creates a new fundraising campaign.
     *
     * @param fundraisingRequestDto campaign creation data
     * @return created fundraising response DTO
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if creator customer does not exist
     */
    FundraisingResponseDto createFundraising(FundraisingRequestDto fundraisingRequestDto);

    /**
     * Fully updates an existing fundraising campaign.
     *
     * @param fundraisingId UUID of the fundraising to update
     * @param fundraisingRequestDto new campaign data
     * @return updated fundraising response DTO
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if campaign or creator not found
     */
    FundraisingResponseDto updateFundraising(UUID fundraisingId, FundraisingRequestDto fundraisingRequestDto);

    /**
     * Partially updates an existing fundraising campaign.
     *
     * @param fundraisingId UUID of the fundraising to update
     * @param fundraisingRequestDto partial campaign data
     * @return updated fundraising response DTO
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if campaign or creator not found
     */
    FundraisingResponseDto partialUpdateFundraising(UUID fundraisingId, FundraisingRequestDto fundraisingRequestDto);

    /**
     * Deletes a fundraising campaign by its unique identifier.
     *
     * @param fundraisingId UUID of the fundraising to delete
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if campaign not found
     */
    void deleteFundraisingById(UUID fundraisingId);
}

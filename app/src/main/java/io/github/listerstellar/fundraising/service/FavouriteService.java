package io.github.listerstellar.fundraising.service;

import io.github.listerstellar.fundraising.dto.CustomerResponseDto;
import io.github.listerstellar.fundraising.dto.FundraisingResponseDto;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing the many-to-many relationship
 * between customers and their favourite fundraising campaigns.
 */
public interface FavouriteService {

    /**
     * Retrieves all fundraising campaigns favourited by a specific customer.
     *
     * @param customerId UUID of the customer
     * @return list of favourite fundraising response DTOs
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if customer is not found
     */
    List<FundraisingResponseDto> getFavouriteFundraisingsByCustomerId(UUID customerId);

    /**
     * Retrieves all customers who have favourited a specific fundraising campaign.
     *
     * @param fundraisingId UUID of the fundraising campaign
     * @return list of customer response DTOs who favourited the campaign
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if campaign is not found
     */
    List<CustomerResponseDto> getFavouredByCustomersByFundraisingId(UUID fundraisingId);

    /**
     * Adds a fundraising campaign to a customer's favourite list (creates M:N association).
     *
     * @param customerId UUID of the customer
     * @param fundraisingId UUID of the fundraising campaign
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if customer or campaign is not found
     */
    void addFavourite(UUID customerId, UUID fundraisingId);

    /**
     * Removes a fundraising campaign from a customer's favourite list (removes M:N association).
     *
     * @param customerId UUID of the customer
     * @param fundraisingId UUID of the fundraising campaign
     * @throws io.github.listerstellar.fundraising.exception.EntityNotFoundException if customer or campaign is not found
     */
    void removeFavourite(UUID customerId, UUID fundraisingId);
}

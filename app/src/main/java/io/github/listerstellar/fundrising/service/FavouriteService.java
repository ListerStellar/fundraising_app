package io.github.listerstellar.fundrising.service;

import io.github.listerstellar.fundrising.dto.CustomerResponseDto;
import io.github.listerstellar.fundrising.dto.FundraisingResponseDto;

import java.util.List;
import java.util.UUID;

public interface FavouriteService {

    public List<FundraisingResponseDto> getFavouriteFundraisingsByCustomerId(UUID customerId);

    public List<CustomerResponseDto> getFavouredByCustomersByFundraisingId(UUID fundraisingId);

    public void addFavourite(UUID customerId, UUID fundraisingId);

    public void removeFavourite(UUID customerId, UUID fundraisingId);
}

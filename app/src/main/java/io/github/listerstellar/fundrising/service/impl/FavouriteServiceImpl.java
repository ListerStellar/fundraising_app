package io.github.listerstellar.fundrising.service.impl;

import io.github.listerstellar.fundrising.dto.CustomerResponseDto;
import io.github.listerstellar.fundrising.dto.FundraisingResponseDto;
import io.github.listerstellar.fundrising.entity.Customer;
import io.github.listerstellar.fundrising.entity.Fundraising;
import io.github.listerstellar.fundrising.mapper.CustomerMapper;
import io.github.listerstellar.fundrising.mapper.FundraisingMapper;
import io.github.listerstellar.fundrising.service.CustomerService;
import io.github.listerstellar.fundrising.service.FavouriteService;
import io.github.listerstellar.fundrising.service.FundraisingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FavouriteServiceImpl implements FavouriteService {

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    private final FundraisingService fundraisingService;
    private final FundraisingMapper fundraisingMapper;

    @Override
    @Transactional(readOnly = true)
    public List<FundraisingResponseDto> getFavouriteFundraisingsByCustomerId(UUID customerId) {
        Customer customer = customerService.getCustomerEntityById(customerId);

        return customer.getFavouriteFundraisings().stream()
                .map(fundraisingMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getFavouredByCustomersByFundraisingId(UUID fundraisingId) {
        Fundraising fundraising = fundraisingService.getFundraisingEntityById(fundraisingId);

        return fundraising.getFavouredByCustomers().stream()
                .map(customerMapper::toDto)
                .toList();
    }

    @Override
    public void addFavourite(UUID customerId, UUID fundraisingId) {
        Customer customer = customerService.getCustomerEntityById(customerId);
        Fundraising fundraising = fundraisingService.getFundraisingEntityById(fundraisingId);

        customer.addFavouriteFundraising(fundraising);
    }

    @Override
    public void removeFavourite(UUID customerId, UUID fundraisingId) {
        Customer customer = customerService.getCustomerEntityById(customerId);
        Fundraising fundraising = fundraisingService.getFundraisingEntityById(fundraisingId);

        customer.removeFavouriteFundraising(fundraising);
    }
}

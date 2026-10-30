package io.github.listerstellar.fundraising.service;

import io.github.listerstellar.fundraising.dto.CustomerResponseDto;
import io.github.listerstellar.fundraising.dto.FundraisingResponseDto;
import io.github.listerstellar.fundraising.entity.Customer;
import io.github.listerstellar.fundraising.entity.Fundraising;
import io.github.listerstellar.fundraising.mapper.CustomerMapper;
import io.github.listerstellar.fundraising.mapper.FundraisingMapper;
import io.github.listerstellar.fundraising.service.impl.FavouriteServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavouriteServiceImplTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private FundraisingService fundraisingService;

    @Mock
    private FundraisingMapper fundraisingMapper;

    @InjectMocks
    private FavouriteServiceImpl favouriteService;

    @Test
    @DisplayName("addFavourite establishes ManyToMany association on both sides")
    void addFavourite_success() {
        UUID customerId = UUID.randomUUID();
        UUID fundraisingId = UUID.randomUUID();

        Customer customer = Customer.builder().id(customerId).favouriteFundraisings(new HashSet<>()).build();
        Fundraising fundraising = Fundraising.builder().id(fundraisingId).favouredByCustomers(new HashSet<>()).build();

        when(customerService.getCustomerEntityById(customerId)).thenReturn(customer);
        when(fundraisingService.getFundraisingEntityById(fundraisingId)).thenReturn(fundraising);

        favouriteService.addFavourite(customerId, fundraisingId);

        assertThat(customer.getFavouriteFundraisings()).contains(fundraising);
        assertThat(fundraising.getFavouredByCustomers()).contains(customer);
    }

    @Test
    @DisplayName("removeFavourite removes ManyToMany association from both sides")
    void removeFavourite_success() {
        UUID customerId = UUID.randomUUID();
        UUID fundraisingId = UUID.randomUUID();

        Customer customer = Customer.builder().id(customerId).favouriteFundraisings(new HashSet<>()).build();
        Fundraising fundraising = Fundraising.builder().id(fundraisingId).favouredByCustomers(new HashSet<>()).build();

        customer.addFavouriteFundraising(fundraising);

        when(customerService.getCustomerEntityById(customerId)).thenReturn(customer);
        when(fundraisingService.getFundraisingEntityById(fundraisingId)).thenReturn(fundraising);

        favouriteService.removeFavourite(customerId, fundraisingId);

        assertThat(customer.getFavouriteFundraisings()).doesNotContain(fundraising);
        assertThat(fundraising.getFavouredByCustomers()).doesNotContain(customer);
    }

    @Test
    @DisplayName("getFavouriteFundraisingsByCustomerId returns mapped list of favourites")
    void getFavouriteFundraisingsByCustomerId_success() {
        UUID customerId = UUID.randomUUID();
        Fundraising campaign = Fundraising.builder().id(UUID.randomUUID()).name("Charity").build();
        Customer customer = Customer.builder().id(customerId).favouriteFundraisings(new HashSet<>(List.of(campaign))).build();

        FundraisingResponseDto dto = new FundraisingResponseDto(campaign.getId(), "Charity", new BigDecimal("100"), "desc", UUID.randomUUID(), Collections.emptySet());

        when(customerService.getCustomerEntityById(customerId)).thenReturn(customer);
        when(fundraisingMapper.toDto(campaign)).thenReturn(dto);

        List<FundraisingResponseDto> result = favouriteService.getFavouriteFundraisingsByCustomerId(customerId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Charity");
    }
}


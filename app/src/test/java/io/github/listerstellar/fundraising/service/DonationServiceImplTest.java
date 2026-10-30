package io.github.listerstellar.fundraising.service;

import io.github.listerstellar.fundraising.dto.DonationRequestDto;
import io.github.listerstellar.fundraising.dto.DonationResponseDto;
import io.github.listerstellar.fundraising.dto.TopDonorDto;
import io.github.listerstellar.fundraising.entity.Customer;
import io.github.listerstellar.fundraising.entity.Donation;
import io.github.listerstellar.fundraising.entity.Fundraising;
import io.github.listerstellar.fundraising.exception.EntityNotFoundException;
import io.github.listerstellar.fundraising.mapper.DonationMapper;
import io.github.listerstellar.fundraising.repository.DonationRepository;
import io.github.listerstellar.fundraising.service.impl.DonationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceImplTest {

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private DonationMapper donationMapper;

    @Mock
    private CustomerService customerService;

    @Mock
    private FundraisingService fundraisingService;

    @InjectMocks
    private DonationServiceImpl donationService;

    @Test
    @DisplayName("createDonation resolves entities, maps, and persists donation")
    void createDonation_success() {
        UUID customerId = UUID.randomUUID();
        UUID fundraisingId = UUID.randomUUID();
        Customer customer = Customer.builder().id(customerId).build();
        Fundraising fundraising = Fundraising.builder().id(fundraisingId).build();

        DonationRequestDto requestDto = new DonationRequestDto(new BigDecimal("100.00"), "Support", customerId, fundraisingId);
        Donation entity = Donation.builder().id(UUID.randomUUID()).amount(new BigDecimal("100.00")).customer(customer).fundraising(fundraising).build();
        DonationResponseDto responseDto = new DonationResponseDto(entity.getId(), new BigDecimal("100.00"), Instant.now(), "Support", customerId, fundraisingId);

        when(customerService.getCustomerEntityById(customerId)).thenReturn(customer);
        when(fundraisingService.getFundraisingEntityById(fundraisingId)).thenReturn(fundraising);
        when(donationMapper.toEntity(requestDto, customer, fundraising)).thenReturn(entity);
        when(donationRepository.save(entity)).thenReturn(entity);
        when(donationMapper.toDto(entity)).thenReturn(responseDto);

        DonationResponseDto result = donationService.createDonation(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.amount()).isEqualTo(new BigDecimal("100.00"));
        verify(donationRepository).save(entity);
    }

    @Test
    @DisplayName("getDonationsByCustomerId returns list filtered by customer")
    void getDonationsByCustomerId_returnsList() {
        UUID customerId = UUID.randomUUID();
        Donation donation = Donation.builder().id(UUID.randomUUID()).build();
        DonationResponseDto responseDto = new DonationResponseDto(donation.getId(), new BigDecimal("50.00"), Instant.now(), "Good luck", customerId, UUID.randomUUID());

        when(donationRepository.findAllByCustomerId(customerId)).thenReturn(List.of(donation));
        when(donationMapper.toDto(donation)).thenReturn(responseDto);

        List<DonationResponseDto> result = donationService.getDonationsByCustomerId(customerId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().customerId()).isEqualTo(customerId);
    }

    @Test
    @DisplayName("deleteDonation throws EntityNotFoundException when donation does not exist")
    void deleteDonation_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(donationRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> donationService.deleteDonation(id))
                .isInstanceOf(EntityNotFoundException.class);

        verify(donationRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("getTopDonorsByFundraisingId returns ranked list from donation repository")
    void getTopDonorsByFundraisingId_success() {
        UUID campaignId = UUID.randomUUID();
        TopDonorDto topDonor = new TopDonorDto(UUID.randomUUID(), "donor1", "Alice", "Smith", new BigDecimal("500.00"));

        when(fundraisingService.getFundraisingEntityById(campaignId)).thenReturn(Fundraising.builder().id(campaignId).build());
        when(donationRepository.findTopDonorsByFundraisingId(eq(campaignId), any(PageRequest.class)))
                .thenReturn(List.of(topDonor));

        List<TopDonorDto> result = donationService.getTopDonorsByFundraisingId(campaignId, 5);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().totalDonated()).isEqualTo(new BigDecimal("500.00"));
    }
}

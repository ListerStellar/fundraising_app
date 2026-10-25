package io.github.listerstellar.fundrising.mapper;

import io.github.listerstellar.fundrising.dto.DonationRequestDto;
import io.github.listerstellar.fundrising.dto.DonationResponseDto;
import io.github.listerstellar.fundrising.entity.Donation;
import io.github.listerstellar.fundrising.service.CustomerService;
import io.github.listerstellar.fundrising.service.FundraisingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DonationMapper {

    private final CustomerService customerService;
    private final FundraisingService fundraisingService;

    public DonationResponseDto toDto(Donation donation) {
        if (donation == null) { return null; }
        return new DonationResponseDto(
                donation.getId(),
                donation.getAmount(),
                donation.getDonationDate(),
                donation.getDescription(),
                donation.getDonor().getId(),
                donation.getFundraising().getId()
        );
    }

    public Donation toEntity(DonationRequestDto donationRequestDto) {
        if (donationRequestDto == null) { return null; }
        return Donation.builder()
                .amount(donationRequestDto.amount())
                .description(donationRequestDto.description())
                .donor(customerService.getCustomerEntityById(donationRequestDto.donorId()))
                .fundraising(fundraisingService.getFundraisingEntityById(donationRequestDto.fundraisingId()))
                .build();
    }
}

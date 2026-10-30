package io.github.listerstellar.fundraising.mapper;

import io.github.listerstellar.fundraising.dto.DonationRequestDto;
import io.github.listerstellar.fundraising.dto.DonationResponseDto;
import io.github.listerstellar.fundraising.entity.Customer;
import io.github.listerstellar.fundraising.entity.Donation;
import io.github.listerstellar.fundraising.entity.Fundraising;
import org.springframework.stereotype.Component;

@Component
public class DonationMapper {

    public DonationResponseDto toDto(Donation donation) {
        if (donation == null) { return null; }
        return new DonationResponseDto(
                donation.getId(),
                donation.getAmount(),
                donation.getDonationDate(),
                donation.getDescription(),
                donation.getCustomer().getId(),
                donation.getFundraising().getId()
        );
    }

    public Donation toEntity(DonationRequestDto donationRequestDto, Customer customer, Fundraising fundraising) {
        if (donationRequestDto == null) { return null; }
        return Donation.builder()
                .amount(donationRequestDto.amount())
                .description(donationRequestDto.description())
                .customer(customer)
                .fundraising(fundraising)
                .build();
    }
}

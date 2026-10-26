package io.github.listerstellar.fundrising.service.impl;

import io.github.listerstellar.fundrising.dto.DonationRequestDto;
import io.github.listerstellar.fundrising.dto.DonationResponseDto;
import io.github.listerstellar.fundrising.entity.Customer;
import io.github.listerstellar.fundrising.entity.Donation;
import io.github.listerstellar.fundrising.entity.Fundraising;
import io.github.listerstellar.fundrising.exception.EntityNotFoundException;
import io.github.listerstellar.fundrising.mapper.DonationMapper;
import io.github.listerstellar.fundrising.repository.DonationRepository;
import io.github.listerstellar.fundrising.service.CustomerService;
import io.github.listerstellar.fundrising.service.DonationService;
import io.github.listerstellar.fundrising.service.FundraisingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class DonationServiceImpl implements DonationService {

    DonationRepository donationRepository;
    DonationMapper donationMapper;

    CustomerService customerService;
    FundraisingService fundraisingService;

    @Override
    public List<DonationResponseDto> getDonations() {
        return donationRepository.findAll().stream()
                .map(donationMapper::toDto)
                .toList();
    }

    @Override
    public List<DonationResponseDto> getDonationsByCustomerId(UUID customerId) {
        return donationRepository.findAllByCustomerId(customerId).stream()
                .map(donationMapper::toDto)
                .toList();
    }

    @Override
    public List<DonationResponseDto> getDonationsByFundraisingId(UUID fundraisingId) {
        return donationRepository.findAllByFundraisingId(fundraisingId).stream()
                .map(donationMapper::toDto)
                .toList();
    }

    @Override
    public Donation getDonationEntityById(UUID donationId) {
        return donationRepository.findById(donationId)
                .orElseThrow(() -> new EntityNotFoundException("Donation", donationId.toString()));
    }

    @Override
    public DonationResponseDto getDonationById(UUID donationId) {
        return donationMapper.toDto(
                donationRepository.findById(donationId)
                        .orElseThrow(() -> new EntityNotFoundException("Donation", donationId.toString()))
        );
    }

    @Override
    public DonationResponseDto createDonation(DonationRequestDto donationRequestDto) {
        return donationMapper.toDto(
                donationRepository.save(
                        donationMapper.toEntity(donationRequestDto)
                )
        );
    }

    @Override
    public DonationResponseDto updateDonation(UUID donationId, DonationRequestDto donationRequestDto) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new EntityNotFoundException("Donation", donationId.toString()));

        if (donationRequestDto.customerId() != null) {
            Customer customer = customerService.getCustomerEntityById(donationRequestDto.customerId());
            donation.setCustomer(customer);
        }
        if (donationRequestDto.fundraisingId() != null) {
            Fundraising fundraising = fundraisingService.getFundraisingEntityById(donationRequestDto.fundraisingId());
            donation.setFundraising(fundraising);
        }
        if (donationRequestDto.amount() != null) donation.setAmount(donationRequestDto.amount());
        if (donationRequestDto.description() != null) donation.setDescription(donationRequestDto.description());

        return donationMapper.toDto(donationRepository.save(donation));
    }

    @Override
    public void deleteDonation(UUID donationId) {
        if (!donationRepository.existsById(donationId)) {
            throw new EntityNotFoundException("Donation", donationId.toString());
        }
        donationRepository.deleteById(donationId);
    }
}

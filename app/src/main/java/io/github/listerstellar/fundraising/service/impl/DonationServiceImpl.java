package io.github.listerstellar.fundraising.service.impl;

import io.github.listerstellar.fundraising.dto.DonationRequestDto;
import io.github.listerstellar.fundraising.dto.DonationResponseDto;
import io.github.listerstellar.fundraising.dto.TopDonorDto;
import io.github.listerstellar.fundraising.entity.Customer;
import io.github.listerstellar.fundraising.entity.Donation;
import io.github.listerstellar.fundraising.entity.Fundraising;
import io.github.listerstellar.fundraising.exception.EntityNotFoundException;
import io.github.listerstellar.fundraising.mapper.DonationMapper;
import io.github.listerstellar.fundraising.repository.DonationRepository;
import io.github.listerstellar.fundraising.service.CustomerService;
import io.github.listerstellar.fundraising.service.DonationService;
import io.github.listerstellar.fundraising.service.FundraisingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final DonationMapper donationMapper;
    private final CustomerService customerService;
    private final FundraisingService fundraisingService;

    @Override
    @Transactional(readOnly = true)
    public List<DonationResponseDto> getDonations() {
        return donationRepository.findAll().stream()
                .map(donationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonationResponseDto> getDonationsByCustomerId(UUID customerId) {
        return donationRepository.findAllByCustomerId(customerId).stream()
                .map(donationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonationResponseDto> getDonationsByFundraisingId(UUID fundraisingId) {
        return donationRepository.findAllByFundraisingId(fundraisingId).stream()
                .map(donationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonationResponseDto> getDonationsByCustomerIdAndFundraisingId(UUID customerId, UUID fundraisingId) {
        return donationRepository.findAllByCustomerIdAndFundraisingId(customerId, fundraisingId).stream()
                .map(donationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Donation getDonationEntityById(UUID donationId) {
        return donationRepository.findById(donationId)
                .orElseThrow(() -> new EntityNotFoundException("Donation", donationId.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public DonationResponseDto getDonationById(UUID donationId) {
        return donationMapper.toDto(getDonationEntityById(donationId));
    }

    @Override
    public DonationResponseDto createDonation(DonationRequestDto donationRequestDto) {
        Customer customer = customerService.getCustomerEntityById(donationRequestDto.customerId());
        Fundraising fundraising = fundraisingService.getFundraisingEntityById(donationRequestDto.fundraisingId());
        Donation donation = donationMapper.toEntity(donationRequestDto, customer, fundraising);
        return donationMapper.toDto(donationRepository.save(donation));
    }

    @Override
    public DonationResponseDto updateDonation(UUID donationId, DonationRequestDto donationRequestDto) {
        Donation donation = getDonationEntityById(donationId);
        Customer customer = customerService.getCustomerEntityById(donationRequestDto.customerId());
        Fundraising fundraising = fundraisingService.getFundraisingEntityById(donationRequestDto.fundraisingId());

        donation.setCustomer(customer);
        donation.setFundraising(fundraising);
        donation.setAmount(donationRequestDto.amount());
        donation.setDescription(donationRequestDto.description());

        return donationMapper.toDto(donationRepository.save(donation));
    }

    @Override
    public DonationResponseDto partialUpdateDonation(UUID donationId, DonationRequestDto donationRequestDto) {
        Donation donation = getDonationEntityById(donationId);

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

    @Override
    @Transactional(readOnly = true)
    public List<TopDonorDto> getTopDonorsByFundraisingId(UUID fundraisingId, int limit) {
        // Validates that the fundraising campaign exists
        fundraisingService.getFundraisingEntityById(fundraisingId);
        return donationRepository.findTopDonorsByFundraisingId(fundraisingId, PageRequest.of(0, limit));
    }
}

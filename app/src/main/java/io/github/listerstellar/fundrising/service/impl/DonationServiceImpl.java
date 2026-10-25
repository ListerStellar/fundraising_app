package io.github.listerstellar.fundrising.service.impl;

import io.github.listerstellar.fundrising.dto.DonationRequestDto;
import io.github.listerstellar.fundrising.dto.DonationResponseDto;
import io.github.listerstellar.fundrising.entity.Donation;
import io.github.listerstellar.fundrising.service.DonationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DonationServiceImpl implements DonationService {

    @Override
    public List<DonationResponseDto> getDonations() {
        return List.of();
    }

    @Override
    public List<DonationResponseDto> getDonationsByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public List<DonationResponseDto> getDonationsByFundraisingId(UUID fundraisingId) {
        return List.of();
    }

    @Override
    public Donation getDonationEntityById(UUID donationId) {
        return null;
    }

    @Override
    public DonationResponseDto getDonationById(UUID donationId) {
        return null;
    }

    @Override
    public DonationResponseDto createDonation(DonationRequestDto donationRequestDto) {
        return null;
    }

    @Override
    public DonationResponseDto updateDonation(UUID donationId, DonationRequestDto donationRequestDto) {
        return null;
    }

    @Override
    public void deleteDonation(UUID donationId) {

    }
}

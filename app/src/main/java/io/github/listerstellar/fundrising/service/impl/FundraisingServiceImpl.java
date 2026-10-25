package io.github.listerstellar.fundrising.service.impl;

import io.github.listerstellar.fundrising.dto.FundraisingRequestDto;
import io.github.listerstellar.fundrising.dto.FundraisingResponseDto;
import io.github.listerstellar.fundrising.entity.Fundraising;
import io.github.listerstellar.fundrising.service.FundraisingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FundraisingServiceImpl implements FundraisingService {

    @Override
    public List<FundraisingResponseDto> getFundraisings() {
        return List.of();
    }

    @Override
    public Fundraising getFundraisingEntityById(UUID fundraisingId) {
        return null;
    }

    @Override
    public FundraisingResponseDto getFundraisingById(UUID fundraisingId) {
        return null;
    }

    @Override
    public FundraisingResponseDto createFundraising(FundraisingRequestDto fundraisingRequestDto) {
        return null;
    }

    @Override
    public FundraisingResponseDto updateFundraising(UUID fundraisingId, FundraisingRequestDto fundraisingRequestDto) {
        return null;
    }

    @Override
    public void deleteFundraisingById(UUID fundraisingId) {

    }
}

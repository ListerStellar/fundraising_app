package io.github.listerstellar.fundrising.service;

import io.github.listerstellar.fundrising.dto.FundraisingRequestDto;
import io.github.listerstellar.fundrising.dto.FundraisingResponseDto;
import io.github.listerstellar.fundrising.entity.Fundraising;

import java.util.List;
import java.util.UUID;

public interface FundraisingService {

    List<FundraisingResponseDto> getFundraisings();

    Fundraising getFundraisingEntityById(UUID fundraisingId);

    FundraisingResponseDto getFundraisingById(UUID fundraisingId);

    FundraisingResponseDto createFundraising(FundraisingRequestDto fundraisingRequestDto);

    FundraisingResponseDto updateFundraising(UUID fundraisingId, FundraisingRequestDto fundraisingRequestDto);

    void deleteFundraisingById(UUID fundraisingId);
}

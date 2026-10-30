package io.github.listerstellar.fundraising.service.impl;

import io.github.listerstellar.fundraising.dto.FundraisingRequestDto;
import io.github.listerstellar.fundraising.dto.FundraisingResponseDto;
import io.github.listerstellar.fundraising.entity.Customer;
import io.github.listerstellar.fundraising.entity.Fundraising;
import io.github.listerstellar.fundraising.exception.EntityNotFoundException;
import io.github.listerstellar.fundraising.mapper.FundraisingMapper;
import io.github.listerstellar.fundraising.repository.FundraisingRepository;
import io.github.listerstellar.fundraising.service.CustomerService;
import io.github.listerstellar.fundraising.service.FundraisingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class FundraisingServiceImpl implements FundraisingService {

    private final FundraisingRepository fundraisingRepository;
    private final FundraisingMapper fundraisingMapper;
    private final CustomerService customerService;

    @Override
    @Transactional(readOnly = true)
    public List<FundraisingResponseDto> getFundraisings() {
        return fundraisingRepository.findAll().stream()
                .map(fundraisingMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Fundraising getFundraisingEntityById(UUID fundraisingId) {
        return fundraisingRepository.findById(fundraisingId)
                .orElseThrow(() -> new EntityNotFoundException("Fundraising", fundraisingId.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public FundraisingResponseDto getFundraisingById(UUID fundraisingId) {
        return fundraisingMapper.toDto(getFundraisingEntityById(fundraisingId));
    }

    @Override
    public FundraisingResponseDto createFundraising(FundraisingRequestDto fundraisingRequestDto) {
        Customer customer = customerService.getCustomerEntityById(fundraisingRequestDto.creatorId());
        Fundraising fundraising = fundraisingMapper.toEntity(fundraisingRequestDto, customer);
        return fundraisingMapper.toDto(fundraisingRepository.save(fundraising));
    }

    @Override
    public FundraisingResponseDto updateFundraising(UUID fundraisingId, FundraisingRequestDto fundraisingRequestDto) {
        Fundraising fundraising = getFundraisingEntityById(fundraisingId);
        Customer customer = customerService.getCustomerEntityById(fundraisingRequestDto.creatorId());

        fundraising.setCreator(customer);
        fundraising.setName(fundraisingRequestDto.name());
        fundraising.setGoal(fundraisingRequestDto.goal());
        fundraising.setDescription(fundraisingRequestDto.description());

        return fundraisingMapper.toDto(fundraisingRepository.save(fundraising));
    }

    @Override
    public FundraisingResponseDto partialUpdateFundraising(UUID fundraisingId, FundraisingRequestDto fundraisingRequestDto) {
        Fundraising fundraising = getFundraisingEntityById(fundraisingId);

        if (fundraisingRequestDto.creatorId() != null) {
            Customer customer = customerService.getCustomerEntityById(fundraisingRequestDto.creatorId());
            fundraising.setCreator(customer);
        }
        if (fundraisingRequestDto.name() != null) fundraising.setName(fundraisingRequestDto.name());
        if (fundraisingRequestDto.goal() != null) fundraising.setGoal(fundraisingRequestDto.goal());
        if (fundraisingRequestDto.description() != null) fundraising.setDescription(fundraisingRequestDto.description());

        return fundraisingMapper.toDto(fundraisingRepository.save(fundraising));
    }

    @Override
    public void deleteFundraisingById(UUID fundraisingId) {
        if (!fundraisingRepository.existsById(fundraisingId)) {
            throw new EntityNotFoundException("Fundraising", fundraisingId.toString());
        }
        fundraisingRepository.deleteById(fundraisingId);
    }
}

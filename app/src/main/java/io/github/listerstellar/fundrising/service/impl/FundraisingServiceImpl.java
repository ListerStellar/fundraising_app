package io.github.listerstellar.fundrising.service.impl;

import io.github.listerstellar.fundrising.dto.FundraisingRequestDto;
import io.github.listerstellar.fundrising.dto.FundraisingResponseDto;
import io.github.listerstellar.fundrising.entity.Customer;
import io.github.listerstellar.fundrising.entity.Fundraising;
import io.github.listerstellar.fundrising.exception.EntityNotFoundException;
import io.github.listerstellar.fundrising.mapper.FundraisingMapper;
import io.github.listerstellar.fundrising.repository.FundraisingRepository;
import io.github.listerstellar.fundrising.service.CustomerService;
import io.github.listerstellar.fundrising.service.FundraisingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class FundraisingServiceImpl implements FundraisingService {

    private final FundraisingRepository fundraisingRepository;
    private final FundraisingMapper fundraisingMapper;

    private final CustomerService customerService;

    @Override
    public List<FundraisingResponseDto> getFundraisings() {
        return fundraisingRepository.findAll().stream()
                .map(fundraisingMapper::toDto)
                .toList();
    }

    @Override
    public Fundraising getFundraisingEntityById(UUID fundraisingId) {
        return fundraisingRepository.findById(fundraisingId)
                .orElseThrow(() -> new EntityNotFoundException("Fundraising", fundraisingId.toString()));
    }

    @Override
    public FundraisingResponseDto getFundraisingById(UUID fundraisingId) {
        return fundraisingMapper.toDto(getFundraisingEntityById(fundraisingId));
    }

    @Override
    public FundraisingResponseDto createFundraising(FundraisingRequestDto fundraisingRequestDto) {
        return fundraisingMapper.toDto(
                fundraisingRepository.save(
                        fundraisingMapper.toEntity(
                                fundraisingRequestDto,
                                customerService.getCustomerEntityById(fundraisingRequestDto.creatorId())
                        )
                )
        );
    }

    @Override
    public FundraisingResponseDto updateFundraising(UUID fundraisingId, FundraisingRequestDto fundraisingRequestDto) {
        Fundraising fundraising = getFundraisingEntityById(fundraisingId);
        Customer customer = customerService.getCustomerEntityById(fundraisingRequestDto.creatorId());

        fundraising.setCreator(customer);
        fundraising.setName(fundraisingRequestDto.name());
        fundraising.setGoal(fundraising.getGoal());
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
        if (fundraisingRequestDto.goal() != null) fundraising.setGoal(fundraising.getGoal());
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

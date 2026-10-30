package io.github.listerstellar.fundraising.service;

import io.github.listerstellar.fundraising.dto.FundraisingRequestDto;
import io.github.listerstellar.fundraising.dto.FundraisingResponseDto;
import io.github.listerstellar.fundraising.entity.Customer;
import io.github.listerstellar.fundraising.entity.Fundraising;
import io.github.listerstellar.fundraising.exception.EntityNotFoundException;
import io.github.listerstellar.fundraising.mapper.FundraisingMapper;
import io.github.listerstellar.fundraising.repository.FundraisingRepository;
import io.github.listerstellar.fundraising.service.impl.FundraisingServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundraisingServiceImplTest {

    @Mock
    private FundraisingRepository fundraisingRepository;

    @Mock
    private FundraisingMapper fundraisingMapper;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private FundraisingServiceImpl fundraisingService;

    @Test
    @DisplayName("createFundraising creates and persists fundraising with resolved creator")
    void createFundraising_success() {
        UUID creatorId = UUID.randomUUID();
        Customer creator = Customer.builder().id(creatorId).username("creator").build();
        FundraisingRequestDto requestDto = new FundraisingRequestDto("Campaign", new BigDecimal("1000.00"), "Description", creatorId);

        Fundraising entity = Fundraising.builder().id(UUID.randomUUID()).name("Campaign").goal(new BigDecimal("1000.00")).creator(creator).build();
        FundraisingResponseDto responseDto = new FundraisingResponseDto(entity.getId(), "Campaign", new BigDecimal("1000.00"), "Description", creatorId, Set.of());

        when(customerService.getCustomerEntityById(creatorId)).thenReturn(creator);
        when(fundraisingMapper.toEntity(requestDto, creator)).thenReturn(entity);
        when(fundraisingRepository.save(entity)).thenReturn(entity);
        when(fundraisingMapper.toDto(entity)).thenReturn(responseDto);

        FundraisingResponseDto result = fundraisingService.createFundraising(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Campaign");
        verify(fundraisingRepository).save(entity);
    }

    @Test
    @DisplayName("updateFundraising updates name and goal properly")
    void updateFundraising_updatesGoalCorrectly() {
        UUID id = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        Customer creator = Customer.builder().id(creatorId).build();
        Fundraising existing = Fundraising.builder().id(id).name("Old Name").goal(new BigDecimal("500.00")).creator(creator).build();

        BigDecimal newGoal = new BigDecimal("2500.00");
        FundraisingRequestDto updateDto = new FundraisingRequestDto("New Name", newGoal, "New Desc", creatorId);
        FundraisingResponseDto responseDto = new FundraisingResponseDto(id, "New Name", newGoal, "New Desc", creatorId, Collections.emptySet());

        when(fundraisingRepository.findById(id)).thenReturn(Optional.of(existing));
        when(customerService.getCustomerEntityById(creatorId)).thenReturn(creator);
        when(fundraisingRepository.save(existing)).thenReturn(existing);
        when(fundraisingMapper.toDto(existing)).thenReturn(responseDto);

        FundraisingResponseDto result = fundraisingService.updateFundraising(id, updateDto);

        assertThat(result.goal()).isEqualTo(newGoal);
        assertThat(existing.getGoal()).isEqualTo(newGoal);
    }

    @Test
    @DisplayName("getFundraisingById throws EntityNotFoundException when campaign does not exist")
    void getFundraisingById_notFound_throwsException() {
        UUID campaignId = UUID.randomUUID();
        when(fundraisingRepository.findById(campaignId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fundraisingService.getFundraisingById(campaignId))
                .isInstanceOf(EntityNotFoundException.class);
    }
}

package io.github.listerstellar.fundraising.mapper;

import io.github.listerstellar.fundraising.dto.FundraisingRequestDto;
import io.github.listerstellar.fundraising.dto.FundraisingResponseDto;
import io.github.listerstellar.fundraising.entity.Customer;
import io.github.listerstellar.fundraising.entity.Fundraising;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FundraisingMapper {

    public FundraisingResponseDto toDto(Fundraising fundraising) {
        if (fundraising == null) { return null; }

        Set<UUID> favouriteCustomers = fundraising.getFavouredByCustomers().isEmpty()
                ? new HashSet<>()
                : fundraising.getFavouredByCustomers().stream()
                        .map(Customer::getId)
                        .collect(Collectors.toSet());

        return new FundraisingResponseDto(
                fundraising.getId(),
                fundraising.getName(),
                fundraising.getGoal(),
                fundraising.getDescription(),
                fundraising.getCreator().getId(),
                favouriteCustomers
        );
    }

    public Fundraising toEntity(FundraisingRequestDto fundraisingRequestDto, Customer customer) {
        if (fundraisingRequestDto == null) { return null; }
        return Fundraising.builder()
                .name(fundraisingRequestDto.name())
                .goal(fundraisingRequestDto.goal())
                .description(fundraisingRequestDto.description())
                .creator(customer)
                .build();
    }
}

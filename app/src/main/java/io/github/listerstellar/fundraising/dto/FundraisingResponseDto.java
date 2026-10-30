package io.github.listerstellar.fundraising.dto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record FundraisingResponseDto(UUID id,
                                     String name,
                                     BigDecimal goal,
                                     String description,
                                     UUID creatorId,
                                     Set<UUID> favouriteCustomerIds) {
}

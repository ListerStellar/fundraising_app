package io.github.listerstellar.fundraising.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TopDonorDto(
        UUID customerId,
        String username,
        String firstName,
        String lastName,
        BigDecimal totalDonated
) {
}


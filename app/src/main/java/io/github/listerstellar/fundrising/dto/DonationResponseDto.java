package io.github.listerstellar.fundrising.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DonationResponseDto(UUID id,
                                  BigDecimal amount,
                                  Instant donationDate,
                                  String description,
                                  UUID customerId,
                                  UUID fundraisingId) {
}

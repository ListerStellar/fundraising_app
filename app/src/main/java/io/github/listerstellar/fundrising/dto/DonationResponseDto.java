package io.github.listerstellar.fundrising.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DonationResponseDto(UUID id,
                                  BigDecimal amount,
                                  LocalDateTime donationDate,
                                  String description,
                                  UUID donorId,
                                  UUID fundraisingId) {
}

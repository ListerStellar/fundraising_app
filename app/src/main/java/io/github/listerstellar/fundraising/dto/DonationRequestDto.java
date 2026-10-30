package io.github.listerstellar.fundraising.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record DonationRequestDto(@NotNull
                                 @Min(value = 1, message = "Amount must be >= 1 EUR")
                                 BigDecimal amount,
                                 @Size(max = 1000, message = "Description can have maximum 1000 symbols")
                                 String description,
                                 @NotNull(message = "Donation must have a donor")
                                 UUID customerId,
                                 @NotNull(message = "Donation must belong to the fundraising")
                                 UUID fundraisingId) {
}

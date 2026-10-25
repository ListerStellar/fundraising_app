package io.github.listerstellar.fundrising.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record FundraisingRequestDto(@NotBlank(message = "Name can't be blank")
                                    @Size(max = 256, message = "Name must have between 1 and 256 characters")
                                    String name,
                                    @NotNull
                                    @Min(value = 10, message = "Goal can't be less than 10 EUR")
                                    BigDecimal goal,
                                    @Size(max = 10000, message = "Description can't be longer than 10000 symbols")
                                    String description,
                                    @NotNull(message = "Fundraising must have a creator")
                                    UUID creatorId) {
}

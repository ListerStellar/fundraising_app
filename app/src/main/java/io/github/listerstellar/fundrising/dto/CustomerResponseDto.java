package io.github.listerstellar.fundrising.dto;

import java.util.Set;
import java.util.UUID;

public record CustomerResponseDto(UUID id,
                                  String username,
                                  String email,
                                  String firstName,
                                  String lastName,
                                  String phoneNumber,
                                  Set<UUID> favouriteFundraisingIds) {
}

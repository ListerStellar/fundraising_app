package io.github.listerstellar.fundrising.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRequestDto(@NotBlank(message = "Username can't be blank")
                                 @Size(max = 100, message = "Username can have maximum 100 symbols")
                                 String username,
                                 @NotBlank(message = "Email can't be blank")
                                 @Size(max = 254, message = "Email can have maximum 254 symbols")
                                 @Email(message = "Wrong email format")
                                 String email,
                                 @Size(max = 100, message = "First name can have minimum 1 and maximum 100 symbols")
                                 String firstName,
                                 @Size(max = 100, message = "Second name can have minimum 1 and maximum 100 symbols")
                                 String lastName,
                                 @Pattern(regexp = "^\\+?[0-9]{3,20}$", message = "Phone number must contain between 3 and 20 digits")
                                 String phoneNumber) {
}

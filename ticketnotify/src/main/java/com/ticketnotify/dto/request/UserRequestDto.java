package com.ticketnotify.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequestDto {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, max = 64, message = "Password must have 8 - 64 characters")
    private String password;

}

package com.ticketnotify.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationResponseDto {

    private String id;
    private String accessToken;
    private String refreshToken;

}

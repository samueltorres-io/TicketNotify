package com.ticketnotify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    private String id;
    private String accessToken;
    private String refreshToken;

}

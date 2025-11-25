package com.ticketnotify.config;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -255018516562007488L;
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    

}

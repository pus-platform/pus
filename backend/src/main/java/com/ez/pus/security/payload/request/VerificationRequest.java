package com.ez.pus.security.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationRequest {
    private String username;
    private String code;
}
package com.ez.pus.security.payload.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

  @NotBlank
  @Size(min = 3, max = 20)
  private String username, fullname;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(min = 8, max = 40)
  private String password;
}

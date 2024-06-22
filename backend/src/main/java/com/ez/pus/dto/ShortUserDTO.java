package com.ez.pus.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortUserDTO {
    Long id;
    String image, username, fullname, isActive;
}

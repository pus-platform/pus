package com.ez.pus.user;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortUserDTO {
    Long id;
    String image, username, fullname;
}

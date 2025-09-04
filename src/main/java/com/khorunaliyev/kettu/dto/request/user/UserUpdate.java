package com.khorunaliyev.kettu.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdate {

    @NotNull
    private Long userId;

    @NotBlank
    private String name;

    private String image;

    private String backgroundImage;

    @Size(min = 1, max = 500)
    private String bio;
}

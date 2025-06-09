package com.khorunaliyev.kettu.dto.request;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryNameRequest {
    @NotNull
    @Size(min = 4, max = 56)
    private String name;
}

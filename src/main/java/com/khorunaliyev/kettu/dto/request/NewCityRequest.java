package com.khorunaliyev.kettu.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewCityRequest extends CityNameRequest{
    @NotNull
    private Long regionId;
}

package com.khorunaliyev.kettu.dto.request.place;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceUpdateStatusRequest {
    @NotNull
    private String status;

    @Size(min = 1, max = 500)
    private String changeReason;
}

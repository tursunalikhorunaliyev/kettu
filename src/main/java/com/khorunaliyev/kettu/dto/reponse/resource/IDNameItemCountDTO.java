package com.khorunaliyev.kettu.dto.reponse.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IDNameItemCountDTO {
    private Long id;
    private String name;
    private Integer itemCount;
}

package com.mjc.school.service.dto;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public record AuthorDtoRequest(
        @NotNull
        @Min(3)
        @Max(15)
        String name) {


}
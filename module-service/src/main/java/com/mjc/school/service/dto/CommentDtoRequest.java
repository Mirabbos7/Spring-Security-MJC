package com.mjc.school.service.dto;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


public record CommentDtoRequest(
        @NotNull
        @Min(5)
        @Max(255)
        String content,
        @NotNull
        Long newsId
) {


}

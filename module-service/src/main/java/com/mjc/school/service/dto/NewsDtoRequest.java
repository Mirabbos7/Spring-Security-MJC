package com.mjc.school.service.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


public record NewsDtoRequest(
        @NotNull
        @Min(5)
        @Max(30)
        String title,
        @NotNull
        @Min(5)
        @Max(255)
        String content,
        @NotNull
        String authorName,
        List<String> tagNames) {
    public NewsDtoRequest {
        if (tagNames == null) {
            tagNames = new ArrayList<>();
        }

    }


}

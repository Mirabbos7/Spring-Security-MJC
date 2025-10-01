package com.mjc.school.dto;

import java.util.List;

public record NewsPageDtoResponse(List<NewsDtoResponse> newsList, long totalNewsCount) {

}

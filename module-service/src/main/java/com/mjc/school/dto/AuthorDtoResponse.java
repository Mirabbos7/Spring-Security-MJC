package com.mjc.school.dto;


public record AuthorDtoResponse(
        Long id,
        String name,
        String createDate,
        String lastUpdateDate) {
}
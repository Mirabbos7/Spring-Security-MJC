package com.mjc.school.service.dto;


public record CommentDtoResponse(
        Long id,
        String content,
        String created,
        String modified,
        Long newsId) {
}


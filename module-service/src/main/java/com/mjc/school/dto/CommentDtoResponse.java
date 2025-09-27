package com.mjc.school.dto;


public record CommentDtoResponse(
        Long id,
        String content,
        String created,
        String modified,
        Long newsId) {
}


package com.mjc.school.service.dto;

import java.util.List;


public record NewsDtoResponse(
        Long id,
        String title,
        String content,
        String createDate,
        String lastUpdateDate,
        AuthorDtoResponse authorDtoResponse,
        List<TagDtoResponse> tagList,
        List<CommentDtoResponse> commentList) {


}
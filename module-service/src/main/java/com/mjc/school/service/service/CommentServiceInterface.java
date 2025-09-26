package com.mjc.school.service.service;

import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;
import com.mjc.school.service.security.service.BaseService;

import java.util.List;

public interface CommentServiceInterface extends BaseService<CommentDtoRequest, CommentDtoResponse, Long> {
    List<CommentDtoResponse> readListOfCommentsByNewsId(Long newsId);
}

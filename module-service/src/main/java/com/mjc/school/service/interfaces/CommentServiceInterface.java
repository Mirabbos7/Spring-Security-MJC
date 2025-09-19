package com.mjc.school.service.interfaces;

import com.mjc.school.service.dto.CommentDtoRequest;
import com.mjc.school.service.dto.CommentDtoResponse;

import java.util.List;

public interface CommentServiceInterface extends BaseService<CommentDtoRequest, CommentDtoResponse, Long> {
    List<CommentDtoResponse> readListOfCommentsByNewsId(Long newsId);
}

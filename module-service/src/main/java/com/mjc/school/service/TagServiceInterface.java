package com.mjc.school.service;

import com.mjc.school.dto.TagDtoRequest;
import com.mjc.school.dto.TagDtoResponse;
import com.mjc.school.security.service.BaseService;

import java.util.List;

public interface TagServiceInterface extends BaseService<TagDtoRequest, TagDtoResponse, Long> {
    List<TagDtoResponse> readListOfTagsByNewsId(Long newsId);
}

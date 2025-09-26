package com.mjc.school.service.service;

import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import com.mjc.school.service.security.service.BaseService;

import java.util.List;

public interface TagServiceInterface extends BaseService<TagDtoRequest, TagDtoResponse, Long> {
    List<TagDtoResponse> readListOfTagsByNewsId(Long newsId);
}

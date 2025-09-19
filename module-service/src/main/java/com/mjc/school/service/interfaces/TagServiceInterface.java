package com.mjc.school.service.interfaces;

import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;

import java.util.List;

public interface TagServiceInterface extends BaseService<TagDtoRequest, TagDtoResponse, Long> {
    List<TagDtoResponse> readListOfTagsByNewsId(Long newsId);
}

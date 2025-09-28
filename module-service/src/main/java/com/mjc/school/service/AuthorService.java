package com.mjc.school.service;

import com.mjc.school.dto.AuthorDtoRequest;
import com.mjc.school.dto.AuthorDtoResponse;
import com.mjc.school.security.service.BaseService;

public interface AuthorService extends BaseService<AuthorDtoRequest, AuthorDtoResponse, Long> {
    AuthorDtoResponse readAuthorByNewsId(Long newsId);
}

package com.mjc.school.service.service;

import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;
import com.mjc.school.service.security.service.BaseService;

public interface AuthorServiceInterface extends BaseService<AuthorDtoRequest, AuthorDtoResponse, Long> {
    AuthorDtoResponse readAuthorByNewsId(Long newsId);
}

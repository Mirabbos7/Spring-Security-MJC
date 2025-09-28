package com.mjc.school.service;

import com.mjc.school.dto.NewsPageDtoResponse;

import java.util.List;

public interface NewsService<NewsDtoRequest, NewsDtoResponse, Long>{
    List<NewsDtoResponse> readListOfNewsByParams(List<String> tagName, List<Long> tagId, String authorName, String title, String content);

    NewsDtoResponse readById(Long id);

    NewsDtoResponse create(NewsDtoRequest createRequest);

    NewsDtoResponse update(Long id, NewsDtoRequest updateRequest);

    boolean deleteById(Long id);
    NewsPageDtoResponse readAll(int page, int size, String sortBy);
}

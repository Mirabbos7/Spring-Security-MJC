package com.mjc.school.interfaces;

import com.mjc.school.dto.NewsPageDtoResponse;
import org.springframework.hateoas.EntityModel;

public interface NewsControllerInterface <NewsDtoRequest, NewsDtoResponse, Long>{
    EntityModel<NewsPageDtoResponse> readAll(int page, int size, String sortBy);

    EntityModel<NewsDtoResponse> readById(Long id);

    EntityModel<NewsDtoResponse> create(NewsDtoRequest createRequest);

    EntityModel<NewsDtoResponse> update(Long id, NewsDtoRequest updateRequest);

    void deleteById(Long id);
}

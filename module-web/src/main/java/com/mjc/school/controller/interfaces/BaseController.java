package com.mjc.school.controller.interfaces;

import org.springframework.hateoas.EntityModel;

import java.util.List;

public interface BaseController<T, R, K> {

    List<R> readAll(int page, int size, String sortBy);

    EntityModel<R> readById(K id);

    EntityModel<R> create(T createRequest);

    EntityModel<R> update(K id, T updateRequest);

    void deleteById(K id);
}

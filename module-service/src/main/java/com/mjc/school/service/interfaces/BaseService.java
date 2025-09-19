package com.mjc.school.service.interfaces;

import java.util.List;

public interface BaseService<T, R, K> {
    List<R> readAll(int page, int size, String sortBy);

    R readById(K id);

    R create(T createRequest);

    R update(K id, T updateRequest);

    boolean deleteById(K id);
}

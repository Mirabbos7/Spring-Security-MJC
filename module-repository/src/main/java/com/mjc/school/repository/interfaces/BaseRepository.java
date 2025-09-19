package com.mjc.school.repository.interfaces;


import java.util.List;
import java.util.Optional;

public interface BaseRepository<T extends BaseEntity<K>, K> {

    List<T> readAll(int page, int size, String sortBy);

    Optional<T> readById(K id);

    T create(T entity);

    T update(T entity);

    boolean deleteById(K id);

    boolean existById(K id);

    T getReference(K id);
}

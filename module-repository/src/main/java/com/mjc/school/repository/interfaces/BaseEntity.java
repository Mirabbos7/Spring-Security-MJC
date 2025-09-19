package com.mjc.school.repository.interfaces;

public interface BaseEntity<K> {

    K getId();

    void setId(K id);
}

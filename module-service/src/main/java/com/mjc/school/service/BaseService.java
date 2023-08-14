package com.mjc.school.service;

import java.util.List;

public interface BaseService<T, R, K> {
    List<R> readAll();

    List<R> readAll(int limit, int offset, String orderBy);

    R readById(K id);

    R create(T createRequest);

    R update(T updateRequest);

    boolean deleteById(K id);
}

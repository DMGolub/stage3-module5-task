package com.mjc.school.controller;

import com.mjc.school.service.validator.annotation.Min;
import com.mjc.school.service.validator.annotation.NotNull;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.mjc.school.service.constants.Constants.ID_MIN_VALUE;

@SuppressWarnings("unused")
public interface BaseController <R, T, K> {

	ResponseEntity<List<R>> readAll(int limit, int offset, String orderBy);

	ResponseEntity<R> readById(K id);

	ResponseEntity<R> create(T createRequest);

	ResponseEntity<R> update(K id, T updateRequest);

	void deleteById(@NotNull @Min(ID_MIN_VALUE) K id);
}
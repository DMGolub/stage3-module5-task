package com.mjc.school.service;

import com.mjc.school.service.dto.AuthorRequestDto;
import com.mjc.school.service.dto.AuthorResponseDto;

public interface AuthorService extends BaseService<AuthorRequestDto, AuthorResponseDto, Long> {

	AuthorResponseDto readAuthorByNewsId(final Long newsId);
}
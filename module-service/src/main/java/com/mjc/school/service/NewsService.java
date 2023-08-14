package com.mjc.school.service;

import com.mjc.school.service.dto.NewsRequestDto;
import com.mjc.school.service.dto.NewsResponseDto;
import com.mjc.school.service.query.NewsQueryParams;

import java.util.List;

public interface NewsService extends BaseService<NewsRequestDto, NewsResponseDto, Long> {

	List<NewsResponseDto> readNewsByParams(NewsQueryParams newsQueryParams);
}
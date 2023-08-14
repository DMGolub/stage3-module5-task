package com.mjc.school.repository;

import com.mjc.school.repository.model.News;
import com.mjc.school.repository.query.NewsSearchQueryParams;

import java.util.List;

public interface NewsRepository extends BaseRepository<News, Long> {

	List<News> readByParams(NewsSearchQueryParams newsSearchQueryParams);
}
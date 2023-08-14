package com.mjc.school.repository.impl;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.News;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.repository.query.NewsSearchQueryParams;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class NewsRepositoryImpl extends AbstractRepository<News, Long> implements NewsRepository {

	@Override
	public List<News> readByParams(final NewsSearchQueryParams searchParams) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		final CriteriaQuery<News> query = criteriaBuilder.createQuery(News.class);
		final Root<News> root = query.from(News.class);

		final List<Predicate> predicates = new ArrayList<>();

		if (searchParams.tagNames() != null || searchParams.tagIds() != null) {
			Join<Tag, News> newsTags = root.join("tags");
			if (searchParams.tagNames() != null) {
				predicates.add(newsTags.get("name").in(searchParams.tagNames()));
			}
			if (searchParams.tagIds() != null) {
				predicates.add(newsTags.get("id").in(searchParams.tagIds()));
			}
		}

		if (searchParams.authorName() != null) {
			Join<Author, News> newsAuthor = root.join("author");
			predicates.add(criteriaBuilder.equal(newsAuthor.get("name"), searchParams.authorName()));
		}

		if (searchParams.title() != null) {
			predicates.add(criteriaBuilder.like(root.get("title"), "%" + searchParams.title() + "%"));
		}

		if (searchParams.content() != null) {
			predicates.add(criteriaBuilder.like(root.get("content"), "%" + searchParams.content() + "%"));
		}

		query.select(root).distinct(true).where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(query).getResultList();
	}
}
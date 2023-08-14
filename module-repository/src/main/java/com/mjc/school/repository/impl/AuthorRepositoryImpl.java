package com.mjc.school.repository.impl;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.model.Author;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AuthorRepositoryImpl extends AbstractRepository<Author, Long> implements AuthorRepository {

	@Override
	public Optional<Author> readAuthorByNewsId(final Long newsId) {
		if (newsId != null) {
			final String query = "SELECT a FROM Author AS a WHERE a.id = " +
				"(SELECT n.author.id FROM News AS n WHERE n.id = :newsId)";
			final Author author = entityManager.createQuery(query, Author.class)
				.setParameter("newsId", newsId)
				.getSingleResult();
			return Optional.ofNullable(author);
		}
		return Optional.empty();
	}
}
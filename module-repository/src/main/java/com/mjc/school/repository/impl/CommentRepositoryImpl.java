package com.mjc.school.repository.impl;

import com.mjc.school.repository.CommentRepository;
import com.mjc.school.repository.model.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("unchecked")
@Repository
public class CommentRepositoryImpl extends AbstractRepository<Comment, Long> implements CommentRepository {

	@Override
	public List<Comment> readCommentsByNewsId(final Long newsId) {
		final var query = entityManager.createQuery("SELECT c FROM Comment AS c WHERE c.news.id = :newsId");
		query.setParameter("newsId", newsId);
		return query.getResultList();
	}
}
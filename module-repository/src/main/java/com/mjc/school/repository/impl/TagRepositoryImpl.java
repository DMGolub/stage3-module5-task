package com.mjc.school.repository.impl;

import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.model.Tag;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class TagRepositoryImpl extends AbstractRepository<Tag, Long> implements TagRepository {

	@Override
	public List<Tag> readTagsByNewsId(final Long newsId) {
		if (newsId != null) {
			final String query = "SELECT n.tags FROM News AS n WHERE n.id = :newsId";
			return entityManager.createQuery(query)
				.setParameter("newsId", newsId)
				.getResultList();
		}
		return Collections.emptyList();
	}
}
package com.mjc.school.repository.util;

import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.News;
import com.mjc.school.repository.model.Tag;

import java.time.LocalDateTime;

public final class Util {

	private static long authorCount;
	private static long newsCount;
	private static long tagCount;

	private Util() {
		// Empty. Hides default public constructor
	}

	public static Author createTestAuthor(final Long authorId) {
		return new Author(
			authorId,
			"Author Name" + authorCount++,
			LocalDateTime.of(2023, 7, 17, 16, 30, 0),
			LocalDateTime.of(2023, 7, 17, 16, 30, 0)
		);
	}

	public static News createTestNews(final Long newsId) {
		return new News(
			newsId,
			"Title" + newsCount++,
			"Content" + newsCount++,
			LocalDateTime.of(2023, 7, 17, 16, 30, 0),
			LocalDateTime.of(2023, 7, 17, 16, 30, 0),
			null,
			null,
			null
		);
	}

	public static Tag createTestTag(final Long tagId) {
		return new Tag(tagId, "Tag name" + tagCount++);
	}
}
package com.mjc.school.service.dto;

import com.mjc.school.service.validator.annotation.Max;
import com.mjc.school.service.validator.annotation.Min;
import com.mjc.school.service.validator.annotation.NotNull;
import com.mjc.school.service.validator.annotation.Size;

import java.util.List;

import static com.mjc.school.service.constants.Constants.ID_MIN_VALUE;
import static com.mjc.school.service.constants.Constants.ID_VALUE_MAX;
import static com.mjc.school.service.constants.Constants.NEWS_CONTENT_LENGTH_MAX;
import static com.mjc.school.service.constants.Constants.NEWS_CONTENT_LENGTH_MIN;
import static com.mjc.school.service.constants.Constants.NEWS_TITLE_LENGTH_MAX;
import static com.mjc.school.service.constants.Constants.NEWS_TITLE_LENGTH_MIN;

public record NewsRequestDto(

	@Min(ID_MIN_VALUE)
	@Max(ID_VALUE_MAX)
	Long id,
	@NotNull
	@Size(min = NEWS_TITLE_LENGTH_MIN, max = NEWS_TITLE_LENGTH_MAX)
	String title,
	@NotNull
	@Size(min = NEWS_CONTENT_LENGTH_MIN, max = NEWS_CONTENT_LENGTH_MAX)
	String content,
	@NotNull
	@Min(ID_MIN_VALUE)
	@Max(ID_VALUE_MAX)
	Long authorId,
	List<Long> tags
) {
	// Empty
}
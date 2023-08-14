package com.mjc.school.service.dto;

import com.mjc.school.service.validator.annotation.Max;
import com.mjc.school.service.validator.annotation.Min;
import com.mjc.school.service.validator.annotation.NotNull;
import com.mjc.school.service.validator.annotation.Size;

import static com.mjc.school.service.constants.Constants.ID_MIN_VALUE;
import static com.mjc.school.service.constants.Constants.ID_VALUE_MAX;
import static com.mjc.school.service.constants.Constants.TAG_NAME_LENGTH_MAX;
import static com.mjc.school.service.constants.Constants.TAG_NAME_LENGTH_MIN;

public record TagRequestDto(
	@Min(ID_MIN_VALUE)
	@Max(ID_VALUE_MAX)
	Long id,
	@NotNull
	@Size(min = TAG_NAME_LENGTH_MIN, max = TAG_NAME_LENGTH_MAX)
	String name
) {
	// Empty
}
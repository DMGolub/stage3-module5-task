package com.mjc.school.service;

import com.mjc.school.service.dto.TagRequestDto;
import com.mjc.school.service.dto.TagResponseDto;

import java.util.List;

public interface TagService extends BaseService<TagRequestDto, TagResponseDto, Long> {

	List<TagResponseDto> readTagsByNewsId(Long newsId);
}
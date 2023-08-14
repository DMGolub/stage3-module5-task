package com.mjc.school.service;

import com.mjc.school.service.dto.CommentRequestDto;
import com.mjc.school.service.dto.CommentResponseDto;

import java.util.List;

public interface CommentService extends BaseService<CommentRequestDto, CommentResponseDto, Long> {

	List<CommentResponseDto> readCommentsByNewsId(final Long newsId);
}
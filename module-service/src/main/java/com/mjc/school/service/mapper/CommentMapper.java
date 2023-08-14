package com.mjc.school.service.mapper;

import com.mjc.school.repository.model.Comment;
import com.mjc.school.service.dto.CommentRequestDto;
import com.mjc.school.service.dto.CommentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

	@Mapping(source = "comment.news.id", target = "newsId")

	CommentResponseDto modelToDto(Comment comment);

	List<CommentResponseDto> modelListToDtoList(List<Comment> comments);

	@Mapping(target = "createDate", ignore = true)
	@Mapping(target = "lastUpdateDate", ignore = true)
	@Mapping(target = "news", ignore = true)
	Comment dtoToModel(CommentRequestDto request);
}
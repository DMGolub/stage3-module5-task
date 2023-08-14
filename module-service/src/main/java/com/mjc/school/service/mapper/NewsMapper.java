package com.mjc.school.service.mapper;

import com.mjc.school.repository.model.Comment;
import com.mjc.school.repository.model.News;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.repository.query.NewsSearchQueryParams;
import com.mjc.school.service.dto.NewsRequestDto;
import com.mjc.school.service.dto.NewsResponseDto;
import com.mjc.school.service.query.NewsQueryParams;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NewsMapper {

	@Mapping(source = "news.author.id", target = "authorId")
	@Mapping(source = "news.tags", target = "tags")
	@Mapping(source = "news.comments", target = "comments")
	@Mapping(target = "s", ignore = true)
	@Mapping(target = "ments", ignore = true)
	NewsResponseDto modelToDto(News news);

	default Long map(Comment comment) {
		return comment.getId();
	}

	default Long map(Tag tag) {
		return tag.getId();
	}

	List<NewsResponseDto> modelListToDtoList(List<News> news);

	@Mapping(target = "createDate", ignore = true)
	@Mapping(target = "lastUpdateDate", ignore = true)
	@Mapping(target = "author", ignore = true)
	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "comments", ignore = true)
	News dtoToModel(NewsRequestDto request);

	@Mapping(target = "names", ignore = true)
	@Mapping(target = "ids", ignore = true)
	NewsSearchQueryParams mapQueryParams(NewsQueryParams newsQueryParams);
}
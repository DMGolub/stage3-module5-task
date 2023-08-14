package com.mjc.school.service.impl;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.exception.EntityConstraintViolationRepositoryException;
import com.mjc.school.repository.model.Author;
import com.mjc.school.repository.model.News;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.service.NewsService;
import com.mjc.school.service.dto.NewsRequestDto;
import com.mjc.school.service.dto.NewsResponseDto;
import com.mjc.school.service.exception.EntityConstraintViolationServiceException;
import com.mjc.school.service.exception.EntityNotFoundException;
import com.mjc.school.service.mapper.NewsMapper;
import com.mjc.school.service.query.NewsQueryParams;
import com.mjc.school.service.validator.annotation.Min;
import com.mjc.school.service.validator.annotation.NotNull;
import com.mjc.school.service.validator.annotation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mjc.school.service.constants.Constants.AUTHOR_ENTITY_NAME;
import static com.mjc.school.service.constants.Constants.ID_MIN_VALUE;
import static com.mjc.school.service.constants.Constants.NEWS_ENTITY_NAME;
import static com.mjc.school.service.constants.Constants.TAG_ENTITY_NAME;
import static com.mjc.school.service.exception.ServiceErrorCode.ENTITY_NOT_FOUND_BY_ID;
import static com.mjc.school.service.exception.ServiceErrorCode.NEWS_CONSTRAINT_VIOLATION;

@Service
public class NewsServiceImpl implements NewsService {

	private final AuthorRepository authorRepository;
	private final NewsRepository newsRepository;
	private final TagRepository tagRepository;
	private final NewsMapper mapper;

	public NewsServiceImpl(
		final AuthorRepository authorRepository,
		final NewsRepository newsRepository,
		final TagRepository tagRepository,
		final NewsMapper mapper
	) {
		this.authorRepository = authorRepository;
		this.newsRepository = newsRepository;
		this.tagRepository = tagRepository;
		this.mapper = mapper;
	}

	@Override
	@Transactional
	public NewsResponseDto create(@NotNull @Valid final NewsRequestDto request) throws EntityNotFoundException {
		final News news = mapper.dtoToModel(request);
		news.setAuthor(getAuthor(request.authorId()));
		final List<Long> tagIds = request.tags();
		if (tagIds != null) {
			news.setTags(getTags(tagIds));
		}
		news.setComments(new ArrayList<>());
		final News result;
		try {
			result = newsRepository.create(news);
		} catch (final EntityConstraintViolationRepositoryException e) {
			throw new EntityConstraintViolationServiceException(
				NEWS_CONSTRAINT_VIOLATION.getMessage(),
				NEWS_CONSTRAINT_VIOLATION.getCode()
			);
		}
		return mapper.modelToDto(result);
	}

	@Override
	@Transactional(readOnly = true)
	public NewsResponseDto readById(@NotNull @Min(ID_MIN_VALUE) final Long id) throws EntityNotFoundException {
		final Optional<News> news = newsRepository.readById(id);
		if (news.isPresent()) {
			return mapper.modelToDto(news.get());
		} else {
			throw new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), NEWS_ENTITY_NAME, id),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<NewsResponseDto> readNewsByParams(@NotNull final NewsQueryParams newsQueryParams) {
		return mapper.modelListToDtoList(
			newsRepository.readByParams(mapper.mapQueryParams(newsQueryParams)));
	}

	@Override
	@Transactional(readOnly = true)
	public List<NewsResponseDto> readAll() {
		return mapper.modelListToDtoList(newsRepository.readAll());
	}

	@Override
	@Transactional(readOnly = true)
	public List<NewsResponseDto> readAll(final int limit, final int offset, final String orderBy) {
		return mapper.modelListToDtoList(newsRepository.readAll(limit, offset, orderBy));
	}

	@Override
	@Transactional
	public NewsResponseDto update(@NotNull @Valid final NewsRequestDto request) throws EntityNotFoundException {
		final Long id = request.id();
		if (id != null) {
			final Optional<News> news = newsRepository.readById(id);
			if (news.isPresent()) {
				final News updated = news.get();
				updated.setTitle(request.title());
				updated.setContent(request.content());
				updated.setAuthor(getAuthor(request.authorId()));
				updated.setTags(getTags(request.tags()));
				final News result;
				try {
					result = newsRepository.update(updated);
				} catch (final EntityConstraintViolationRepositoryException e) {
					throw new EntityConstraintViolationServiceException(
						NEWS_CONSTRAINT_VIOLATION.getMessage(),
						NEWS_CONSTRAINT_VIOLATION.getCode()
					);
				}
				return mapper.modelToDto(result);
			}
		}
		throw new EntityNotFoundException(
			String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), NEWS_ENTITY_NAME, id),
			ENTITY_NOT_FOUND_BY_ID.getCode()
		);
	}

	@Override
	@Transactional
	public boolean deleteById(@NotNull @Min(ID_MIN_VALUE) final Long id) throws EntityNotFoundException {
		if (newsRepository.existById(id)) {
			return newsRepository.deleteById(id);
		}
		throw new EntityNotFoundException(
			String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), NEWS_ENTITY_NAME, id),
			ENTITY_NOT_FOUND_BY_ID.getCode()
		);
	}

	private Author getAuthor(final Long authorId) throws EntityNotFoundException {
		if (authorId != null) {
			final Optional<Author> author = authorRepository.readById(authorId);
			if (author.isPresent()) {
				return author.get();
			}
		}
		throw new EntityNotFoundException(
			String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), AUTHOR_ENTITY_NAME, authorId),
			ENTITY_NOT_FOUND_BY_ID.getCode()
		);
	}

	private List<Tag> getTags(final List<Long> tagIds) throws EntityNotFoundException {
		final List<Tag> tags = new ArrayList<>();
		for (Long tagId : tagIds) {
			tags.add(getTag(tagId));
		}
		return tags;
	}

	private Tag getTag(final Long tagId) throws EntityNotFoundException {
		if (tagId != null) {
			final Optional<Tag> tag = tagRepository.readById(tagId);
			if (tag.isPresent()) {
				return tag.get();
			}
		}
		throw new EntityNotFoundException(
			String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), TAG_ENTITY_NAME, tagId),
			ENTITY_NOT_FOUND_BY_ID.getCode()
		);
	}
}
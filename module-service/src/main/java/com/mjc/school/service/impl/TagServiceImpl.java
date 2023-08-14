package com.mjc.school.service.impl;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.exception.EntityConstraintViolationRepositoryException;
import com.mjc.school.repository.model.News;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.repository.query.NewsSearchQueryParams;
import com.mjc.school.service.TagService;
import com.mjc.school.service.dto.TagRequestDto;
import com.mjc.school.service.dto.TagResponseDto;
import com.mjc.school.service.exception.EntityConstraintViolationServiceException;
import com.mjc.school.service.exception.EntityNotFoundException;
import com.mjc.school.service.mapper.TagMapper;
import com.mjc.school.service.validator.annotation.Min;
import com.mjc.school.service.validator.annotation.NotNull;
import com.mjc.school.service.validator.annotation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.mjc.school.service.constants.Constants.ID_MIN_VALUE;
import static com.mjc.school.service.constants.Constants.NEWS_ENTITY_NAME;
import static com.mjc.school.service.constants.Constants.TAG_ENTITY_NAME;
import static com.mjc.school.service.exception.ServiceErrorCode.ENTITY_NOT_FOUND_BY_ID;
import static com.mjc.school.service.exception.ServiceErrorCode.TAG_CONSTRAINT_VIOLATION;

@Service
public class TagServiceImpl implements TagService {

	private final NewsRepository newsRepository;
	private final TagRepository tagRepository;
	private final TagMapper tagMapper;

	public TagServiceImpl(
		final NewsRepository newsRepository,
		final TagRepository tagRepository,
		final TagMapper tagMapper
	) {
		this.newsRepository = newsRepository;
		this.tagRepository = tagRepository;
		this.tagMapper = tagMapper;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TagResponseDto> readAll() {
		return tagMapper.modelListToDtoList(tagRepository.readAll());
	}

	@Override
	@Transactional(readOnly = true)
	public List<TagResponseDto> readAll(final int limit, final int offset, final String orderBy) {
		return tagMapper.modelListToDtoList(tagRepository.readAll(limit, offset, orderBy));
	}

	@Override
	@Transactional(readOnly = true)
	public TagResponseDto readById(@NotNull @Min(ID_MIN_VALUE) final Long id)
			throws EntityNotFoundException {
		final Optional<Tag> tag = tagRepository.readById(id);
		if (tag.isPresent()) {
			return tagMapper.modelToDto(tag.get());
		} else {
			throw new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), TAG_ENTITY_NAME, id),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<TagResponseDto> readTagsByNewsId(@NotNull @Min(ID_MIN_VALUE) final Long newsId) {
		if (newsRepository.existById(newsId)) {
			return tagMapper.modelListToDtoList(tagRepository.readTagsByNewsId(newsId));
		}
		throw new EntityNotFoundException(
			String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), NEWS_ENTITY_NAME, newsId),
			ENTITY_NOT_FOUND_BY_ID.getCode()
		);
	}

	@Override
	@Transactional
	public TagResponseDto create(@NotNull @Valid final TagRequestDto request)
			throws EntityConstraintViolationServiceException {
		try {
			return tagMapper.modelToDto(tagRepository.create(tagMapper.dtoToModel(request)));
		} catch (final EntityConstraintViolationRepositoryException e) {
			throw new EntityConstraintViolationServiceException(
				TAG_CONSTRAINT_VIOLATION.getMessage(),
				TAG_CONSTRAINT_VIOLATION.getCode()
			);
		}
	}

	@Override
	@Transactional
	public TagResponseDto update(@NotNull @Valid final TagRequestDto request)
			throws EntityConstraintViolationServiceException {
		final Long id = request.id();
		if (id != null && tagRepository.existById(id)) {
			final Tag tag = tagMapper.dtoToModel(request);
			final Tag result;
			try {
				result = tagRepository.update(tag);
			} catch (final EntityConstraintViolationRepositoryException e) {
				throw new EntityConstraintViolationServiceException(
					TAG_CONSTRAINT_VIOLATION.getMessage(),
					TAG_CONSTRAINT_VIOLATION.getCode()
				);
			}
			return tagMapper.modelToDto(result);
		} else {
			throw new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), TAG_ENTITY_NAME, id),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			);
		}
	}

	@Override
	@Transactional
	public boolean deleteById(@NotNull @Min(ID_MIN_VALUE) final Long id) throws EntityNotFoundException {
		if (tagRepository.existById(id)) {
			NewsSearchQueryParams params =
				new NewsSearchQueryParams(null, List.of(id), null, null, null);
			final List<News> newsWithTag = newsRepository.readByParams(params);
			for (final News news : newsWithTag) {
				news.getTags().removeIf(t -> id.equals(t.getId()));
				newsRepository.update(news);
			}
			return tagRepository.deleteById(id);
		} else {
			throw new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), TAG_ENTITY_NAME, id),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			);
		}
	}
}
package com.mjc.school.service.impl;

import com.mjc.school.repository.CommentRepository;
import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.model.Comment;
import com.mjc.school.repository.model.News;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.dto.CommentRequestDto;
import com.mjc.school.service.dto.CommentResponseDto;
import com.mjc.school.service.exception.EntityNotFoundException;
import com.mjc.school.service.mapper.CommentMapper;
import com.mjc.school.service.validator.annotation.Min;
import com.mjc.school.service.validator.annotation.NotNull;
import com.mjc.school.service.validator.annotation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.mjc.school.service.constants.Constants.COMMENT_ENTITY_NAME;
import static com.mjc.school.service.constants.Constants.ID_MIN_VALUE;
import static com.mjc.school.service.constants.Constants.NEWS_ENTITY_NAME;
import static com.mjc.school.service.exception.ServiceErrorCode.ENTITY_NOT_FOUND_BY_ID;

@Service
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final NewsRepository newsRepository;
	private final CommentMapper commentMapper;

	public CommentServiceImpl(
		final CommentRepository commentRepository,
		final NewsRepository newsRepository,
		final CommentMapper commentMapper
	) {
		this.commentRepository = commentRepository;
		this.newsRepository = newsRepository;
		this.commentMapper = commentMapper;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CommentResponseDto> readAll() {
		return commentMapper.modelListToDtoList(commentRepository.readAll());
	}

	@Override
	@Transactional(readOnly = true)
	public List<CommentResponseDto> readAll(final int limit, final int offset, final String orderBy) {
		return commentMapper.modelListToDtoList(commentRepository.readAll(limit, offset, orderBy));
	}

	@Override
	@Transactional(readOnly = true)
	public CommentResponseDto readById(@NotNull @Min(ID_MIN_VALUE) final Long id)
			throws EntityNotFoundException {
		final Optional<Comment> comment = commentRepository.readById(id);
		if (comment.isPresent()) {
			return commentMapper.modelToDto(comment.get());
		} else {
			throw new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), COMMENT_ENTITY_NAME, id),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<CommentResponseDto> readCommentsByNewsId(@NotNull @Min(ID_MIN_VALUE) final Long newsId)
			throws EntityNotFoundException {
		if (newsRepository.existById(newsId)) {
			return commentMapper.modelListToDtoList(commentRepository.readCommentsByNewsId(newsId));
		}
		throw new EntityNotFoundException(
			String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), NEWS_ENTITY_NAME, newsId),
			ENTITY_NOT_FOUND_BY_ID.getCode()
		);
	}

	@Override
	@Transactional
	public CommentResponseDto create(@NotNull @Valid final CommentRequestDto request)
			throws EntityNotFoundException {
		final Comment comment = commentMapper.dtoToModel(request);
		comment.setNews(getNews(request.newsId()));
		return commentMapper.modelToDto(commentRepository.create(comment));
	}

	@Override
	@Transactional
	public CommentResponseDto update(@NotNull @Valid final CommentRequestDto request)
			throws EntityNotFoundException {
		final Long id = request.id();
		if (id != null) {
			final Optional<Comment> comment = commentRepository.readById(id);
			if (comment.isPresent()) {
				final Comment updatedComment = comment.get();
				updatedComment.setContent(request.content());
				return commentMapper.modelToDto(commentRepository.update(updatedComment));
			}
		}
		throw new EntityNotFoundException(
			String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), COMMENT_ENTITY_NAME, id),
			ENTITY_NOT_FOUND_BY_ID.getCode()
		);
	}

	@Override
	@Transactional
	public boolean deleteById(@NotNull @Min(ID_MIN_VALUE) final Long id) {
		final Optional<Comment> comment = commentRepository.readById(id);
		if (comment.isPresent()) {
			final News news = comment.get().getNews();
			news.getComments().removeIf(c -> id.equals(c.getId()));
			return commentRepository.deleteById(id);
		}
		throw new EntityNotFoundException(
			String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), COMMENT_ENTITY_NAME, id),
			ENTITY_NOT_FOUND_BY_ID.getCode()
		);
	}

	private News getNews(final Long newsId) throws EntityNotFoundException {
		if (newsId != null) {
			final Optional<News> news = newsRepository.readById(newsId);
			if (news.isPresent()) {
				return news.get();
			}
		}
		throw new EntityNotFoundException(
			String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), NEWS_ENTITY_NAME, newsId),
			ENTITY_NOT_FOUND_BY_ID.getCode()
		);
	}
}
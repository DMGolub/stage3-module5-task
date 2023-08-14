package com.mjc.school.service.impl;

import com.mjc.school.repository.CommentRepository;
import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.model.Comment;
import com.mjc.school.repository.model.News;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.ServiceAopTestConfiguration;
import com.mjc.school.service.dto.CommentRequestDto;
import com.mjc.school.service.dto.CommentResponseDto;
import com.mjc.school.service.exception.ValidationException;
import com.mjc.school.service.mapper.CommentMapper;
import com.mjc.school.service.util.Util;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ServiceAopTestConfiguration.class})
public class CommentServiceImplAopTest {

	@Autowired
	private CommentMapper commentMapper;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private NewsRepository newsRepository;
	@Autowired
	private CommentService commentService;

	@Nested
	class TestCreate {

		@Test
		void create_shouldThrowValidationException_whenRequestIsNull() {
			assertThrows(ValidationException.class, () -> commentService.create(null));
		}

		@Test
		void create_shouldThrowValidationException_whenContentIsNull() {
			CommentRequestDto nullContent = new CommentRequestDto(null, null, null);

			assertThrows(ValidationException.class, () -> commentService.create(nullContent));
		}

		@Test
		void create_shouldThrowValidationException_whenContentViolatesLengthConstraints() {
			CommentRequestDto shortContent = new CommentRequestDto(null, "C", null);
			CommentRequestDto longContent = new CommentRequestDto(null, "C".repeat(300), null);

			assertThrows(ValidationException.class, () -> commentService.create(shortContent));
			assertThrows(ValidationException.class, () -> commentService.create(longContent));
		}

		@Test
		void create_shouldNotThrowValidationException_whenGivenValidComment() {
			final long commentId = 1L;
			final Long newsId = 1L;
			final String content = "Some valid content";
			final News news = Util.createTestNews(newsId);
			final CommentRequestDto request = new CommentRequestDto(null, content, 1L);
			final Comment mappedComment = Util.dtoToComment(request);
			when(commentMapper.dtoToModel(request)).thenReturn(mappedComment);
			when(newsRepository.readById(newsId)).thenReturn(Optional.of(news));
			final Comment savedComment = new Comment(commentId, content, news, null, null);
			when(commentRepository.create(any())).thenReturn(savedComment);
			final CommentResponseDto response = Util.commentToDto(savedComment);
			when(commentMapper.modelToDto(savedComment)).thenReturn(response);

			assertDoesNotThrow(() -> commentService.create(request));
		}
	}

	@Nested
	class TestReadById {

		@Test
		void readById_shouldThrowValidationException_whenIdIsNull() {
			assertThrows(ValidationException.class, () -> commentService.readById(null));
		}

		@Test
		void readById_shouldThrowValidationException_whenIdIsNegative() {
			final long id = -5L;
			assertThrows(ValidationException.class, () -> commentService.readById(id));
		}

		@Test
		void readById_shouldThrowValidationException_whenIdIsZero() {
			final long id = 0;
			assertThrows(ValidationException.class, () -> commentService.readById(id));
		}

		@Test
		void readById_shouldNotThrowValidationException_whenIdIsValid() {
			final long id = 2L;
			final long newsId = 2L;
			final News news = Util.createTestNews(newsId);
			final Comment toBeFound = Util.createTestComment(id);
			toBeFound.setNews(news);
			when(commentRepository.readById(id)).thenReturn(Optional.of(toBeFound));
			final CommentResponseDto expected = Util.commentToDto(toBeFound);
			when(commentMapper.modelToDto(toBeFound)).thenReturn(expected);

			assertDoesNotThrow(() -> commentService.readById(id));
		}
	}

	@Nested
	class TestUpdate {

		@Test
		void update_shouldThrowValidationException_whenRequestIsNull() {
			assertThrows(ValidationException.class, () -> commentService.update(null));
		}

		@Test
		void update_shouldThrowValidationException_whenIdIsNegative() {
			final long id = -5L;
			CommentRequestDto request = Util.createTestCommentRequest(id);

			assertThrows(ValidationException.class, () -> commentService.update(request));
		}

		@Test
		void update_shouldThrowValidationException_whenIdIsZero() {
			final long id = 0;
			CommentRequestDto request = Util.createTestCommentRequest(id);

			assertThrows(ValidationException.class, () -> commentService.update(request));
		}


		@Test
		void update_shouldThrowValidationException_whenContentIsNull() {
			CommentRequestDto nullContent = new CommentRequestDto(1L, null, null);

			assertThrows(ValidationException.class, () -> commentService.update(nullContent));
		}

		@Test
		void update_shouldThrowValidationException_whenContentViolatesLengthConstraints() {
			CommentRequestDto shortContent = new CommentRequestDto(1L, "C", null);
			CommentRequestDto longContent = new CommentRequestDto(1L, "C".repeat(300), null);

			assertThrows(ValidationException.class, () -> commentService.update(shortContent));
			assertThrows(ValidationException.class, () -> commentService.update(longContent));
		}

		@Test
		void update_shouldNotThrowValidationException_whenCommentIsValid() {
			final long id = 1L;
			final long newsId = 1L;
			final News news = Util.createTestNews(newsId);
			final CommentRequestDto request = new CommentRequestDto(id, "Updated content", newsId);
			final Comment updated = new Comment(id, "Updated content", news, null, null);
			when(commentRepository.readById(request.id())).thenReturn(Optional.of(updated));
			when(commentRepository.update(any())).thenReturn(updated);
			final CommentResponseDto response = Util.commentToDto(updated);
			when(commentMapper.modelToDto(updated)).thenReturn(response);

			assertDoesNotThrow(() -> commentService.update(request));
		}
	}

	@Nested
	class TestDeleteById {

		@Test
		void deleteById_shouldThrowValidationException_whenIdIsNull() {
			assertThrows(ValidationException.class, () -> commentService.deleteById(null));
		}

		@Test
		void deleteById_shouldThrowValidationException_whenIdIsNegative() {
			final long id = -5L;
			assertThrows(ValidationException.class, () -> commentService.deleteById(id));
		}

		@Test
		void deleteById_shouldThrowValidationException_whenIdIsZero() {
			final long id = 0;
			assertThrows(ValidationException.class, () -> commentService.deleteById(id));
		}

		@Test
		void deleteById_shouldNotThrowValidationException_whenIdIsValid() {
			final long id = 5L;
			final long newsId = 1L;
			final News news = Util.createTestNews(newsId);
			final Comment comment = new Comment(id, "Content", news, null, null);
			final List<Comment> comments = new ArrayList<>();
			comments.add(comment);
			news.setComments(comments);
			when(commentRepository.readById(id)).thenReturn(Optional.of(comment));
			when(commentRepository.deleteById(id)).thenReturn(true);

			assertDoesNotThrow(() -> commentService.deleteById(id));
		}
	}
}
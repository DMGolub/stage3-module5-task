package com.mjc.school.service.impl;

import com.mjc.school.repository.CommentRepository;
import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.model.Comment;
import com.mjc.school.repository.model.News;
import com.mjc.school.service.dto.CommentRequestDto;
import com.mjc.school.service.dto.CommentResponseDto;
import com.mjc.school.service.exception.EntityNotFoundException;
import com.mjc.school.service.mapper.CommentMapper;
import com.mjc.school.service.util.Util;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

	@Mock
	private NewsRepository newsRepository;
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private CommentMapper commentMapper;
	@InjectMocks
	private CommentServiceImpl commentService;

	@Nested
	class TestCreate {

		@Test
		void create_shouldReturnSavedEntity_whenValidRequestDtoProvided() {
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

			final CommentResponseDto result = commentService.create(request);

			verify(commentMapper, times(1)).dtoToModel(request);
			verify(newsRepository, times(1)).readById(newsId);
			verify(commentRepository, times(1)).create(mappedComment);
			verify(commentMapper, times(1)).modelToDto(savedComment);
			assertEquals(response, result);
		}
	}

	@Nested
	class TestReadById {

		@Test
		void readById_shouldThrowEntityNotFoundException_whenThereIsNoEntityWithGivenId() {
			final long id = 99L;
			when(commentRepository.readById(id)).thenReturn(Optional.empty());

			assertThrows(EntityNotFoundException.class, () -> commentService.readById(id));
			verify(commentRepository, times(1)).readById(any());
			verifyNoInteractions(commentMapper);
		}

		@Test
		void readById_shouldReturnDTO_whenEntityWithGivenIdIsFound() {
			final long id = 2L;
			final Comment toBeFound = Util.createTestComment(id);
			toBeFound.setNews(Util.createTestNews(2L));
			when(commentRepository.readById(id)).thenReturn(Optional.of(toBeFound));
			final CommentResponseDto expected = Util.commentToDto(toBeFound);
			when(commentMapper.modelToDto(toBeFound)).thenReturn(expected);

			assertEquals(expected, commentService.readById(id));
			verify(commentRepository, times(1)).readById(id);
			verify(commentMapper, times(1)).modelToDto(toBeFound);
		}
	}

	@Nested
	class TesReadAll {

		@Test
		void readAll_shouldReturnEmptyDTOList_whenRepositoryReturnsEmptyList() {
			when(commentRepository.readAll()).thenReturn(new ArrayList<>());

			final List<CommentResponseDto> expected = new ArrayList<>();

			assertEquals(expected, commentService.readAll());
			verify(commentRepository, times(1)).readAll();
			verify(commentMapper, times(1)).modelListToDtoList(new ArrayList<>());
		}

		@Test
		void readAll_shouldReturnTwoDTO_whenRepositoryReturnsTwoEntities() {
			final Comment comment1 = Util.createTestComment(1L);
			comment1.setNews(Util.createTestNews(1L));
			final Comment comment2 = Util.createTestComment(2L);
			comment2.setNews(Util.createTestNews(2L));

			final List<Comment> allComments = Arrays.asList(comment1, comment2);
			when(commentRepository.readAll()).thenReturn(allComments);
			final List<CommentResponseDto> expected = Util.commentListToDtoList(allComments);
			when(commentMapper.modelListToDtoList(allComments)).thenReturn(expected);

			assertEquals(expected, commentService.readAll());
			verify(commentRepository, times(1)).readAll();
			verify(commentMapper, times(1)).modelListToDtoList(allComments);
		}
	}

	@Nested
	class TestUpdate {

		@Test
		void update_shouldThrowValidationException_whenIdIsNull() {
			CommentRequestDto request = Util.createTestCommentRequest(null);

			assertThrows(EntityNotFoundException.class, () -> commentService.update(request));
		}

		@Test
		void update_shouldThrowEntityNotFoundException_whenEntityWithGivenIdNotFound() {
			final long id = 99L;
			final CommentRequestDto request = Util.createTestCommentRequest(id);
			when(commentRepository.readById(request.id())).thenReturn(Optional.empty());

			assertThrows(EntityNotFoundException.class, () -> commentService.update(request));
			verify(commentRepository, times(1)).readById(request.id());
			verify(commentRepository, times(0)).update(any());
		}

		@Test
		void update_shouldReturnUpdatedEntity_whenValidRequestDtoProvided() {
			final long id = 1L;
			final long newsId = 1L;
			final News news = Util.createTestNews(newsId);
			final CommentRequestDto request = new CommentRequestDto(id, "Updated content", newsId);
			final Comment updated = new Comment(id, "Updated content", news, null, null);
			when(commentRepository.readById(request.id())).thenReturn(Optional.of(updated));
			when(commentRepository.update(any())).thenReturn(updated);
			final CommentResponseDto response = Util.commentToDto(updated);
			when(commentMapper.modelToDto(updated)).thenReturn(response);

			final CommentResponseDto result = commentService.update(request);

			verify(commentRepository, times(1)).readById(request.id());
			verify(commentRepository, times(1)).update(any());
			assertEquals(response, result);
		}
	}

	@Nested
	class TestDeleteById {

		@Test
		void deleteById_shouldThrowEntityNotFoundException_whenThereIsNoEntityWithGivenId() {
			final long id = 5L;
			when(commentRepository.readById(id)).thenReturn(Optional.empty());

			assertThrows(EntityNotFoundException.class, () -> commentService.deleteById(id));
			verify(commentRepository, times(1)).readById(id);
			verify(commentRepository, times(0)).deleteById(id);
		}

		@Test
		void deleteById_shouldReturnTrue_whenRepositoryDeletesEntityById() {
			final long id = 15L;
			final long newsId = 1L;
			final News news = Util.createTestNews(newsId);
			final Comment comment = new Comment(id, "Content", news, null, null);
			final List<Comment> comments = new ArrayList<>();
			comments.add(comment);
			news.setComments(comments);
			when(commentRepository.readById(id)).thenReturn(Optional.of(comment));
			when(commentRepository.deleteById(id)).thenReturn(true);

			assertTrue(commentService.deleteById(id));
			verify(commentRepository, times(1)).readById(id);
			verify(commentRepository, times(1)).deleteById(id);
		}

		@Test
		void deleteById_shouldReturnFalse_whenRepositoryDoesNotDeleteEntityById() {
			final long id = 99L;
			final long newsId = 1L;
			final News news = Util.createTestNews(newsId);
			final Comment comment = new Comment(id, "Content", news, null, null);
			final List<Comment> comments = new ArrayList<>();
			comments.add(comment);
			news.setComments(comments);
			when(commentRepository.readById(id)).thenReturn(Optional.of(comment));
			when(commentRepository.deleteById(id)).thenReturn(false);

			assertFalse(commentService.deleteById(id));
			verify(commentRepository, times(1)).readById(id);
			verify(commentRepository, times(1)).deleteById(id);
		}
	}
}
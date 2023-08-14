package com.mjc.school.service.impl;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.exception.EntityConstraintViolationRepositoryException;
import com.mjc.school.repository.impl.AuthorRepositoryImpl;
import com.mjc.school.repository.impl.NewsRepositoryImpl;
import com.mjc.school.repository.impl.TagRepositoryImpl;
import com.mjc.school.repository.model.News;
import com.mjc.school.service.NewsService;
import com.mjc.school.service.dto.NewsRequestDto;
import com.mjc.school.service.dto.NewsResponseDto;
import com.mjc.school.service.exception.EntityConstraintViolationServiceException;
import com.mjc.school.service.exception.EntityNotFoundException;
import com.mjc.school.service.mapper.NewsMapper;
import com.mjc.school.service.util.Util;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

	private final AuthorRepository authorRepository = mock(AuthorRepositoryImpl.class);
	private final NewsRepository newsRepository = mock(NewsRepositoryImpl.class);
	private final TagRepository tagRepository = mock(TagRepositoryImpl.class);
	private final NewsMapper newsMapper = mock(NewsMapper.class);
	private final NewsService newsService =
		new NewsServiceImpl(authorRepository, newsRepository, tagRepository, newsMapper);

	@Nested
	class TestCreate {

		@Test
		void create_shouldThrowEntityNotFoundException_whenAuthorIdViolatesConstraints() {
			final NewsRequestDto zeroAuthorId = new NewsRequestDto(
				null,
				"Some valid title",
				"Some valid content",
				0L,
				null
			);
			final NewsRequestDto negativeAuthorId = new NewsRequestDto(
				null,
				"Some valid title",
				"Some valid content",
				-2L,
				null
			);

			assertThrows(EntityNotFoundException.class, () -> newsService.create(zeroAuthorId));
			assertThrows(EntityNotFoundException.class, () -> newsService.create(negativeAuthorId));
		}

		@Test
		void create_shouldThrowEntityNotFoundException_whenAuthorNotFound() {
			final long authorId = 99L;
			final NewsRequestDto request = new NewsRequestDto(
				null,
				"Some valid title",
				"Some valid content",
				authorId,
				null
			);

			when(authorRepository.readById(authorId)).thenReturn(Optional.empty());

			assertThrows(EntityNotFoundException.class, () -> newsService.create(request));
			verify(authorRepository, times(1)).readById(authorId);
			verifyNoInteractions(newsRepository);
		}

		@Test
		void create_shouldThrowEntityConstraintViolationServiceException_whenTitleAlreadyExists() {
			final long authorId = 1L;
			final NewsRequestDto request = new NewsRequestDto(
				null,
				"Conflicting title",
				"Some valid content",
				authorId,
				new ArrayList<>()
			);
			final News newsRequest = Util.dtoToNews(request);
			when(authorRepository.readById(authorId))
				.thenReturn(Optional.of(Util.createTestAuthor(authorId)));
			when(newsMapper.dtoToModel(request)).thenReturn(newsRequest);
			when(newsRepository.create(any())).thenThrow(
				new EntityConstraintViolationRepositoryException("Constraint violation"));

			assertThrows(EntityConstraintViolationServiceException.class,
				() -> newsService.create(request));
		}

		@Test
		void create_shouldReturnSavedEntity_whenValidRequestDtoProvided() {
			final long authorId = 1L;
			final NewsRequestDto request = new NewsRequestDto(
				null,
				"Some valid title",
				"Some valid content",
				authorId,
				new ArrayList<>()
			);
			final News newsRequest = Util.dtoToNews(request);
			when(authorRepository.readById(authorId))
				.thenReturn(Optional.of(Util.createTestAuthor(authorId)));
			when(newsMapper.dtoToModel(request)).thenReturn(newsRequest);
			final LocalDateTime date = LocalDateTime.now();
			final News savedNews = new News(
				1L,
				request.title(),
				request.content(),
				date,
				date,
				Util.createTestAuthor(request.authorId()),
				new ArrayList<>(),
				new ArrayList<>()
			);
			when(newsRepository.create(any())).thenReturn(savedNews);
			final NewsResponseDto response = Util.newsToDTO(savedNews);
			when(newsMapper.modelToDto(savedNews)).thenReturn(response);

			final NewsResponseDto result = newsService.create(request);

			verify(authorRepository, times(1)).readById(authorId);
			verify(newsMapper, times(1)).dtoToModel(request);
			verify(newsRepository, times(1)).create(any());
			verify(newsMapper, times(1)).modelToDto(savedNews);
			assertEquals(response, result);
		}
	}

	@Nested
	class TestReadById {

		@Test
		void readById_shouldThrowEntityNotFoundException_whenThereIsNoEntityWithGivenId() {
			final long id = 99L;
			when(newsRepository.readById(id)).thenReturn(Optional.empty());

			assertThrows(EntityNotFoundException.class, () -> newsService.readById(id));
			verify(newsRepository, times(1)).readById(any());
		}

		@Test
		void readById_shouldReturnDTO_whenEntityWithGivenIdIsFound() {
			final long id = 2L;
			final News toBeFound = Util.createTestNews(id);
			when(newsRepository.existById(id)).thenReturn(true);
			when(newsRepository.readById(id)).thenReturn(Optional.of(toBeFound));
			final NewsResponseDto response = Util.newsToDTO(toBeFound);
			when(newsMapper.modelToDto(toBeFound)).thenReturn(response);

			final NewsResponseDto result = newsService.readById(id);

			assertEquals(response, result);
			verify(newsRepository, times(1)).readById(id);
		}
	}

	@Nested
	class TestReadAll {

		@Test
		void readAll_shouldReturnEmptyDTOList_whenRepositoryReturnsEmptyList() {
			final List<News> news = new ArrayList<>();
			when(newsRepository.readAll()).thenReturn(news);
			when(newsMapper.modelListToDtoList(news)).thenReturn(new ArrayList<>());

			final List<NewsResponseDto> expected = new ArrayList<>();

			assertEquals(expected, newsService.readAll());
			verify(newsRepository, times(1)).readAll();
			verify(newsMapper, times(1)).modelListToDtoList(news);
		}

		@Test
		void readAll_shouldReturnTwoDTOs_whenRepositoryReturnsTwoEntities() {
			final List<News> allNews = Arrays.asList(
				Util.createTestNews(1L),
				Util.createTestNews(2L)
			);
			when(newsRepository.readAll()).thenReturn(allNews);
			final List<NewsResponseDto> response = Util.newsListToNewsDTOList(allNews);
			when(newsMapper.modelListToDtoList(allNews)).thenReturn(response);

			final List<NewsResponseDto> result = newsService.readAll();

			assertEquals(response, result);
			verify(newsRepository, times(1)).readAll();
		}
	}

	@Nested
	class TestUpdate {

		@Test
		void update_shouldThrowEntityNotFoundException_whenIdIsNull() {
			final NewsRequestDto request = Util.createTestNewsRequest(null);

			assertThrows(EntityNotFoundException.class, () -> newsService.update(request));
		}

		@Test
		void update_shouldThrowEntityNotFoundException_whenAuthorIdViolatesConstraints() {
			final NewsRequestDto nullAuthorId = new NewsRequestDto(
				1L,
				"Some valid title",
				"Some valid content",
				null,
				null
			);
			final NewsRequestDto negativeAuthorId = new NewsRequestDto(
				1L,
				"Some valid title",
				"Some valid content",
				-2L,
				null
			);

			assertThrows(EntityNotFoundException.class, () -> newsService.update(nullAuthorId));
			assertThrows(EntityNotFoundException.class, () -> newsService.update(negativeAuthorId));
		}

		@Test
		void update_shouldThrowEntityNotFoundException_whenAuthorNotFound() {
			final long id = 5;
			final NewsRequestDto request = Util.createTestNewsRequest(id);
			when(authorRepository.readById(request.authorId())).thenReturn(Optional.empty());

			assertThrows(EntityNotFoundException.class, () -> newsService.update(request));
			verify(newsRepository, times(1)).readById(request.id());
			verify(authorRepository, times(0)).readById(request.authorId());
		}

		@Test
		void update_shouldThrowEntityNotFoundException_whenEntityWithGivenIdNotFound() {
			final long id = 99L;
			final NewsRequestDto request = Util.createTestNewsRequest(id);
			when(newsRepository.readById(request.id())).thenReturn(Optional.empty());
			when(newsRepository.update(any())).thenThrow(new NoSuchElementException());

			assertThrows(EntityNotFoundException.class, () -> newsService.update(request));
			verify(newsRepository, times(1)).readById(request.id());
			verify(authorRepository, times(0)).readById(request.authorId());
			verify(newsRepository, times(0)).update(any());
		}

		@Test
		void update_shouldThrowEntityConstraintViolationServiceException_whenTitleAlreadyExists() {
			final long id = 1L;
			final NewsRequestDto request = new NewsRequestDto(
				id,
				"Conflicting title",
				"Some updated content",
				2L,
				new ArrayList<>()
			);
			final LocalDateTime date = LocalDateTime.now();
			final News oldState = new News(id, "Old title", "Old content",
				date, date, Util.createTestAuthor(2L), new ArrayList<>(), new ArrayList<>());
			when(authorRepository.readById(request.authorId()))
				.thenReturn(Optional.of(Util.createTestAuthor(request.authorId())));
			when(newsRepository.readById(request.id())).thenReturn(Optional.of(oldState));
			final News newsRequest = Util.dtoToNews(request);
			when(newsMapper.dtoToModel(request)).thenReturn(newsRequest);
			when(newsRepository.update(any())).thenThrow(
				new EntityConstraintViolationRepositoryException("Constraint violation"));

			assertThrows(EntityConstraintViolationServiceException.class,
				() -> newsService.update(request));
		}

		@Test
		void update_shouldReturnUpdatedEntity_whenValidRequestDtoProvided() {
			final long id = 1L;
			final NewsRequestDto request = new NewsRequestDto(
				id,
				"Some updated title",
				"Some updated content",
				2L,
				new ArrayList<>()
			);
			final News updated = new News(
				id,
				"Some updated title",
				"Some updated content",
				LocalDateTime.of(2023, 7, 17, 16, 30, 0),
				LocalDateTime.now(),
				Util.createTestAuthor(2L),
				new ArrayList<>(),
				new ArrayList<>()
			);
			when(authorRepository.readById(request.authorId()))
				.thenReturn(Optional.of(Util.createTestAuthor(request.authorId())));
			when(newsRepository.readById(request.id())).thenReturn(Optional.of(updated));
			final News newsRequest = Util.dtoToNews(request);
			when(newsMapper.dtoToModel(request)).thenReturn(newsRequest);
			when(newsRepository.update(any())).thenReturn(updated);
			final NewsResponseDto response = Util.newsToDTO(updated);
			when(newsMapper.modelToDto(updated)).thenReturn(response);

			final NewsResponseDto result = newsService.update(request);

			verify(authorRepository, times(1)).readById(request.authorId());
			verify(newsRepository, times(1)).readById(request.id());
			verify(newsRepository, times(1)).update(any());
			assertEquals(response, result);
		}
	}

	@Nested
	class TestDeleteById {

		@Test
		void deleteById_shouldThrowEntityNotFoundException_whenThereIsNoEntityWithGivenId() {
			final long id = 5L;
			when(newsRepository.existById(id)).thenReturn(false);

			assertThrows(EntityNotFoundException.class, () -> newsService.deleteById(id));
			verify(newsRepository, times(1)).existById(id);
			verify(newsRepository, times(0)).deleteById(id);
		}

		@Test
		void deleteById_shouldReturnTrue_whenRepositoryDeletesEntityById() {
			final long id = 15L;
			when(newsRepository.existById(id)).thenReturn(true);
			when(newsRepository.deleteById(id)).thenReturn(true);

			assertTrue(newsService.deleteById(id));
			verify(newsRepository, times(1)).existById(id);
			verify(newsRepository, times(1)).deleteById(id);
		}

		@Test
		void deleteById_shouldReturnFalse_whenRepositoryDoesNotDeleteEntityById() {
			final long id = 99L;
			when(newsRepository.existById(id)).thenReturn(true);
			when(newsRepository.deleteById(id)).thenReturn(false);

			assertFalse(newsService.deleteById(id));
			verify(newsRepository, times(1)).existById(id);
			verify(newsRepository, times(1)).deleteById(id);
		}
	}
}
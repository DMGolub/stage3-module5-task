package com.mjc.school.service.impl;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.model.News;
import com.mjc.school.service.NewsService;
import com.mjc.school.service.ServiceAopTestConfiguration;
import com.mjc.school.service.dto.NewsRequestDto;
import com.mjc.school.service.exception.ValidationException;
import com.mjc.school.service.mapper.NewsMapper;
import com.mjc.school.service.util.Util;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ServiceAopTestConfiguration.class})
public class NewsServiceImplAopTest {

	@Autowired
	private NewsMapper newsMapper;
	@Autowired
	private AuthorRepository authorRepository;
	@Autowired
	private NewsRepository newsRepository;
	@Autowired
	private NewsService newsService;

	@Nested
	class TestCreate {

		@Test
		void create_shouldThrowValidationException_whenRequestIsNull() {
			assertThrows(ValidationException.class, () -> newsService.create(null));
		}

		@Test
		void create_shouldThrowValidationException_whenTitleIsNull() {
			NewsRequestDto nullTitle = new NewsRequestDto(
				null,
				null,
				"Some valid content",
				2L,
				null
			);

			assertThrows(ValidationException.class, () -> newsService.create(nullTitle));
		}

		@Test
		void create_shouldThrowValidationException_whenTitleViolatesLengthConstraints() {
			NewsRequestDto shortTitle = new NewsRequestDto(
				null,
				"T",
				"Some valid content",
				2L,
				null
			);
			NewsRequestDto longTitle = new NewsRequestDto(
				null,
				"T".repeat(50),
				"Some valid content",
				2L,
				null
			);

			assertThrows(ValidationException.class, () -> newsService.create(shortTitle));
			assertThrows(ValidationException.class, () -> newsService.create(longTitle));
		}

		@Test
		void create_shouldThrowValidationException_whenContentIsNull() {
			NewsRequestDto nullContent = new NewsRequestDto(
				null,
				"Valid title",
				null,
				2L,
				null
			);

			assertThrows(ValidationException.class, () -> newsService.create(nullContent));
		}

		@Test
		void create_shouldThrowValidationException_whenContentViolatesLengthConstraints() {
			NewsRequestDto shortContent = new NewsRequestDto(
				null,
				"Some valid title",
				"C",
				2L,
				null
			);
			NewsRequestDto longContent = new NewsRequestDto(
				null,
				"Some valid title",
				"C".repeat(300),
				2L,
				null
			);

			assertThrows(ValidationException.class, () -> newsService.create(shortContent));
			assertThrows(ValidationException.class, () -> newsService.create(longContent));
		}

		@Test
		void create_shouldNotThrowValidationException_whenNewsIsValid() {
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
			when(newsMapper.modelToDto(savedNews)).thenReturn(Util.newsToDTO(savedNews));

			assertDoesNotThrow(() -> newsService.create(request));
		}
	}

	@Nested
	class TestReadById {

		@Test
		void readById_shouldThrowValidationException_whenIdIsNull() {
			assertThrows(ValidationException.class, () -> newsService.readById(null));
		}

		@Test
		void readById_shouldThrowValidationException_whenIdIsNegative() {
			final long id = -5L;
			assertThrows(ValidationException.class, () -> newsService.readById(id));
		}

		@Test
		void readById_shouldThrowValidationException_whenIdIsZero() {
			final long id = 0;
			assertThrows(ValidationException.class, () -> newsService.readById(id));
		}

		@Test
		void readById_shouldNotThrowValidationException_whenIdIsValid() {
			final long id = 2L;
			final News toBeFound = Util.createTestNews(id);
			when(newsRepository.existById(id)).thenReturn(true);
			when(newsRepository.readById(id)).thenReturn(Optional.of(toBeFound));
			when(newsMapper.modelToDto(toBeFound)).thenReturn(Util.newsToDTO(toBeFound));

			assertDoesNotThrow(() -> newsService.readById(id));
		}
	}

	@Nested
	class TestUpdate {

		@Test
		void update_shouldThrowValidationException_whenRequestIsNull() {
			assertThrows(ValidationException.class, () -> newsService.update(null));
		}

		@Test
		void update_shouldThrowValidationException_whenIdIsNegative() {
			final long id = -5L;
			NewsRequestDto request = Util.createTestNewsRequest(id);

			assertThrows(ValidationException.class, () -> newsService.update(request));
		}

		@Test
		void update_shouldThrowValidationException_whenIdIsZero() {
			final long id = 0;
			NewsRequestDto request = Util.createTestNewsRequest(id);

			assertThrows(ValidationException.class, () -> newsService.update(request));
		}

		@Test
		void update_shouldThrowValidationException_whenTitleIsNull() {
			NewsRequestDto nullTitle = new NewsRequestDto(
				1L,
				null,
				"Some valid content",
				2L,
				null
			);

			assertThrows(ValidationException.class, () -> newsService.update(nullTitle));
		}

		@Test
		void update_shouldThrowValidationException_whenTitleViolatesLengthConstraints() {
			NewsRequestDto shortTitle = new NewsRequestDto(
				1L,
				"T",
				"Some valid content",
				2L,
				null
			);
			NewsRequestDto longTitle = new NewsRequestDto(
				1L,
				"T".repeat(50),
				"Some valid content",
				2L,
				null
			);

			assertThrows(ValidationException.class, () -> newsService.update(shortTitle));
			assertThrows(ValidationException.class, () -> newsService.update(longTitle));
		}

		@Test
		void update_shouldThrowValidationException_whenContentIsNull() {
			NewsRequestDto nullContent = new NewsRequestDto(
				1L,
				"Some valid title",
				null,
				2L,
				null
			);

			assertThrows(ValidationException.class, () -> newsService.update(nullContent));
		}

		@Test
		void update_shouldThrowValidationException_whenContentViolatesLengthConstraints() {
			NewsRequestDto shortContent = new NewsRequestDto(
				1L,
				"Some valid title",
				"C",
				2L,
				null
			);
			NewsRequestDto longContent = new NewsRequestDto(
				1L,
				"Some valid title",
				"C".repeat(300),
				2L,
				null
			);

			assertThrows(ValidationException.class, () -> newsService.update(shortContent));
			assertThrows(ValidationException.class, () -> newsService.update(longContent));
		}

		@Test
		void update_shouldNotThrowValidationException_whenNewsIsValid() {
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
			when(authorRepository.existById(request.authorId())).thenReturn(true);
			when(authorRepository.readById(request.authorId()))
				.thenReturn(Optional.of(Util.createTestAuthor(request.authorId())));
			when(newsRepository.readById(id)).thenReturn(Optional.of(updated));
			when(newsMapper.dtoToModel(request)).thenReturn(Util.dtoToNews(request));
			when(newsRepository.update(any())).thenReturn(updated);
			when(newsMapper.modelToDto(updated)).thenReturn(Util.newsToDTO(updated));

			assertDoesNotThrow(() -> newsService.update(request));
		}
	}

	@Nested
	class TestDeleteById {

		@Test
		void deleteById_shouldThrowValidationException_whenIdIsNull() {
			assertThrows(ValidationException.class, () -> newsService.deleteById(null));
		}

		@Test
		void deleteById_shouldThrowValidationException_whenIdIsNegative() {
			final long id = -5L;
			assertThrows(ValidationException.class, () -> newsService.deleteById(id));
		}

		@Test
		void deleteById_shouldThrowValidationException_whenIdIsZero() {
			final long id = 0;
			assertThrows(ValidationException.class, () -> newsService.deleteById(id));
		}

		@Test
		void deleteById_shouldNotThrowValidationException_whenIdIsValid() {
			final long id = 15L;
			when(newsRepository.existById(id)).thenReturn(true);
			when(newsRepository.deleteById(id)).thenReturn(true);

			assertDoesNotThrow(() -> newsService.deleteById(id));
		}
	}
}
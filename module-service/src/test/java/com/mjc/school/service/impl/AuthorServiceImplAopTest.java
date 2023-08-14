package com.mjc.school.service.impl;

import com.mjc.school.repository.AuthorRepository;
import com.mjc.school.repository.model.Author;
import com.mjc.school.service.ServiceAopTestConfiguration;
import com.mjc.school.service.dto.AuthorRequestDto;
import com.mjc.school.service.dto.AuthorResponseDto;
import com.mjc.school.service.exception.EntityNotFoundException;
import com.mjc.school.service.exception.ValidationException;
import com.mjc.school.service.mapper.AuthorMapper;
import com.mjc.school.service.util.Util;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ServiceAopTestConfiguration.class})
public class AuthorServiceImplAopTest {

	@Autowired
	private AuthorMapper authorMapper;
	@Autowired
	private AuthorRepository authorRepository;
	@Autowired
	private AuthorServiceImpl authorService;

	@Nested
	class TestCreate {

		@Test
		void create_shouldThrowValidationException_whenRequestIsNull() {
			assertThrows(ValidationException.class, () -> authorService.create(null));
		}

		@Test
		void create_shouldThrowValidationException_whenNameIsNull() {
			AuthorRequestDto nullName = new AuthorRequestDto(null, null);

			assertThrows(ValidationException.class, () -> authorService.create(nullName));
		}

		@Test
		void create_shouldThrowValidationException_whenNameViolatesLengthConstraints() {
			AuthorRequestDto shortName = new AuthorRequestDto(null, "N");
			AuthorRequestDto longName = new AuthorRequestDto(null, "N".repeat(50));

			assertThrows(ValidationException.class, () -> authorService.create(shortName));
			assertThrows(ValidationException.class, () -> authorService.create(longName));
		}

		@Test
		void create_shouldNotThrowValidationException_whenGivenValidAuthor() {
			final long authorId = 1L;
			final String authorName = "Some valid name";
			final AuthorRequestDto request = new AuthorRequestDto(null, authorName);
			final Author mappedAuthor = new Author(null, authorName, null, null);
			when(authorMapper.dtoToModel(request)).thenReturn(mappedAuthor);
			final LocalDateTime date = LocalDateTime.now();
			final Author savedAuthor = new Author(authorId, authorName, date, date);
			when(authorRepository.create(any())).thenReturn(savedAuthor);
			final AuthorResponseDto response = Util.authorToDTO(savedAuthor);
			when(authorMapper.modelToDto(savedAuthor)).thenReturn(response);
			assertDoesNotThrow(() -> authorService.create(request));
		}
	}

	@Nested
	class TestReadById {

		@Test
		void readById_shouldThrowValidationException_whenIdIsNull() {
			assertThrows(ValidationException.class, () -> authorService.readById(null));
		}

		@Test
		void readById_shouldThrowValidationException_whenIdIsNegative() {
			final long id = -5L;
			assertThrows(ValidationException.class, () -> authorService.readById(id));
		}

		@Test
		void readById_shouldThrowValidationException_whenIdIsZero() {
			final long id = 0;
			assertThrows(ValidationException.class, () -> authorService.readById(id));
		}

		@Test
		void readById_shouldNotThrowValidationException_whenIdIsValid() {
			final long id = 2L;
			final Author toBeFound = Util.createTestAuthor(id);
			when(authorRepository.readById(id)).thenReturn(Optional.of(toBeFound));
			final AuthorResponseDto expected = Util.authorToDTO(toBeFound);
			when(authorMapper.modelToDto(toBeFound)).thenReturn(expected);

			assertDoesNotThrow(() -> authorService.readById(id));
		}
	}

	@Nested
	class TestUpdate {

		@Test
		void update_shouldThrowValidationException_whenRequestIsNull() {
			assertThrows(ValidationException.class, () -> authorService.update(null));
		}

		@Test
		void update_shouldThrowValidationException_whenIdIsNegative() {
			final long id = -5L;
			AuthorRequestDto request = Util.createTestAuthorRequest(id);

			assertThrows(ValidationException.class, () -> authorService.update(request));
		}

		@Test
		void update_shouldThrowValidationException_whenIdIsZero() {
			final long id = 0;
			AuthorRequestDto request = Util.createTestAuthorRequest(id);

			assertThrows(ValidationException.class, () -> authorService.update(request));
		}


		@Test
		void update_shouldThrowValidationException_whenNameIsNull() {
			AuthorRequestDto nullName = new AuthorRequestDto(1L, null);

			assertThrows(ValidationException.class, () -> authorService.update(nullName));
		}

		@Test
		void update_shouldThrowValidationException_whenNameViolatesLengthConstraints() {
			AuthorRequestDto shortName = new AuthorRequestDto(1L, "N");
			AuthorRequestDto longName = new AuthorRequestDto(1L, "N".repeat(50));

			assertThrows(ValidationException.class, () -> authorService.update(shortName));
			assertThrows(ValidationException.class, () -> authorService.update(longName));
		}

		@Test
		void update_shouldNotThrowValidationException_whenAuthorIsValid() {
			final long id = 1L;
			final AuthorRequestDto request = new AuthorRequestDto(id, "Updated name");
			final Author updated = new Author(
				id,
				"Updated name",
				LocalDateTime.of(2023, 7, 17, 16, 30, 0),
				LocalDateTime.now()
			);
			when(authorRepository.existById(request.id())).thenReturn(true);
			when(authorMapper.dtoToModel(request)).thenReturn(updated);
			when(authorRepository.update(any())).thenReturn(updated);
			final AuthorResponseDto response = Util.authorToDTO(updated);
			when(authorMapper.modelToDto(updated)).thenReturn(response);

			assertThrows(EntityNotFoundException.class, () -> authorService.update(request));
		}
	}

	@Nested
	class TestDeleteById {

		@Test
		void deleteById_shouldThrowValidationException_whenIdIsNull() {
			assertThrows(ValidationException.class, () -> authorService.deleteById(null));
		}

		@Test
		void deleteById_shouldThrowValidationException_whenIdIsNegative() {
			final long id = -5L;
			assertThrows(ValidationException.class, () -> authorService.deleteById(id));
		}

		@Test
		void deleteById_shouldThrowValidationException_whenIdIsZero() {
			final long id = 0;
			assertThrows(ValidationException.class, () -> authorService.deleteById(id));
		}

		@Test
		void deleteById_shouldNotThrowValidationException_whenIdIsValid() {
			final long id = 5L;
			when(authorRepository.existById(id)).thenReturn(true);
			when(authorRepository.deleteById(id)).thenReturn(true);

			assertDoesNotThrow(() -> authorService.deleteById(id));
		}
	}
}
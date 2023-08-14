package com.mjc.school.service.impl;

import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.service.ServiceAopTestConfiguration;
import com.mjc.school.service.TagService;
import com.mjc.school.service.dto.TagRequestDto;
import com.mjc.school.service.dto.TagResponseDto;
import com.mjc.school.service.exception.ValidationException;
import com.mjc.school.service.mapper.TagMapper;
import com.mjc.school.service.util.Util;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ServiceAopTestConfiguration.class})
public class TagServiceImplAopTest {

	@Autowired
	private TagMapper tagMapper;
	@Autowired
	private TagRepository tagRepository;
	@Autowired
	private TagService tagService;

	@Nested
	class TestCreate {

		@Test
		void create_shouldThrowValidationException_whenRequestIsNull() {
			assertThrows(ValidationException.class, () -> tagService.create(null));
		}

		@Test
		void create_shouldThrowValidationException_whenNameIsNull() {
			TagRequestDto nullName = new TagRequestDto(null, null);

			assertThrows(ValidationException.class, () -> tagService.create(nullName));
		}

		@Test
		void create_shouldThrowValidationException_whenNameViolatesLengthConstraints() {
			TagRequestDto shortName = new TagRequestDto(null, "N");
			TagRequestDto longName = new TagRequestDto(null, "N".repeat(50));

			assertThrows(ValidationException.class, () -> tagService.create(shortName));
			assertThrows(ValidationException.class, () -> tagService.create(longName));
		}

		@Test
		void create_shouldNotThrowValidationException_whenGivenValidTag() {
			final long tagId = 1L;
			final String tagName = "Some valid name";
			final TagRequestDto request = new TagRequestDto(null, tagName);
			final Tag mappedTag = new Tag(null, tagName);
			when(tagMapper.dtoToModel(request)).thenReturn(mappedTag);
			final Tag savedTag = new Tag(tagId, tagName);
			when(tagRepository.create(any())).thenReturn(savedTag);
			final TagResponseDto response = Util.tagToDTO(savedTag);
			when(tagMapper.modelToDto(savedTag)).thenReturn(response);
			assertDoesNotThrow(() -> tagService.create(request));
		}
	}

	@Nested
	class TestReadById {

		@Test
		void readById_shouldThrowValidationException_whenIdIsNull() {
			assertThrows(ValidationException.class, () -> tagService.readById(null));
		}

		@Test
		void readById_shouldThrowValidationException_whenIdIsNegative() {
			final long id = -5L;
			assertThrows(ValidationException.class, () -> tagService.readById(id));
		}

		@Test
		void readById_shouldThrowValidationException_whenIdIsZero() {
			final long id = 0;
			assertThrows(ValidationException.class, () -> tagService.readById(id));
		}

		@Test
		void readById_shouldNotThrowValidationException_whenIdIsValid() {
			final long id = 2L;
			final Tag toBeFound = Util.createTestTag(id);
			when(tagRepository.readById(id)).thenReturn(Optional.of(toBeFound));
			final TagResponseDto expected = Util.tagToDTO(toBeFound);
			when(tagMapper.modelToDto(toBeFound)).thenReturn(expected);

			assertDoesNotThrow(() -> tagService.readById(id));
		}
	}

	@Nested
	class TestUpdate {

		@Test
		void update_shouldThrowValidationException_whenRequestIsNull() {
			assertThrows(ValidationException.class, () -> tagService.update(null));
		}

		@Test
		void update_shouldThrowValidationException_whenIdIsNegative() {
			final long id = -5L;
			TagRequestDto request = Util.createTestTagRequest(id);

			assertThrows(ValidationException.class, () -> tagService.update(request));
		}

		@Test
		void update_shouldThrowValidationException_whenIdIsZero() {
			final long id = 0;
			TagRequestDto request = Util.createTestTagRequest(id);

			assertThrows(ValidationException.class, () -> tagService.update(request));
		}


		@Test
		void update_shouldThrowValidationException_whenNameIsNull() {
			TagRequestDto nullName = new TagRequestDto(1L, null);

			assertThrows(ValidationException.class, () -> tagService.update(nullName));
		}

		@Test
		void update_shouldThrowValidationException_whenNameViolatesLengthConstraints() {
			TagRequestDto shortName = new TagRequestDto(1L, "N");
			TagRequestDto longName = new TagRequestDto(1L, "N".repeat(50));

			assertThrows(ValidationException.class, () -> tagService.update(shortName));
			assertThrows(ValidationException.class, () -> tagService.update(longName));
		}

		@Test
		void update_shouldNotThrowValidationException_whenTagIsValid() {
			final long id = 1L;
			final TagRequestDto request = new TagRequestDto(id, "Updated name");
			final Tag updated = new Tag(id, "Updated name");
			when(tagRepository.existById(request.id())).thenReturn(true);
			when(tagMapper.dtoToModel(request)).thenReturn(updated);
			when(tagRepository.update(any())).thenReturn(updated);
			final TagResponseDto response = Util.tagToDTO(updated);
			when(tagMapper.modelToDto(updated)).thenReturn(response);

			assertDoesNotThrow(() -> tagService.update(request));
		}
	}

	@Nested
	class TestDeleteById {

		@Test
		void deleteById_shouldThrowValidationException_whenIdIsNull() {
			assertThrows(ValidationException.class, () -> tagService.deleteById(null));
		}

		@Test
		void deleteById_shouldThrowValidationException_whenIdIsNegative() {
			final long id = -5L;
			assertThrows(ValidationException.class, () -> tagService.deleteById(id));
		}

		@Test
		void deleteById_shouldThrowValidationException_whenIdIsZero() {
			final long id = 0;
			assertThrows(ValidationException.class, () -> tagService.deleteById(id));
		}

		@Test
		void deleteById_shouldNotThrowValidationException_whenIdIsValid() {
			final long id = 5L;
			when(tagRepository.existById(id)).thenReturn(true);
			when(tagRepository.deleteById(id)).thenReturn(true);

			assertDoesNotThrow(() -> tagService.deleteById(id));
		}
	}
}
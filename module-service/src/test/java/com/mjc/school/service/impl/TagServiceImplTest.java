package com.mjc.school.service.impl;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.exception.EntityConstraintViolationRepositoryException;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.repository.query.NewsSearchQueryParams;
import com.mjc.school.service.dto.TagRequestDto;
import com.mjc.school.service.dto.TagResponseDto;
import com.mjc.school.service.exception.EntityConstraintViolationServiceException;
import com.mjc.school.service.exception.EntityNotFoundException;
import com.mjc.school.service.mapper.TagMapper;
import com.mjc.school.service.util.Util;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
class TagServiceImplTest {

	@Mock
	private NewsRepository newsRepository;
	@Mock
	private TagRepository tagRepository;
	@Mock
	private TagMapper tagMapper;
	@InjectMocks
	private TagServiceImpl tagService;

	@Nested
	class TestCreate {

		@Test
		void create_shouldThrowEntityConstraintViolationServiceException_whenNameAlreadyExists() {
			final String tagName = "Conflicting name";
			final TagRequestDto request = new TagRequestDto(null, tagName);
			when(tagRepository.create(any())).thenThrow(
				new EntityConstraintViolationRepositoryException("Constraint violation"));

			assertThrows(EntityConstraintViolationServiceException.class,
				() -> tagService.create(request));

		}

		@Test
		void create_shouldReturnSavedEntity_whenValidRequestDtoProvided() {
			final long tagId = 1L;
			final String tagName = "Some valid name";
			final TagRequestDto request = new TagRequestDto(null, tagName);
			final Tag mappedTag = Util.dtoToTag(request);
			when(tagMapper.dtoToModel(request)).thenReturn(mappedTag);
			final Tag savedTag = new Tag(tagId, tagName);
			when(tagRepository.create(any())).thenReturn(savedTag);
			final TagResponseDto response = Util.tagToDTO(savedTag);
			when(tagMapper.modelToDto(savedTag)).thenReturn(response);

			final TagResponseDto result = tagService.create(request);

			verify(tagMapper, times(1)).dtoToModel(request);
			verify(tagRepository, times(1)).create(mappedTag);
			verify(tagMapper, times(1)).modelToDto(savedTag);
			assertEquals(response, result);
		}
	}

	@Nested
	class TestReadById {

		@Test
		void readById_shouldThrowEntityNotFoundException_whenThereIsNoEntityWithGivenId() {
			final long id = 99L;
			when(tagRepository.readById(id)).thenReturn(Optional.empty());

			assertThrows(EntityNotFoundException.class, () -> tagService.readById(id));
			verify(tagRepository, times(1)).readById(any());
			verifyNoInteractions(tagMapper);
		}

		@Test
		void readById_shouldReturnDTO_whenEntityWithGivenIdIsFound() {
			final long id = 2L;
			final Tag toBeFound = Util.createTestTag(id);
			when(tagRepository.readById(id)).thenReturn(Optional.of(toBeFound));
			final TagResponseDto expected = Util.tagToDTO(toBeFound);
			when(tagMapper.modelToDto(toBeFound)).thenReturn(expected);

			assertEquals(expected, tagService.readById(id));
			verify(tagRepository, times(1)).readById(id);
			verify(tagMapper, times(1)).modelToDto(toBeFound);
		}
	}

	@Nested
	class TesReadAll {

		@Test
		void readAll_shouldReturnEmptyDTOList_whenRepositoryReturnsEmptyList() {
			when(tagRepository.readAll()).thenReturn(new ArrayList<>());

			final List<TagResponseDto> expected = new ArrayList<>();

			assertEquals(expected, tagService.readAll());
			verify(tagRepository, times(1)).readAll();
			verify(tagMapper, times(1)).modelListToDtoList(new ArrayList<>());
		}

		@Test
		void readAll_shouldReturnTwoDTO_whenRepositoryReturnsTwoEntities() {
			final List<Tag> allTags = Arrays.asList(
				Util.createTestTag(1L),
				Util.createTestTag(2L)
			);
			when(tagRepository.readAll()).thenReturn(allTags);
			final List<TagResponseDto> expected = Util.tagListToTagDTOList(allTags);
			when(tagMapper.modelListToDtoList(allTags)).thenReturn(expected);

			assertEquals(expected, tagService.readAll());
			verify(tagRepository, times(1)).readAll();
			verify(tagMapper, times(1)).modelListToDtoList(allTags);
		}
	}

	@Nested
	class TestUpdate {

		@Test
		void update_shouldThrowValidationException_whenIdIsNull() {
			TagRequestDto request = Util.createTestTagRequest(null);

			assertThrows(EntityNotFoundException.class, () -> tagService.update(request));
		}

		@Test
		void update_shouldThrowEntityNotFoundException_whenEntityWithGivenIdNotFound() {
			final long id = 99L;
			final TagRequestDto request = Util.createTestTagRequest(id);
			when(tagRepository.existById(request.id())).thenReturn(false);

			assertThrows(EntityNotFoundException.class, () -> tagService.update(request));
			verify(tagRepository, times(1)).existById(request.id());
			verify(tagRepository, times(0)).update(any());
		}

		@Test
		void update_shouldThrowEntityConstraintViolationServiceException_whenNameAlreadyExists() {
			final long id = 1L;
			final TagRequestDto request = new TagRequestDto(id, "Conflicting name");
			when(tagRepository.existById(request.id())).thenReturn(true);
			when(tagRepository.update(any())).thenThrow(
				new EntityConstraintViolationRepositoryException("Constraint violation"));

			assertThrows(EntityConstraintViolationServiceException.class,
				() -> tagService.update(request));
		}

		@Test
		void update_shouldReturnUpdatedEntity_whenValidRequestDtoProvided() {
			final long id = 1L;
			final TagRequestDto request = new TagRequestDto(id, "Updated name");
			final Tag updated = new Tag(id, "Updated name");
			when(tagRepository.existById(request.id())).thenReturn(true);
			when(tagMapper.dtoToModel(request)).thenReturn(updated);
			when(tagRepository.update(any())).thenReturn(updated);
			final TagResponseDto response = Util.tagToDTO(updated);
			when(tagMapper.modelToDto(updated)).thenReturn(response);

			final TagResponseDto result = tagService.update(request);

			verify(tagRepository, times(1)).existById(request.id());
			verify(tagRepository, times(1)).update(any());
			assertEquals(response, result);
		}
	}

	@Nested
	class TestDeleteById {

		@Test
		void deleteById_shouldThrowEntityNotFoundException_whenThereIsNoEntityWithGivenId() {
			final long id = 5L;
			when(tagRepository.existById(id)).thenReturn(false);

			assertThrows(EntityNotFoundException.class, () -> tagService.deleteById(id));
			verify(tagRepository, times(1)).existById(id);
			verify(tagRepository, times(0)).deleteById(id);
		}

		@Test
		void deleteById_shouldReturnTrue_whenRepositoryDeletesEntityById() {
			final long id = 15L;
			when(tagRepository.existById(id)).thenReturn(true);
			final NewsSearchQueryParams params =
				new NewsSearchQueryParams(null, List.of(id), null, null, null);
			when(newsRepository.readByParams(params)).thenReturn(Collections.emptyList());
			when(tagRepository.deleteById(id)).thenReturn(true);

			assertTrue(tagService.deleteById(id));
			verify(tagRepository, times(1)).existById(id);
			verify(newsRepository, times(1)).readByParams(params);
			verify(tagRepository, times(1)).deleteById(id);
		}

		@Test
		void deleteById_shouldReturnFalse_whenRepositoryDoesNotDeleteEntityById() {
			final long id = 99L;
			when(tagRepository.existById(id)).thenReturn(true);
			final NewsSearchQueryParams params =
				new NewsSearchQueryParams(null, List.of(id), null, null, null);
			when(newsRepository.readByParams(params)).thenReturn(Collections.emptyList());
			when(tagRepository.deleteById(id)).thenReturn(false);

			assertFalse(tagService.deleteById(id));
			verify(tagRepository, times(1)).existById(id);
			verify(newsRepository, times(1)).readByParams(params);
			verify(tagRepository, times(1)).deleteById(id);
		}
	}
}
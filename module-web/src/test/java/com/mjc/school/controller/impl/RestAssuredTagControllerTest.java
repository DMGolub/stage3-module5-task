package com.mjc.school.controller.impl;

import com.mjc.school.controller.ControllerTestConfig;
import com.mjc.school.service.TagService;
import com.mjc.school.service.dto.TagRequestDto;
import com.mjc.school.service.dto.TagResponseDto;
import com.mjc.school.service.exception.EntityConstraintViolationServiceException;
import com.mjc.school.service.exception.EntityNotFoundException;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;

import static com.mjc.school.controller.constants.Constants.NEWS_ROOT_PATH;
import static com.mjc.school.controller.constants.Constants.TAG_ROOT_PATH;
import static com.mjc.school.service.constants.Constants.NEWS_ENTITY_NAME;
import static com.mjc.school.service.constants.Constants.TAG_ENTITY_NAME;
import static com.mjc.school.service.exception.ServiceErrorCode.ENTITY_NOT_FOUND_BY_ID;
import static com.mjc.school.service.exception.ServiceErrorCode.TAG_CONSTRAINT_VIOLATION;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {ControllerTestConfig.class})
class RestAssuredTagControllerTest {

	private static final String BASE_URI = "http://localhost";
	private static final String REQUEST_MAPPING_URI = "/api/v1";
	private static final String CONTENT_TYPE = "application/json";

	@Autowired
	private TagService tagService;
	@LocalServerPort
	private int port;
	private List<TagResponseDto> tags;

	@BeforeEach
	public void setUp() {
		reset(tagService);

		RestAssured.baseURI = BASE_URI;
		RestAssured.port = port;
		RestAssured.basePath = REQUEST_MAPPING_URI;

		tags = Arrays.asList(
			new TagResponseDto(1L, "Name One"),
			new TagResponseDto(2L, "Name Two")
		);
	}

	@Test
	void readAll_shouldReturn200_whenRequestIsCorrect() {
		when(tagService.readAll(10, 0, "id::asc")).thenReturn(tags);
		final int EXPECTED_STATUS_CODE = 200;

		RestAssured.given()
			.get(TAG_ROOT_PATH)
			.then().assertThat().statusCode(EXPECTED_STATUS_CODE)
			.body(containsString(tags.get(0).name()))
			.body(containsString(tags.get(1).name()));
		verify(tagService, times(1)).readAll(10, 0, "id::asc");
	}

	@Nested
	class TestReadById {

		@Test
		void readById_shouldReturn404_whenEntityNotFoundById() {
			final long tagId = 99L;
			when(tagService.readById(tagId)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), TAG_ENTITY_NAME, tagId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.get(TAG_ROOT_PATH + "/" + tagId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE);
			verify(tagService, times(1)).readById(tagId);
		}

		@Test
		void readById_shouldReturn200_whenRequestIsCorrectAndEntityExists() {
			final int tagId = 2;
			when(tagService.readById((long) tagId)).thenReturn(tags.get(tagId - 1));
			final int EXPECTED_STATUS_CODE = 200;

			RestAssured.given()
				.get(TAG_ROOT_PATH + "/" + tagId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE)
				.body("id", equalTo(tagId))
				.body("name", equalTo(tags.get(tagId - 1).name()));
			verify(tagService, times(1)).readById((long) tagId);
		}
	}

	@Nested
	class TestReadByNewsId {

		@Test
		void readByNewsId_shouldReturn404_whenNewsNotFoundById() {
			final long newsId = 99L;
			when(tagService.readTagsByNewsId(newsId)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), NEWS_ENTITY_NAME, newsId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.get(NEWS_ROOT_PATH + "/" + newsId + "/tags")
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE);
			verify(tagService, times(1)).readTagsByNewsId(newsId);
		}

		@Test
		void readByNewsId_shouldReturn200_whenRequestIsCorrectAndNewsExists() {
			final long newsId = 1L;
			when(tagService.readTagsByNewsId(newsId)).thenReturn(tags);
			final int EXPECTED_STATUS_CODE = 200;

			RestAssured.given()
				.get(NEWS_ROOT_PATH + "/" + newsId + "/tags")
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE)
				.body(containsString(tags.get(0).name()))
				.body(containsString(tags.get(1).name()));
			verify(tagService, times(1)).readTagsByNewsId(newsId);
		}
	}

	@Nested
	class TestCreate {

		@Test
		void create_shouldReturn400_whenNameIsEmpty() {
			final String name = "";
			final TagRequestDto request = new TagRequestDto(null, name);
			final int EXPECTED_STATUS_CODE = 400;	// HttpStatus.BAD_REQUEST

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(TAG_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(tagService);
		}

		@Test
		void create_shouldReturn409_whenNameAlreadyExists() {
			final String name = tags.get(0).name();
			final TagRequestDto request = new TagRequestDto(null, name);
			when(tagService.create(request)).thenThrow(new EntityConstraintViolationServiceException(
				TAG_CONSTRAINT_VIOLATION.getMessage(),
				TAG_CONSTRAINT_VIOLATION.getCode()
			));
			final int EXPECTED_STATUS_CODE = 409;	// HttpStatus.CONFLICT

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(TAG_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verify(tagService, times(1)).create(request);
		}

		@Test
		void create_shouldReturn201_whenRequestIsCorrect() {
			final String name = "New Name";
			final TagRequestDto request = new TagRequestDto(null, name);
			final int initialSize = tags.size();
			final TagResponseDto created = new TagResponseDto((long) (initialSize + 1), name);
			when(tagService.create(request)).thenReturn(created);
			final int EXPECTED_STATUS_CODE = 201;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(TAG_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE)
				.body("id", equalTo(initialSize + 1))
				.body("name", equalTo(name));
			verify(tagService, times(1)).create(request);
		}
	}

	@Nested
	class TestUpdate {

		@Test
		void update_shouldReturn409_whenPathIdAndRequestIdDoNotMatch() {
			final long pathId = 1L;
			final long tagId = 2L;
			final String updatedName = "Updated Name";
			final TagRequestDto request = new TagRequestDto(tagId, updatedName);
			final int EXPECTED_STATUS_CODE = 409;	// HttpStatus.CONFLICT

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(TAG_ROOT_PATH + "/" + pathId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(tagService);
		}

		@Test
		void update_shouldReturn404_whenEntityNotFoundById() {
			final long tagId = 2L;
			final String updatedName = "Updated Name";
			final TagRequestDto request = new TagRequestDto(tagId, updatedName);
			when(tagService.update(request)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), TAG_ENTITY_NAME, tagId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(TAG_ROOT_PATH + "/" + tagId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verify(tagService, times(1)).update(request);
		}

		@Test
		void update_shouldReturn400_whenNameIsEmpty() {
			final long tagId = 2L;
			final String updatedName = "";
			final TagRequestDto request = new TagRequestDto(tagId, updatedName);
			final int EXPECTED_STATUS_CODE = 400;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(TAG_ROOT_PATH + "/" + tagId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(tagService);
		}

		@Test
		void update_shouldReturn409_whenNameAlreadyExists() {
			final long tagId = 2L;
			final String updatedName = tags.get(0).name();
			final TagRequestDto request = new TagRequestDto(tagId, updatedName);
			when(tagService.update(request)).thenThrow(new EntityConstraintViolationServiceException(
				TAG_CONSTRAINT_VIOLATION.getMessage(),
				TAG_CONSTRAINT_VIOLATION.getCode()
			));
			final int EXPECTED_STATUS_CODE = 409;		// HttpStatus.CONFLICT

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(TAG_ROOT_PATH + "/" + tagId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verify(tagService, times(1)).update(request);
		}

		@Test
		void update_shouldReturn200_whenRequestIsCorrect() {
			final long tagId = 2L;
			final String updatedName = "Updated Name";
			final TagRequestDto request = new TagRequestDto(tagId, updatedName);
			final TagResponseDto updated = new TagResponseDto(tagId, updatedName);
			when(tagService.update(request)).thenReturn(updated);
			final int EXPECTED_STATUS_CODE = 200;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(TAG_ROOT_PATH + "/" + tagId)
				.then().statusCode(EXPECTED_STATUS_CODE)
				.body("id", equalTo((int) tagId))
				.body("name", equalTo(updatedName));
			verify(tagService, times(1)).update(request);
		}
	}

	@Nested
	class TestDelete {

		@Test
		void deleteById_shouldReturn404_whenEntityNotFoundById() {
			final long tagId = 99L;
			when(tagService.deleteById(tagId)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), TAG_ENTITY_NAME, tagId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.delete(TAG_ROOT_PATH + "/" + tagId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE);
			verify(tagService, times(1)).deleteById(tagId);
		}

		@Test
		void deleteById_shouldReturn204_whenRequestIsCorrect() {
			final long tagId = 1L;
			when(tagService.deleteById(tagId)).thenReturn(true);
			final int EXPECTED_STATUS_CODE = 204;

			RestAssured.given()
				.delete(TAG_ROOT_PATH + "/" + tagId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE);
			verify(tagService, times(1)).deleteById(tagId);
		}
	}
}
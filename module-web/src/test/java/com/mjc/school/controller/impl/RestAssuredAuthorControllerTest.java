package com.mjc.school.controller.impl;

import com.mjc.school.controller.ControllerTestConfig;
import com.mjc.school.service.AuthorService;
import com.mjc.school.service.dto.AuthorRequestDto;
import com.mjc.school.service.dto.AuthorResponseDto;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.mjc.school.controller.constants.Constants.AUTHOR_ROOT_PATH;
import static com.mjc.school.controller.constants.Constants.NEWS_ROOT_PATH;
import static com.mjc.school.service.constants.Constants.AUTHOR_ENTITY_NAME;
import static com.mjc.school.service.constants.Constants.NEWS_ENTITY_NAME;
import static com.mjc.school.service.exception.ServiceErrorCode.AUTHOR_CONSTRAINT_VIOLATION;
import static com.mjc.school.service.exception.ServiceErrorCode.ENTITY_NOT_FOUND_BY_ID;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {ControllerTestConfig.class})
class RestAssuredAuthorControllerTest {

	private static final String BASE_URI = "http://localhost";
	private static final String REQUEST_MAPPING_URI = "/api/v1";
	private static final String CONTENT_TYPE = "application/json";

	@Autowired
	private AuthorService authorService;
	@LocalServerPort
	private int port;
	private List<AuthorResponseDto> authors;

	@BeforeEach
	public void setUp() {
		reset(authorService);

		RestAssured.baseURI = BASE_URI;
		RestAssured.port = port;
		RestAssured.basePath = REQUEST_MAPPING_URI;

		final LocalDateTime date = LocalDateTime.now();
		authors = Arrays.asList(
			new AuthorResponseDto(1L, "Name One", date, date),
			new AuthorResponseDto(2L, "Name Two", date, date)
		);
	}

	@Test
	void readAll_shouldReturn200_whenRequestIsCorrect() {
		when(authorService.readAll(10, 0, "id::asc")).thenReturn(authors);
		final int EXPECTED_STATUS_CODE = 200;

		RestAssured.given()
			.get(AUTHOR_ROOT_PATH)
			.then().assertThat().statusCode(EXPECTED_STATUS_CODE)
			.body(containsString(authors.get(0).name()))
			.body(containsString(authors.get(1).name()));
		verify(authorService, times(1)).readAll(10, 0, "id::asc");
	}

	@Nested
	class TestReadById {

		@Test
		void readById_shouldReturn404_whenEntityNotFoundById() {
			final long authorId = 99L;
			when(authorService.readById(authorId)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), AUTHOR_ENTITY_NAME, authorId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.get(AUTHOR_ROOT_PATH + "/" + authorId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE);
			verify(authorService, times(1)).readById(authorId);
		}

		@Test
		void readById_shouldReturn200_whenRequestIsCorrectAndEntityExists() {
			final int authorId = 2;
			when(authorService.readById((long) authorId)).thenReturn(authors.get(authorId - 1));
			final int EXPECTED_STATUS_CODE = 200;

			RestAssured.given()
				.get(AUTHOR_ROOT_PATH + "/" + authorId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE)
				.body("id", equalTo(authorId))
				.body("name", equalTo(authors.get(authorId - 1).name()));
			verify(authorService, times(1)).readById((long) authorId);
		}
	}

	@Nested
	class TestReadByNewsId {

		@Test
		void readByNewsId_shouldReturn404_whenNewsNotFoundById() {
			final long newsId = 99L;
			when(authorService.readAuthorByNewsId(newsId)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), NEWS_ENTITY_NAME, newsId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.get(NEWS_ROOT_PATH + "/" + newsId + "/author")
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE);
			verify(authorService, times(1)).readAuthorByNewsId(newsId);
		}

		@Test
		void readByNewsId_shouldReturn200_whenRequestIsCorrectAndNewsExists() {
			final long newsId = 1L;
			final int authorId = 1;
			when(authorService.readAuthorByNewsId(newsId)).thenReturn(authors.get(authorId - 1));
			final int EXPECTED_STATUS_CODE = 200;

			RestAssured.given()
				.get(NEWS_ROOT_PATH + "/" + newsId + "/author")
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE)
				.body("id", equalTo(authorId))
				.body("name", equalTo(authors.get(authorId - 1).name()));
			verify(authorService, times(1)).readAuthorByNewsId(newsId);
		}
	}

	@Nested
	class TestCreate {

		@Test
		void create_shouldReturn400_whenNameIsEmpty() {
			final String name = "";
			final AuthorRequestDto request = new AuthorRequestDto(null, name);
			final int EXPECTED_STATUS_CODE = 400;	// HttpStatus.BAD_REQUEST

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(AUTHOR_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(authorService);
		}

		@Test
		void create_shouldReturn409_whenNameAlreadyExists() {
			final String name = authors.get(0).name();
			final AuthorRequestDto request = new AuthorRequestDto(null, name);
			when(authorService.create(request)).thenThrow(new EntityConstraintViolationServiceException(
				AUTHOR_CONSTRAINT_VIOLATION.getMessage(),
				AUTHOR_CONSTRAINT_VIOLATION.getCode()
			));
			final int EXPECTED_STATUS_CODE = 409;	// HttpStatus.CONFLICT

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(AUTHOR_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verify(authorService, times(1)).create(request);
		}

		@Test
		void create_shouldReturn201_whenRequestIsCorrect() {
			final String name = "New Name";
			final AuthorRequestDto request = new AuthorRequestDto(null, name);
			final LocalDateTime date = LocalDateTime.now();
			final int initialSize = authors.size();
			final AuthorResponseDto created = new AuthorResponseDto((long) (initialSize + 1), name, date, date);
			when(authorService.create(request)).thenReturn(created);
			final int EXPECTED_STATUS_CODE = 201;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(AUTHOR_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE)
				.body("id", equalTo(initialSize + 1))
				.body("name", equalTo(name));
			verify(authorService, times(1)).create(request);
		}
	}

	@Nested
	class TestUpdate {

		@Test
		void update_shouldReturn409_whenPathIdAndRequestIdDoNotMatch() {
			final long pathId = 1L;
			final long authorId = 2L;
			final String updatedName = "Updated Name";
			final AuthorRequestDto request = new AuthorRequestDto(authorId, updatedName);
			final int EXPECTED_STATUS_CODE = 409;	// HttpStatus.CONFLICT

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(AUTHOR_ROOT_PATH + "/" + pathId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(authorService);
		}

		@Test
		void update_shouldReturn404_whenEntityNotFoundById() {
			final long authorId = 99L;
			final String updatedName = "Updated Name";
			final AuthorRequestDto request = new AuthorRequestDto(authorId, updatedName);
			when(authorService.update(request)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), AUTHOR_ENTITY_NAME, authorId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(AUTHOR_ROOT_PATH + "/" + authorId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verify(authorService, times(1)).update(request);
		}

		@Test
		void update_shouldReturn400_whenNameIsEmpty() {
			final long authorId = 2L;
			final String updatedName = "";
			final AuthorRequestDto request = new AuthorRequestDto(authorId, updatedName);
			final int EXPECTED_STATUS_CODE = 400;	// HttpStatus.BAD_REQUEST

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(AUTHOR_ROOT_PATH + "/" + authorId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(authorService);
		}

		@Test
		void update_shouldReturn409_whenNameAlreadyExists() {
			final long authorId = 2L;
			final String updatedName = authors.get(0).name();
			final AuthorRequestDto request = new AuthorRequestDto(authorId, updatedName);
			when(authorService.update(request)).thenThrow(new EntityConstraintViolationServiceException(
				AUTHOR_CONSTRAINT_VIOLATION.getMessage(),
				AUTHOR_CONSTRAINT_VIOLATION.getCode()
			));
			final int EXPECTED_STATUS_CODE = 409;	// HttpStatus.CONFLICT

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(AUTHOR_ROOT_PATH + "/" + authorId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verify(authorService, times(1)).update(request);
		}

		@Test
		void update_shouldReturn200_whenRequestIsCorrect() {
			final long authorId = 2L;
			final String updatedName = "Updated Name";
			final AuthorRequestDto request = new AuthorRequestDto(authorId, updatedName);
			final LocalDateTime date = LocalDateTime.now();
			final AuthorResponseDto updated = new AuthorResponseDto(authorId, updatedName, date, date);
			when(authorService.update(request)).thenReturn(updated);
			final int EXPECTED_STATUS_CODE = 200;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(AUTHOR_ROOT_PATH + "/" + authorId)
				.then().statusCode(EXPECTED_STATUS_CODE)
				.body("id", equalTo((int) authorId))
				.body("name", equalTo(updatedName));;
			verify(authorService, times(1)).update(request);
		}
	}

	@Nested
	class TestDelete {

		@Test
		void deleteById_shouldReturn404_whenEntityNotFoundById() {
			final long authorId = 99L;
			when(authorService.deleteById(authorId)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), AUTHOR_ENTITY_NAME, authorId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.delete(AUTHOR_ROOT_PATH + "/" + authorId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE);
			verify(authorService, times(1)).deleteById(authorId);
		}

		@Test
		void deleteById_shouldReturn204_whenRequestIsCorrectAndEntityNotDeleted() {
			final long authorId = 1L;
			when(authorService.deleteById(authorId)).thenReturn(false);
			final int EXPECTED_STATUS_CODE = 204;

			RestAssured.given()
				.delete(AUTHOR_ROOT_PATH + "/" + authorId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE);
			verify(authorService, times(1)).deleteById(authorId);
		}

		@Test
		void deleteById_shouldReturn204_whenRequestIsCorrectAndEntityDeleted() {
			final long authorId = 1L;
			when(authorService.deleteById(authorId)).thenReturn(true);
			final int EXPECTED_STATUS_CODE = 204;

			RestAssured.given()
				.delete(AUTHOR_ROOT_PATH + "/" + authorId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE);
			verify(authorService, times(1)).deleteById(authorId);
		}
	}
}
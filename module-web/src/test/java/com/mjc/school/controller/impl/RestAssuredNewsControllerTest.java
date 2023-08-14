package com.mjc.school.controller.impl;

import com.mjc.school.controller.ControllerTestConfig;
import com.mjc.school.service.NewsService;
import com.mjc.school.service.dto.NewsRequestDto;
import com.mjc.school.service.dto.NewsResponseDto;
import com.mjc.school.service.exception.EntityConstraintViolationServiceException;
import com.mjc.school.service.exception.EntityNotFoundException;
import com.mjc.school.service.query.NewsQueryParams;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mjc.school.controller.constants.Constants.NEWS_ROOT_PATH;
import static com.mjc.school.service.constants.Constants.AUTHOR_ENTITY_NAME;
import static com.mjc.school.service.constants.Constants.NEWS_ENTITY_NAME;
import static com.mjc.school.service.exception.ServiceErrorCode.ENTITY_NOT_FOUND_BY_ID;
import static com.mjc.school.service.exception.ServiceErrorCode.NEWS_CONSTRAINT_VIOLATION;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {ControllerTestConfig.class})
class RestAssuredNewsControllerTest {

	private static final String BASE_URI = "http://localhost";
	private static final String REQUEST_MAPPING_URI = "/api/v1";
	private static final String CONTENT_TYPE = "application/json";

	@Autowired
	private NewsService newsService;
	@LocalServerPort
	private int port;
	private List<NewsResponseDto> news;

	@BeforeEach
	public void setUp() {
		reset(newsService);

		RestAssured.baseURI = BASE_URI;
		RestAssured.port = port;
		RestAssured.basePath = REQUEST_MAPPING_URI;

		final LocalDateTime date = LocalDateTime.now();
		final long authorId = 1L;
		final NewsResponseDto news1 = new NewsResponseDto(
			1L,
			"Title One",
			"Content One",
			date,
			date,
			authorId,
			new ArrayList<>(),
			new ArrayList<>()
		);
		final NewsResponseDto news2 = new NewsResponseDto(
			2L,
			"Title Two",
			"Content Two",
			date,
			date,
			authorId,
			new ArrayList<>(),
			new ArrayList<>()
		);
		news = Arrays.asList(news1, news2);
	}

	@Test
	void readAll_shouldReturn200_whenRequestIsCorrect() {
		when(newsService.readAll(10, 0, "id::asc")).thenReturn(news);
		final int EXPECTED_STATUS_CODE = 200;

		RestAssured.given()
			.get(NEWS_ROOT_PATH)
			.then().assertThat().statusCode(EXPECTED_STATUS_CODE)
			.body(containsString(news.get(0).title()))
			.body(containsString(news.get(0).content()))
			.body(containsString(news.get(1).title()))
			.body(containsString(news.get(1).content()));
		verify(newsService, times(1)).readAll(10, 0, "id::asc");
	}

	@Nested
	class TestReadById {

		@Test
		void readById_shouldReturn404_whenEntityNotFoundById() {
			final long newsId = 99L;
			when(newsService.readById(newsId)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), NEWS_ENTITY_NAME, newsId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.get(NEWS_ROOT_PATH + "/" + newsId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE);
			verify(newsService, times(1)).readById(newsId);
		}

		@Test
		void readById_shouldReturn200_whenRequestIsCorrectAndEntityExists() {
			final int newsId = 2;
			when(newsService.readById((long) newsId)).thenReturn(news.get(newsId - 1));
			final int EXPECTED_STATUS_CODE = 200;

			RestAssured.given()
				.get(NEWS_ROOT_PATH + "/" + newsId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE)
				.body("id", equalTo(newsId))
				.body("title", equalTo(news.get(newsId - 1).title()))
				.body("content", equalTo(news.get(newsId - 1).content()));
			verify(newsService, times(1)).readById((long) newsId);
		}
	}

	@Test
	void readNewsByParams_shouldReturn200_whenRequestIsCorrect() {
		final String authorName = "Author Name";
		final NewsQueryParams params =
			new NewsQueryParams(null, null, authorName, null, null);
		when(newsService.readNewsByParams(params)).thenReturn(news);
		final int EXPECTED_STATUS_CODE = 200;

		RestAssured.given()
			.get(NEWS_ROOT_PATH + "/search?author_name=Author Name")
			.then().assertThat().statusCode(EXPECTED_STATUS_CODE)
			.body(containsString(news.get(0).title()))
			.body(containsString(news.get(0).content()))
			.body(containsString(news.get(1).title()))
			.body(containsString(news.get(1).content()));
		verify(newsService, times(1)).readNewsByParams(params);
	}

	@Nested
	class TestCreate {

		@Test
		void create_shouldReturn400_whenTitleIsEmpty() {
			final String title = "";
			final String content = "New Content";
			final long authorId = 1L;
			final NewsRequestDto request = new NewsRequestDto(null, title, content, authorId, null);
			final int EXPECTED_STATUS_CODE = 400;	// HttpStatus.BAD_REQUEST

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(NEWS_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(newsService);
		}

		@Test
		void create_shouldReturn400_whenContentIsEmpty() {
			final String title = "New title";
			final String content = "";
			final long authorId = 1L;
			final NewsRequestDto request = new NewsRequestDto(null, title, content, authorId, null);
			final int EXPECTED_STATUS_CODE = 400;	// HttpStatus.BAD_REQUEST

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(NEWS_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(newsService);
		}

		@Test
		void create_shouldReturn400_whenAuthorIdIsZero() {
			final String title = "New title";
			final String content = "New content";
			final long authorId = 0;
			final NewsRequestDto request = new NewsRequestDto(null, title, content, authorId, null);
			final int EXPECTED_STATUS_CODE = 400;	// HttpStatus.BAD_REQUEST

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(NEWS_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(newsService);
		}

		@Test
		void create_shouldReturn400_whenAuthorIdIsNegative() {
			final String title = "New title";
			final String content = "New content";
			final long authorId = -1L;
			final NewsRequestDto request = new NewsRequestDto(null, title, content, authorId, null);
			final int EXPECTED_STATUS_CODE = 400;	// HttpStatus.BAD_REQUEST

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(NEWS_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(newsService);
		}

		@Test
		void create_shouldReturn404_whenAuthorNotFoundById() {
			final String title = "New Name";
			final String content = "New Content";
			final long authorId = 99L;
			final NewsRequestDto request = new NewsRequestDto(null, title, content, authorId, null);
			when(newsService.create(request)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), AUTHOR_ENTITY_NAME, authorId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(NEWS_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verify(newsService, times(1)).create(request);
		}

		@Test
		void create_shouldReturn409_whenTitleAlreadyExists() {
			final String title = news.get(0).title();
			final String content = "New Content";
			final long authorId = 1L;
			final NewsRequestDto request = new NewsRequestDto(null, title, content, authorId, null);
			when(newsService.create(request)).thenThrow(new EntityConstraintViolationServiceException(
				NEWS_CONSTRAINT_VIOLATION.getMessage(),
				NEWS_CONSTRAINT_VIOLATION.getCode()
			));
			final int EXPECTED_STATUS_CODE = 409;	// HttpStatus.CONFLICT

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(NEWS_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verify(newsService, times(1)).create(request);
		}

		@Test
		void create_shouldReturn201_whenRequestIsCorrect() {
			final String title = "New Name";
			final String content = "New Content";
			final long authorId = 1L;
			final NewsRequestDto request = new NewsRequestDto(null, title, content, authorId, null);
			final LocalDateTime date = LocalDateTime.now();
			final List<Long> tags = new ArrayList<>();
			final List<Long> comments = new ArrayList<>();
			final int initialSize = news.size();
			final NewsResponseDto created =
				new NewsResponseDto((long) (initialSize + 1), title, content, date, date, authorId, tags, comments);
			when(newsService.create(request)).thenReturn(created);
			final int EXPECTED_STATUS_CODE = 201;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(NEWS_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE)
				.body("id", equalTo(initialSize + 1))
				.body("title", equalTo(title))
				.body("content", equalTo(content));
			verify(newsService, times(1)).create(request);
		}
	}

	@Nested
	class TestUpdate {

		@Test
		void update_shouldReturn409_whenPathIdAndRequestIdDoNotMatch() {
			final long pathId = 1L;
			final long newsId = 2L;
			final String updatedTitle = "Updated Title";
			final String updatedContent = "Updated Content";
			final long authorId = 1L;
			final NewsRequestDto request =
				new NewsRequestDto(newsId, updatedTitle, updatedContent, authorId, null);
			final int EXPECTED_STATUS_CODE = 409;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(NEWS_ROOT_PATH + "/" + pathId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(newsService);
		}

		@Test
		void update_shouldReturn404_whenEntityNotFoundById() {
			final long newsId = 99L;
			final String updatedTitle = "Updated Title";
			final String updatedContent = "Updated Content";
			final long authorId = 1L;
			final NewsRequestDto request =
				new NewsRequestDto(newsId, updatedTitle, updatedContent, authorId, null);
			when(newsService.update(request)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), NEWS_ENTITY_NAME, newsId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(NEWS_ROOT_PATH + "/" + newsId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verify(newsService, times(1)).update(request);
		}

		@Test
		void update_shouldReturn400_whenTitleIsEmpty() {
			final long newsId = 2L;
			final String updatedTitle = "";
			final String updatedContent = "Updated Content";
			final long authorId = 1L;
			final NewsRequestDto request =
				new NewsRequestDto(newsId, updatedTitle, updatedContent, authorId, null);
			final int EXPECTED_STATUS_CODE = 400;	// HttpStatus.BAD_REQUEST

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(NEWS_ROOT_PATH + "/" + newsId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(newsService);
		}

		@Test
		void update_shouldReturn400_whenContentIsEmpty() {
			final long newsId = 2L;
			final String updatedTitle = "Updated title";
			final String updatedContent = "";
			final long authorId = 1L;
			final NewsRequestDto request =
				new NewsRequestDto(newsId, updatedTitle, updatedContent, authorId, null);
			final int EXPECTED_STATUS_CODE = 400;	// HttpStatus.BAD_REQUEST

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(NEWS_ROOT_PATH + "/" + newsId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(newsService);
		}

		@Test
		void update_shouldReturn400_whenAuthorIdIsZero() {
			final long newsId = 2L;
			final String updatedTitle = "Updated title";
			final String updatedContent = "Updated content";
			final long authorId = 0;
			final NewsRequestDto request =
				new NewsRequestDto(newsId, updatedTitle, updatedContent, authorId, null);
			final int EXPECTED_STATUS_CODE = 400;	// HttpStatus.BAD_REQUEST

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(NEWS_ROOT_PATH + "/" + newsId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(newsService);
		}

		@Test
		void update_shouldReturn400_whenAuthorIdIsNegative() {
			final long newsId = 2L;
			final String updatedTitle = "Updated title";
			final String updatedContent = "Updated content";
			final long authorId = -1L;
			final NewsRequestDto request =
				new NewsRequestDto(newsId, updatedTitle, updatedContent, authorId, null);
			final int EXPECTED_STATUS_CODE = 400;	// HttpStatus.BAD_REQUEST

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(NEWS_ROOT_PATH + "/" + newsId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(newsService);
		}

		@Test
		void update_shouldReturn404_whenAuthorNotFoundById() {
			final long newsId = 2L;
			final String updatedTitle = "Updated Title";
			final String updatedContent = "Updated Content";
			final long authorId = 99L;
			final NewsRequestDto request =
				new NewsRequestDto(newsId, updatedTitle, updatedContent, authorId, null);
			when(newsService.update(request)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), AUTHOR_ENTITY_NAME, authorId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(NEWS_ROOT_PATH + "/" + newsId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verify(newsService, times(1)).update(request);
		}

		@Test
		void update_shouldReturn409_whenTitleAlreadyExists() {
			final long newsId = 2L;
			final String updatedTitle = "Updated Title";
			final String updatedContent = "Updated Content";
			final long authorId = 1L;
			final NewsRequestDto request =
				new NewsRequestDto(newsId, updatedTitle, updatedContent, authorId, null);
			when(newsService.update(request)).thenThrow(new EntityConstraintViolationServiceException(
				NEWS_CONSTRAINT_VIOLATION.getMessage(),
				NEWS_CONSTRAINT_VIOLATION.getCode()
			));
			final int EXPECTED_STATUS_CODE = 409;	// HttpStatus.CONFLICT

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(NEWS_ROOT_PATH + "/" + newsId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verify(newsService, times(1)).update(request);
		}

		@Test
		void update_shouldReturn200_whenRequestIsCorrect() {
			final long newsId = 2L;
			final String updatedTitle = "Updated Title";
			final String updatedContent = "Updated Content";
			final long authorId = 1L;
			final NewsRequestDto request =
				new NewsRequestDto(newsId, updatedTitle, updatedContent, authorId, null);
			final LocalDateTime date = LocalDateTime.now();
			final List<Long> tags = new ArrayList<>();
			final List<Long> comments = new ArrayList<>();
			final NewsResponseDto updated =
				new NewsResponseDto(newsId, updatedTitle, updatedContent, date, date, authorId, tags, comments);
			when(newsService.update(request)).thenReturn(updated);
			final int EXPECTED_STATUS_CODE = 200;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(NEWS_ROOT_PATH + "/" + newsId)
				.then().statusCode(EXPECTED_STATUS_CODE)
				.body("id", equalTo((int) newsId))
				.body("title", equalTo(updatedTitle))
				.body("content", equalTo(updatedContent))
				.body("authorId", equalTo((int) authorId));
			verify(newsService, times(1)).update(request);
		}
	}

	@Nested
	class TestDelete {

		@Test
		void deleteById_shouldReturn404_whenEntityNotFoundById() {
			final long newsId = 99L;
			when(newsService.deleteById(newsId)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), NEWS_ENTITY_NAME, newsId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.delete(NEWS_ROOT_PATH + "/" + newsId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE);
			verify(newsService, times(1)).deleteById(newsId);
		}

		@Test
		void deleteById_shouldReturn204_whenRequestIsCorrect() {
			final long newsId = 1L;
			when(newsService.deleteById(newsId)).thenReturn(true);
			final int EXPECTED_STATUS_CODE = 204;

			RestAssured.given()
				.delete(NEWS_ROOT_PATH + "/" + newsId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE);
			verify(newsService, times(1)).deleteById(newsId);
		}
	}
}
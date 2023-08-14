package com.mjc.school.controller.impl;

import com.mjc.school.controller.ControllerTestConfig;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.dto.CommentRequestDto;
import com.mjc.school.service.dto.CommentResponseDto;
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

import static com.mjc.school.controller.constants.Constants.COMMENT_ROOT_PATH;
import static com.mjc.school.controller.constants.Constants.NEWS_ROOT_PATH;
import static com.mjc.school.service.constants.Constants.COMMENT_ENTITY_NAME;
import static com.mjc.school.service.constants.Constants.NEWS_ENTITY_NAME;
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
class RestAssuredCommentControllerTest {

	private static final String BASE_URI = "http://localhost";
	private static final String REQUEST_MAPPING_URI = "/api/v1";
	private static final String CONTENT_TYPE = "application/json";

	@Autowired
	private CommentService commentService;
	@LocalServerPort
	private int port;
	private List<CommentResponseDto> comments;

	@BeforeEach
	public void setUp() {
		reset(commentService);

		RestAssured.baseURI = BASE_URI;
		RestAssured.port = port;
		RestAssured.basePath = REQUEST_MAPPING_URI;

		final long newsId = 1L;
		final LocalDateTime date = LocalDateTime.now();
		comments = Arrays.asList(
			new CommentResponseDto(1L, "Content One", newsId, date, date),
			new CommentResponseDto(2L, "Content Two", newsId, date, date)
		);
	}

	@Test
	void readAll_shouldReturn200_whenRequestIsCorrect() {
		when(commentService.readAll(10, 0, "id::asc")).thenReturn(comments);
		final int EXPECTED_STATUS_CODE = 200;

		RestAssured.given()
			.get(COMMENT_ROOT_PATH)
			.then().assertThat().statusCode(EXPECTED_STATUS_CODE)
			.body(containsString(comments.get(0).content()))
			.body(containsString(comments.get(1).content()));
		verify(commentService, times(1)).readAll(10, 0, "id::asc");
	}

	@Nested
	class TestReadById {

		@Test
		void readById_shouldReturn404_whenEntityNotFoundById() {
			final long commentId = 99L;
			when(commentService.readById(commentId)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), COMMENT_ENTITY_NAME, commentId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.get(COMMENT_ROOT_PATH + "/" + commentId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE);
			verify(commentService, times(1)).readById(commentId);
		}

		@Test
		void readById_shouldReturn200_whenRequestIsCorrectAndEntityExists() {
			final int commentId = 2;
			when(commentService.readById((long) commentId)).thenReturn(comments.get(commentId - 1));
			final int EXPECTED_STATUS_CODE = 200;

			RestAssured.given()
				.get(COMMENT_ROOT_PATH + "/" + commentId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE)
				.body("id", equalTo(commentId))
				.body("content", equalTo(comments.get(commentId - 1).content()));
			verify(commentService, times(1)).readById((long) commentId);
		}
	}

	@Nested
	class TestReadByNewsId {

		@Test
		void readCommentsByNewsId_shouldReturn404_whenNewsNotFoundById() {
			final long newsId = 99L;
			when(commentService.readCommentsByNewsId(newsId)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), NEWS_ENTITY_NAME, newsId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.get(NEWS_ROOT_PATH + "/" + newsId + "/comments")
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE);
			verify(commentService, times(1)).readCommentsByNewsId(newsId);
		}

		@Test
		void readCommentsByNewsId_shouldReturn200_whenRequestIsCorrectAndNewsExists() {
			final long newsId = 1L;
			when(commentService.readCommentsByNewsId(newsId)).thenReturn(comments);
			final int EXPECTED_STATUS_CODE = 200;

			RestAssured.given()
				.get(NEWS_ROOT_PATH + "/" + newsId + "/comments")
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE)
				.body(containsString(comments.get(0).content()))
				.body(containsString(comments.get(1).content()));
			verify(commentService, times(1)).readCommentsByNewsId(newsId);
		}
	}

	@Nested
	class TestCreate {

		@Test
		void create_shouldReturn400_whenContentIsEmpty() {
			final String content = "";
			final long newsId = 1L;
			final CommentRequestDto request = new CommentRequestDto(null, content, newsId);
			final int EXPECTED_STATUS_CODE = 400;	// HttpStatus.BAD_REQUEST

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(COMMENT_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(commentService);
		}

		@Test
		void create_shouldReturn400_whenNewsIdIsZero() {
			final String content = "Some valid content";
			final long newsId = 0;
			final CommentRequestDto request = new CommentRequestDto(null, content, newsId);
			final int EXPECTED_STATUS_CODE = 400;	// HttpStatus.BAD_REQUEST

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(COMMENT_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(commentService);
		}

		@Test
		void create_shouldReturn400_whenNewsIdIsNegative() {
			final String content = "Some valid content";
			final long newsId = -1L;
			final CommentRequestDto request = new CommentRequestDto(null, content, newsId);
			final int EXPECTED_STATUS_CODE = 400;	// HttpStatus.BAD_REQUEST

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(COMMENT_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(commentService);
		}

		@Test
		void create_shouldReturn404_whenNewsNotFoundById() {
			final String content = "New Content";
			final long newsId = 99L;
			final CommentRequestDto request = new CommentRequestDto(null, content, newsId);
			when(commentService.create(request)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), NEWS_ENTITY_NAME, newsId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(COMMENT_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verify(commentService, times(1)).create(request);
		}

		@Test
		void create_shouldReturn201_whenRequestIsCorrect() {
			final String content = "New Content";
			final long newsId = 1L;
			final CommentRequestDto request = new CommentRequestDto(null, content, newsId);
			final LocalDateTime date = LocalDateTime.now();
			final int initialSize = comments.size();
			final CommentResponseDto created =
				new CommentResponseDto((long) (initialSize + 1), content, newsId, date, date);
			when(commentService.create(request)).thenReturn(created);
			final int EXPECTED_STATUS_CODE = 201;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().post(COMMENT_ROOT_PATH)
				.then().statusCode(EXPECTED_STATUS_CODE)
				.body("id", equalTo(initialSize + 1))
				.body("content", equalTo(content))
				.body("newsId", equalTo((int) newsId));
			verify(commentService, times(1)).create(request);
		}
	}

	@Nested
	class TestUpdate {

		@Test
		void update_shouldReturn409_whenPathIdAndRequestIdDoNotMatch() {
			final long pathId = 1L;
			final long commentId = 2L;
			final String updatedContent = "Updated Comment";
			final long newsId = 1L;
			final CommentRequestDto request = new CommentRequestDto(commentId, updatedContent, newsId);
			final int EXPECTED_STATUS_CODE = 409;	// HttpStatus.CONFLICT

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(COMMENT_ROOT_PATH + "/" + pathId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(commentService);
		}

		@Test
		void update_shouldReturn404_whenEntityNotFoundById() {
			final long commentId = 99L;
			final String updatedContent = "Updated Comment";
			final long newsId = 1L;
			final CommentRequestDto request = new CommentRequestDto(commentId, updatedContent, newsId);
			when(commentService.update(request)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), COMMENT_ENTITY_NAME, commentId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(COMMENT_ROOT_PATH + "/" + commentId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verify(commentService, times(1)).update(request);
		}

		@Test
		void update_shouldReturn400_whenContentIsEmpty() {
			final long commentId = 2L;
			final String updatedContent = "";
			final long newsId = 1L;
			final CommentRequestDto request = new CommentRequestDto(commentId, updatedContent, newsId);
			final int EXPECTED_STATUS_CODE = 400;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(COMMENT_ROOT_PATH + "/" + commentId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verifyNoInteractions(commentService);
		}

		@Test
		void update_shouldReturn404_whenNewsNotFoundById() {
			final long commentId = 2L;
			final String updatedContent = "Updated Comment";
			final long newsId = 99L;
			final CommentRequestDto request = new CommentRequestDto(commentId, updatedContent, newsId);
			when(commentService.update(request)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), NEWS_ENTITY_NAME, commentId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(COMMENT_ROOT_PATH + "/" + commentId)
				.then().statusCode(EXPECTED_STATUS_CODE);
			verify(commentService, times(1)).update(request);
		}

		@Test
		void update_shouldReturn200_whenRequestIsCorrect() {
			final long commentId = 2L;
			final String updatedContent = "Updated Comment";
			final long newsId = 1L;
			final CommentRequestDto request = new CommentRequestDto(commentId, updatedContent, newsId);
			final LocalDateTime date = LocalDateTime.now();
			final CommentResponseDto updated =
				new CommentResponseDto(commentId, updatedContent, newsId, date, date);
			when(commentService.update(request)).thenReturn(updated);
			final int EXPECTED_STATUS_CODE = 200;

			RestAssured.given()
				.contentType(CONTENT_TYPE)
				.body(request)
				.when().patch(COMMENT_ROOT_PATH + "/" + commentId)
				.then().statusCode(EXPECTED_STATUS_CODE)
				.body("id", equalTo((int) commentId))
				.body("content", equalTo(updatedContent))
				.body("newsId", equalTo((int) newsId));
			verify(commentService, times(1)).update(request);
		}
	}

	@Nested
	class TestDelete {

		@Test
		void deleteById_shouldReturn404_whenEntityNotFoundById() {
			final long commentId = 99L;
			when(commentService.deleteById(commentId)).thenThrow(new EntityNotFoundException(
				String.format(ENTITY_NOT_FOUND_BY_ID.getMessage(), COMMENT_ENTITY_NAME, commentId),
				ENTITY_NOT_FOUND_BY_ID.getCode()
			));
			final int EXPECTED_STATUS_CODE = 404;

			RestAssured.given()
				.delete(COMMENT_ROOT_PATH + "/" + commentId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE);
			verify(commentService, times(1)).deleteById(commentId);
		}

		@Test
		void deleteById_shouldReturn204_whenRequestIsCorrect() {
			final long commentId = 1L;
			when(commentService.deleteById(commentId)).thenReturn(true);
			final int EXPECTED_STATUS_CODE = 204;

			RestAssured.given()
				.delete(COMMENT_ROOT_PATH + "/" + commentId)
				.then().assertThat().statusCode(EXPECTED_STATUS_CODE);
			verify(commentService, times(1)).deleteById(commentId);
		}
	}
}
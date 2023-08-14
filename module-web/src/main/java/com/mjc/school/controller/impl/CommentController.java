package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.controller.versioning.ApiVersion;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.dto.CommentRequestDto;
import com.mjc.school.service.dto.CommentResponseDto;
import com.mjc.school.service.validator.annotation.Min;
import com.mjc.school.service.validator.annotation.NotNull;
import com.mjc.school.service.validator.annotation.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.mjc.school.controller.constants.Constants.API_ROOT_PATH;
import static com.mjc.school.controller.constants.Constants.COMMENT_ROOT_PATH;
import static com.mjc.school.controller.constants.Constants.NEWS_ROOT_PATH;
import static com.mjc.school.service.constants.Constants.ID_MIN_VALUE;

@RestController
@ApiVersion(1)
@RequestMapping(API_ROOT_PATH)
@Api(produces = "application/json", value = "Operations for creating, updating, retrieving and deleting comments")
public class CommentController implements BaseController<CommentResponseDto, CommentRequestDto, Long> {

	private final CommentService commentService;

	public CommentController(final CommentService commentService) {
		this.commentService = commentService;
	}

	@Override
	@ApiOperation(value = "View all comments", response = List.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully retrieved all comments"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@GetMapping(COMMENT_ROOT_PATH)
	public ResponseEntity<List<CommentResponseDto>> readAll(
		@RequestParam(defaultValue = "10", required = false) @Min(1) final int limit,
		@RequestParam(defaultValue = "0", required = false) @Min(0) final int offset,
		@RequestParam(defaultValue = "id::asc", required = false) final String orderBy
	) {
		return ResponseEntity.ok(commentService.readAll(limit, offset, orderBy));
	}

	@Override
	@ApiOperation(value = "Retrieve specific comment with the supplied id", response = CommentResponseDto.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully retrieved the comment with the supplied id"),
		@ApiResponse(code = 400, message = "Request violates any of existing constraints"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@GetMapping(COMMENT_ROOT_PATH + "/{id:\\d+}")
	public ResponseEntity<CommentResponseDto> readById(
		@PathVariable @NotNull @Min(ID_MIN_VALUE) final Long id
	) {
		return ResponseEntity.ok(commentService.readById(id));
	}

	@ApiOperation(value = "Retrieve comments by supplied news id", response = List.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully retrieved comments by supplied news id"),
		@ApiResponse(code = 400, message = "Request violates any of existing constraints"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@GetMapping(NEWS_ROOT_PATH + "/{newsId:\\d+}/comments")
	public ResponseEntity<List<CommentResponseDto>> readCommentsByNewsId(
		@PathVariable("newsId") @NotNull @Min(ID_MIN_VALUE) final Long newsId
	) {
		return ResponseEntity.ok(commentService.readCommentsByNewsId(newsId));
	}

	@Override
	@PostMapping(path = COMMENT_ROOT_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create new author", response = CommentResponseDto.class)
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = "Successfully created new comment"),
		@ApiResponse(code = 400, message = "Request violates any of existing constraints"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<CommentResponseDto> create(
		@RequestBody @Valid final CommentRequestDto request
	) {
		return new ResponseEntity<>(commentService.create(request), HttpStatus.CREATED);
	}

	@Override
	@ApiOperation(value = "Update specific comment information", response = CommentResponseDto.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully updated comment information"),
		@ApiResponse(code = 400, message = "Request violates any of existing constraints"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@PatchMapping(path = COMMENT_ROOT_PATH + "/{id:\\d+}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CommentResponseDto> update(
		@PathVariable Long id,
		@RequestBody @Valid final CommentRequestDto request
	) {
		if (!id.equals(request.id())) {
			throw new IllegalArgumentException("Path id and request id do not match");
		}
		return ResponseEntity.ok(commentService.update(request));
	}

	@Override
	@ApiOperation(value = "Deletes specific comment with the supplied id")
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully deletes the specific comment"),
		@ApiResponse(code = 400, message = "Request violates any of existing constraints"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@DeleteMapping(COMMENT_ROOT_PATH + "/{id:\\d+}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@PathVariable @NotNull @Min(ID_MIN_VALUE) final Long id) {
		commentService.deleteById(id);
	}
}
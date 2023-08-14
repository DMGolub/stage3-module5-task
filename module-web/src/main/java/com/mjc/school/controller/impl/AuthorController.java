package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.controller.versioning.ApiVersion;
import com.mjc.school.service.AuthorService;
import com.mjc.school.service.dto.AuthorRequestDto;
import com.mjc.school.service.dto.AuthorResponseDto;
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
import static com.mjc.school.controller.constants.Constants.AUTHOR_ROOT_PATH;
import static com.mjc.school.controller.constants.Constants.NEWS_ROOT_PATH;
import static com.mjc.school.service.constants.Constants.ID_MIN_VALUE;

@RestController
@ApiVersion(1)
@RequestMapping(API_ROOT_PATH)
@Api(produces = "application/json", value = "Operations for creating, updating, retrieving and deleting authors")
public class AuthorController implements BaseController<AuthorResponseDto, AuthorRequestDto, Long> {

	private final AuthorService authorService;

	public AuthorController(final AuthorService authorService) {
		this.authorService = authorService;
	}

	@Override
	@ApiOperation(value = "View all authors", response = List.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully retrieved all authors"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@GetMapping(AUTHOR_ROOT_PATH)
	public ResponseEntity<List<AuthorResponseDto>> readAll(
		@RequestParam(defaultValue = "10", required = false) @Min(1) final int limit,
		@RequestParam(defaultValue = "0", required = false) @Min(0) final int offset,
		@RequestParam(name = "order_by", defaultValue = "id::asc", required = false) final String orderBy
	) {
		return ResponseEntity.ok(authorService.readAll(limit, offset, orderBy));
	}

	@Override
	@ApiOperation(value = "Retrieve specific author with the supplied id", response = AuthorResponseDto.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully retrieved the author with the supplied id"),
		@ApiResponse(code = 400, message = "Request violates any of existing constraints"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@GetMapping(AUTHOR_ROOT_PATH + "/{id:\\d+}")
	public ResponseEntity<AuthorResponseDto> readById(
		@PathVariable @NotNull @Min(ID_MIN_VALUE) final Long id
	) {
		return ResponseEntity.ok(authorService.readById(id));
	}

	@ApiOperation(value = "Retrieve specific author by supplied news id", response = AuthorResponseDto.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully retrieved the author by supplied news id"),
		@ApiResponse(code = 400, message = "Request violates any of existing constraints"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@GetMapping(NEWS_ROOT_PATH + "/{newsId:\\d+}/author")
	public ResponseEntity<AuthorResponseDto> readByNewsId(
		@PathVariable("newsId") @NotNull @Min(ID_MIN_VALUE) final Long newsId
	) {
		return ResponseEntity.ok(authorService.readAuthorByNewsId(newsId));
	}

	@Override
	@PostMapping(path = AUTHOR_ROOT_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create new author", response = AuthorResponseDto.class)
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = "Successfully created new author"),
		@ApiResponse(code = 400, message = "Request violates any of existing constraints"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 409, message = "Author you are trying to save has a name conflict: name already exists"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<AuthorResponseDto> create(@RequestBody @Valid final AuthorRequestDto request) {
		return new ResponseEntity<>(authorService.create(request), HttpStatus.CREATED);
	}

	@Override
	@ApiOperation(value = "Update specific author information", response = AuthorRequestDto.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully updated author information"),
		@ApiResponse(code = 400, message = "Request violates any of existing constraints"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@PatchMapping(path = AUTHOR_ROOT_PATH + "/{id:\\d+}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AuthorResponseDto> update(
		@PathVariable Long id,
		@RequestBody @Valid final AuthorRequestDto request
	) {
		if (!id.equals(request.id())) {
			throw new IllegalArgumentException("Path id and request id do not match");
		}
		return ResponseEntity.ok(authorService.update(request));
	}

	@Override
	@ApiOperation(value = "Deletes specific author with the supplied id")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = "Successfully deletes the specific author"),
		@ApiResponse(code = 400, message = "Request violates any of existing constraints"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@DeleteMapping(AUTHOR_ROOT_PATH + "/{id:\\d+}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@PathVariable @NotNull @Min(ID_MIN_VALUE) final Long id) {
		authorService.deleteById(id);
	}
}
package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.controller.versioning.ApiVersion;
import com.mjc.school.service.TagService;
import com.mjc.school.service.dto.TagRequestDto;
import com.mjc.school.service.dto.TagResponseDto;
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
import static com.mjc.school.controller.constants.Constants.NEWS_ROOT_PATH;
import static com.mjc.school.controller.constants.Constants.TAG_ROOT_PATH;
import static com.mjc.school.service.constants.Constants.ID_MIN_VALUE;

@RestController
@ApiVersion(1)
@RequestMapping(API_ROOT_PATH)
@Api(produces = "application/json", value = "Operations for creating, updating, retrieving and deleting tags")
public class TagController implements BaseController<TagResponseDto, TagRequestDto, Long> {

	private final TagService tagService;

	public TagController(final TagService tagService) {
		this.tagService = tagService;
	}

	@Override
	@ApiOperation(value = "View all tags", response = List.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully retrieved all tags"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@GetMapping(TAG_ROOT_PATH)
	public ResponseEntity<List<TagResponseDto>> readAll(
		@RequestParam(defaultValue = "10", required = false) @Min(1) final int limit,
		@RequestParam(defaultValue = "0", required = false) @Min(0) final int offset,
		@RequestParam(defaultValue = "id::asc", required = false) final String orderBy
	) {
		return ResponseEntity.ok(tagService.readAll(limit, offset, orderBy));
	}

	@Override
	@ApiOperation(value = "Retrieve specific tag with the supplied id", response = TagResponseDto.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully retrieved the tag with the supplied id"),
		@ApiResponse(code = 400, message = "Request violates any of existing constraints"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@GetMapping(TAG_ROOT_PATH + "/{id:\\d+}")
	public ResponseEntity<TagResponseDto> readById(
		@PathVariable @NotNull @Min(ID_MIN_VALUE) final Long id
	) {
		return ResponseEntity.ok(tagService.readById(id));
	}

	@ApiOperation(value = "Retrieve tags by supplied news id", response = List.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully retrieved tags by supplied news id"),
		@ApiResponse(code = 400, message = "Request violates any of existing constraints"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@GetMapping(NEWS_ROOT_PATH + "/{newsId:\\d+}/tags")
	public ResponseEntity<List<TagResponseDto>> readTagsByNewsId(
		@PathVariable("newsId") @NotNull @Min(ID_MIN_VALUE) final Long newsId
	) {
		return ResponseEntity.ok(tagService.readTagsByNewsId(newsId));
	}

	@Override
	@PostMapping(path = TAG_ROOT_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create new author", response = TagResponseDto.class)
	@ApiResponses(value = {
		@ApiResponse(code = 201, message = "Successfully created new tag"),
		@ApiResponse(code = 400, message = "Request violates any of existing constraints"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 409, message = "Tag you are trying to save has a name conflict: name already exists"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<TagResponseDto> create(@RequestBody @Valid final TagRequestDto request) {
		return new ResponseEntity<>(tagService.create(request), HttpStatus.CREATED);
	}

	@Override
	@ApiOperation(value = "Update specific tag information", response = TagResponseDto.class)
	@ApiResponses(value = {
		@ApiResponse(code = 200, message = "Successfully updated tag information"),
		@ApiResponse(code = 400, message = "Request violates any of existing constraints"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@PatchMapping(path = TAG_ROOT_PATH + "/{id:\\d+}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TagResponseDto> update(
		@PathVariable Long id,
		@RequestBody @Valid final TagRequestDto request
	) {
		if (!id.equals(request.id())) {
			throw new IllegalArgumentException("Path id and request id do not match");
		}
		return ResponseEntity.ok(tagService.update(request));
	}

	@Override
	@ApiOperation(value = "Deletes specific tag with the supplied id")
	@ApiResponses(value = {
		@ApiResponse(code = 204, message = "Successfully deletes the specific tag"),
		@ApiResponse(code = 400, message = "Request violates any of existing constraints"),
		@ApiResponse(code = 401, message = "You are not authorized"),
		@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
		@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
		@ApiResponse(code = 500, message = "Application failed to process the request"),
		@ApiResponse(code = 503, message = "Api version you are trying to use is not supported")
	})
	@DeleteMapping(TAG_ROOT_PATH + "/{id:\\d+}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@PathVariable @NotNull @Min(ID_MIN_VALUE) final Long id) {
		tagService.deleteById(id);
	}
}
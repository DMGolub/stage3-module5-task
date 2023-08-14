package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.controller.versioning.ApiVersion;
import com.mjc.school.service.TagService;
import com.mjc.school.service.dto.TagRequestDto;
import com.mjc.school.service.dto.TagResponseDto;
import com.mjc.school.service.validator.annotation.Min;
import com.mjc.school.service.validator.annotation.NotNull;
import com.mjc.school.service.validator.annotation.Valid;
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
@RequestMapping(path = API_ROOT_PATH, produces = {"application/JSON"})
public class TagController implements BaseController<TagResponseDto, TagRequestDto, Long> {

	private final TagService tagService;

	public TagController(final TagService tagService) {
		this.tagService = tagService;
	}

	@Override
	@GetMapping(TAG_ROOT_PATH)
	public ResponseEntity<List<TagResponseDto>> readAll(
		@RequestParam(defaultValue = "10", required = false) @Min(1) final int limit,
		@RequestParam(defaultValue = "0", required = false) @Min(0) final int offset,
		@RequestParam(defaultValue = "id::asc", required = false) final String orderBy
	) {
		return ResponseEntity.ok(tagService.readAll(limit, offset, orderBy));
	}

	@Override
	@GetMapping(TAG_ROOT_PATH + "/{id:\\d+}")
	public ResponseEntity<TagResponseDto> readById(
		@PathVariable @NotNull @Min(ID_MIN_VALUE) final Long id
	) {
		return ResponseEntity.ok(tagService.readById(id));
	}

	@GetMapping(NEWS_ROOT_PATH + "/{newsId:\\d+}/tags")
	public ResponseEntity<List<TagResponseDto>> readTagsByNewsId(
		@PathVariable("newsId") @NotNull @Min(ID_MIN_VALUE) final Long newsId
	) {
		return ResponseEntity.ok(tagService.readTagsByNewsId(newsId));
	}

	@Override
	@PostMapping(path = TAG_ROOT_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<TagResponseDto> create(@RequestBody @Valid final TagRequestDto request) {
		return new ResponseEntity<>(tagService.create(request), HttpStatus.CREATED);
	}

	@Override
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
	@DeleteMapping(TAG_ROOT_PATH + "/{id:\\d+}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@PathVariable @NotNull @Min(ID_MIN_VALUE) final Long id) {
		tagService.deleteById(id);
	}
}
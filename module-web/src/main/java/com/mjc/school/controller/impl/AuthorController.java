package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.controller.versioning.ApiVersion;
import com.mjc.school.service.AuthorService;
import com.mjc.school.service.dto.AuthorRequestDto;
import com.mjc.school.service.dto.AuthorResponseDto;
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
import static com.mjc.school.controller.constants.Constants.AUTHOR_ROOT_PATH;
import static com.mjc.school.controller.constants.Constants.NEWS_ROOT_PATH;
import static com.mjc.school.service.constants.Constants.ID_MIN_VALUE;

@RestController
@ApiVersion(1)
@RequestMapping(path = API_ROOT_PATH, produces = {"application/JSON"})
public class AuthorController implements BaseController<AuthorResponseDto, AuthorRequestDto, Long> {

	private final AuthorService authorService;

	public AuthorController(final AuthorService authorService) {
		this.authorService = authorService;
	}

	@Override
	@GetMapping(AUTHOR_ROOT_PATH)
	public ResponseEntity<List<AuthorResponseDto>> readAll(
		@RequestParam(defaultValue = "10", required = false) @Min(1) final int limit,
		@RequestParam(defaultValue = "0", required = false) @Min(0) final int offset,
		@RequestParam(name = "order_by", defaultValue = "id::asc", required = false) final String orderBy
	) {
		return ResponseEntity.ok(authorService.readAll(limit, offset, orderBy));
	}

	@Override
	@GetMapping(AUTHOR_ROOT_PATH + "/{id:\\d+}")
	public ResponseEntity<AuthorResponseDto> readById(
		@PathVariable @NotNull @Min(ID_MIN_VALUE) final Long id
	) {
		return ResponseEntity.ok(authorService.readById(id));
	}

	@GetMapping(NEWS_ROOT_PATH + "/{newsId:\\d+}/author")
	public ResponseEntity<AuthorResponseDto> readByNewsId(
		@PathVariable("newsId") @NotNull @Min(ID_MIN_VALUE) final Long newsId
	) {
		return ResponseEntity.ok(authorService.readAuthorByNewsId(newsId));
	}

	@Override
	@PostMapping(path = AUTHOR_ROOT_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<AuthorResponseDto> create(@RequestBody @Valid final AuthorRequestDto request) {
		return new ResponseEntity<>(authorService.create(request), HttpStatus.CREATED);
	}

	@Override
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
	@DeleteMapping(AUTHOR_ROOT_PATH + "/{id:\\d+}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@PathVariable @NotNull @Min(ID_MIN_VALUE) final Long id) {
		authorService.deleteById(id);
	}
}
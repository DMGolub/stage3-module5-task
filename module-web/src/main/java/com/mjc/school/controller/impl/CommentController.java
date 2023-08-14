package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.controller.versioning.ApiVersion;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.dto.CommentRequestDto;
import com.mjc.school.service.dto.CommentResponseDto;
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
import static com.mjc.school.controller.constants.Constants.COMMENT_ROOT_PATH;
import static com.mjc.school.controller.constants.Constants.NEWS_ROOT_PATH;
import static com.mjc.school.service.constants.Constants.ID_MIN_VALUE;

@RestController
@ApiVersion(1)
@RequestMapping(path = API_ROOT_PATH, produces = {"application/JSON"})
public class CommentController implements BaseController<CommentResponseDto, CommentRequestDto, Long> {

	private final CommentService commentService;

	public CommentController(final CommentService commentService) {
		this.commentService = commentService;
	}

	@Override
	@GetMapping(COMMENT_ROOT_PATH)
	public ResponseEntity<List<CommentResponseDto>> readAll(
		@RequestParam(defaultValue = "10", required = false) @Min(1) final int limit,
		@RequestParam(defaultValue = "0", required = false) @Min(0) final int offset,
		@RequestParam(defaultValue = "id::asc", required = false) final String orderBy
	) {
		return ResponseEntity.ok(commentService.readAll(limit, offset, orderBy));
	}

	@Override
	@GetMapping(COMMENT_ROOT_PATH + "/{id:\\d+}")
	public ResponseEntity<CommentResponseDto> readById(
		@PathVariable @NotNull @Min(ID_MIN_VALUE) final Long id
	) {
		return ResponseEntity.ok(commentService.readById(id));
	}

	@GetMapping(NEWS_ROOT_PATH + "/{newsId:\\d+}/comments")
	public ResponseEntity<List<CommentResponseDto>> readCommentsByNewsId(
		@PathVariable("newsId") @NotNull @Min(ID_MIN_VALUE) final Long newsId
	) {
		return ResponseEntity.ok(commentService.readCommentsByNewsId(newsId));
	}

	@Override
	@PostMapping(path = COMMENT_ROOT_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<CommentResponseDto> create(
		@RequestBody @Valid final CommentRequestDto request
	) {
		return new ResponseEntity<>(commentService.create(request), HttpStatus.CREATED);
	}

	@Override
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
	@DeleteMapping(COMMENT_ROOT_PATH + "/{id:\\d+}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@PathVariable @NotNull @Min(ID_MIN_VALUE) final Long id) {
		commentService.deleteById(id);
	}
}
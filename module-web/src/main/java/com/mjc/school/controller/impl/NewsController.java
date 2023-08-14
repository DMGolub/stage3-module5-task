package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.controller.versioning.ApiVersion;
import com.mjc.school.service.NewsService;
import com.mjc.school.service.dto.NewsRequestDto;
import com.mjc.school.service.dto.NewsResponseDto;
import com.mjc.school.service.query.NewsQueryParams;
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
import static com.mjc.school.service.constants.Constants.ID_MIN_VALUE;

@RestController
@ApiVersion(1)
@RequestMapping(path = API_ROOT_PATH, produces = {"application/JSON"})
public class NewsController implements BaseController<NewsResponseDto, NewsRequestDto, Long> {

	private final NewsService newsService;

	public NewsController(final NewsService newsService) {
		this.newsService = newsService;
	}

	@Override
	@GetMapping(NEWS_ROOT_PATH)
	public ResponseEntity<List<NewsResponseDto>> readAll(
		@RequestParam(defaultValue = "10", required = false) @Min(1) final int limit,
		@RequestParam(defaultValue = "0", required = false) @Min(0) final int offset,
		@RequestParam(defaultValue = "id::asc", required = false) final String orderBy
	) {
		return ResponseEntity.ok(newsService.readAll(limit, offset, orderBy));
	}

	@Override
	@GetMapping(NEWS_ROOT_PATH + "/{id:\\d+}")
	public ResponseEntity<NewsResponseDto> readById(
		@PathVariable @NotNull @Min(ID_MIN_VALUE) final Long id
	) {
		return ResponseEntity.ok(newsService.readById(id));
	}

	@GetMapping(NEWS_ROOT_PATH + "/search")
	public ResponseEntity<List<NewsResponseDto>> readNewsByParams(
		@RequestParam(value = "tag_names", required = false) final List<String> tagNames,
		@RequestParam(value = "tag_ids", required = false) final List<Long> tagIds,
		@RequestParam(value = "author_name", required = false) final String authorName,
		@RequestParam(required = false) final String title,
		@RequestParam(required = false) final String content
	) {
		final List<NewsResponseDto> news = newsService.readNewsByParams(
			new NewsQueryParams(tagNames, tagIds, authorName, title, content));
		return ResponseEntity.ok(news);
	}

	@Override
	@PostMapping(path = NEWS_ROOT_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<NewsResponseDto> create(@RequestBody @Valid final NewsRequestDto request) {
		return new ResponseEntity<>(newsService.create(request), HttpStatus.CREATED);
	}

	@Override
	@PatchMapping(path = NEWS_ROOT_PATH + "/{id:\\d+}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NewsResponseDto> update(
		@PathVariable @NotNull @Min(ID_MIN_VALUE) final Long id,
		@RequestBody @Valid final  NewsRequestDto request
	) {
		if (!id.equals(request.id())) {
			throw new IllegalArgumentException("Path id and request id do not match");
		}
		return ResponseEntity.ok(newsService.update(request));
	}

	@Override
	@DeleteMapping(NEWS_ROOT_PATH + "/{id:\\d+}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@PathVariable @NotNull @Min(ID_MIN_VALUE) final Long id) {
		newsService.deleteById(id);
	}
}
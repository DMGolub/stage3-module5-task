package com.mjc.school.controller;

import com.mjc.school.service.AuthorService;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.NewsService;
import com.mjc.school.service.TagService;
import com.mjc.school.service.impl.AuthorServiceImpl;
import com.mjc.school.service.impl.CommentServiceImpl;
import com.mjc.school.service.impl.NewsServiceImpl;
import com.mjc.school.service.impl.TagServiceImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@SpringBootApplication(scanBasePackages = {"com.mjc.school"})
public class ControllerTestConfig {

	@Bean
	@Primary
	public AuthorService authorService() {
		return mock(AuthorServiceImpl.class);
	}

	@Bean
	@Primary
	public CommentService commentService() {
		return mock(CommentServiceImpl.class);
	}

	@Bean
	@Primary
	public NewsService newsService() {
		return mock(NewsServiceImpl.class);
	}

	@Bean
	@Primary
	public TagService tagService() {
		return mock(TagServiceImpl.class);
	}
}
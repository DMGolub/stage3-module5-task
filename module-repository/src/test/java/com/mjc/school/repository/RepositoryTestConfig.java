package com.mjc.school.repository;

import com.mjc.school.repository.config.RepositoryConfig;
import com.mjc.school.repository.impl.AuthorRepositoryImpl;
import com.mjc.school.repository.impl.NewsRepositoryImpl;
import com.mjc.school.repository.impl.TagRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@ComponentScan("com.mjc.school.repository.impl")
@Import(RepositoryConfig.class)
public class RepositoryTestConfig {

	@Bean
	@Primary
	public AuthorRepository authorRepository() {
		return new AuthorRepositoryImpl();
	}

	@Bean
	@Primary
	public NewsRepository newsRepository() {
		return new NewsRepositoryImpl();
	}

	@Bean
	@Primary
	public TagRepository tagRepository() {
		return new TagRepositoryImpl();
	}
}
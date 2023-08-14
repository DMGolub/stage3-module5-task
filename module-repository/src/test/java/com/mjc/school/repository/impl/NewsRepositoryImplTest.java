package com.mjc.school.repository.impl;

import com.mjc.school.repository.NewsRepository;
import com.mjc.school.repository.RepositoryTestConfig;
import com.mjc.school.repository.model.News;
import com.mjc.school.repository.util.Util;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RepositoryTestConfig.class})
class NewsRepositoryImplTest {

	@Autowired
	private NewsRepository repository;

	@Nested
	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
		scripts = {"classpath:truncate_db.sql"})
	class TestReadAll {

		@Test
		void readAll_shouldReturnEmptyList_whenStorageIsEmpty() {
			assertEquals(Collections.emptyList(), repository.readAll());
		}

		@Test
		void readAll_shouldReturnTwoEntities_whenThereAreTwoEntitiesInTheStorage() {
			final List<News> storage = Arrays.asList(
				Util.createTestNews(null),
				Util.createTestNews(null)
			);
			storage.forEach(repository::create);

			final List<News> result = repository.readAll();

			assertEquals(2, result.size());
			assertEquals(1L, result.get(0).getId());
			assertEquals(2L, result.get(1).getId());
		}
	}

	@Nested
	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
		scripts = {"classpath:truncate_db.sql"})
	class TestReadById {

		@Test
		void readById_shouldReturnEmptyOptional_whenThereIsNoEntityWithGivenId() {
			final List<News> storage = Arrays.asList(
				Util.createTestNews(null),
				Util.createTestNews(null)
			);
			storage.forEach(repository::create);

			assertEquals(Optional.empty(), repository.readById(3L));
		}

		@Test
		void readById_shouldReturnEntity_whenEntityWithGivenIdExists() {
			News news = Util.createTestNews(null);
			repository.create(news);

			final Optional<News> result = repository.readById(1L);

			assertTrue(result.isPresent());
			assertEquals(1L, result.get().getId());
			assertEquals(news.getTitle(), result.get().getTitle());
			assertEquals(news.getContent(), result.get().getContent());
		}
	}

	@Nested
	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
		scripts = {"classpath:truncate_db.sql"})
	class TestCreate {

		@Test
		void create_shouldSaveEntityAndReturnEntityWithId1_whenStorageIsEmpty() {
			final News news = Util.createTestNews(null);

			final News result = repository.create(news);

			assertNotNull(result);
			assertEquals(1L, result.getId());
			assertEquals(news.getTitle(), result.getTitle());
			assertEquals(news.getContent(), result.getContent());
			assertEquals(1, repository.readAll().size());
		}

		@Test
		void create_shouldSaveEntityAndReturnEntityWithId3_whenStorageContainsTwoEntities() {
			final List<News> storage = List.of(
				Util.createTestNews(null),
				Util.createTestNews(null)
			);
			storage.forEach(repository::create);

			final News news = Util.createTestNews(null);
			final News result = repository.create(news);

			assertNotNull(result);
			assertEquals(3L, result.getId());
			assertEquals(news.getTitle(), result.getTitle());
			assertEquals(news.getContent(), result.getContent());
			assertEquals(3, repository.readAll().size());
		}
	}

	@Nested
	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
		scripts = {"classpath:truncate_db.sql"})
	class TestUpdate {

		@Test
		void update_shouldReturnNull_whenThereIsNoEntityWithGivenId() {
			final List<News> storage = List.of(
				Util.createTestNews(null),
				Util.createTestNews(null)
			);
			storage.forEach(repository::create);

			final News updated = Util.createTestNews(99L);
			final News result = repository.update(updated);

			assertNull(result);
		}

		@Test
		void update_shouldReturnUpdatedEntity_whenEntityIsValid() {
			final List<News> storage = List.of(
				Util.createTestNews(null),
				Util.createTestNews(null)
			);
			storage.forEach(repository::create);

			final News updated = Util.createTestNews(2L);
			updated.setTitle("Updated title");
			updated.setContent("Updated content");

			final News result = repository.update(updated);

			assertNotNull(result);
			assertEquals(updated.getId(), result.getId());
			assertEquals(updated.getTitle(), result.getTitle());
			assertEquals(updated.getContent(), result.getContent());
		}
	}

	@Nested
	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
		scripts = {"classpath:truncate_db.sql"})
	class TestDeleteById {

		@Test
		void deleteById_shouldReturnFalse_whenIdIsNull() {
			final List<News> storage = Arrays.asList(
				Util.createTestNews(null),
				Util.createTestNews(null)
			);
			storage.forEach(repository::create);

			assertFalse(repository.deleteById(null));
			assertEquals(2, repository.readAll().size());
		}

		@Test
		void deleteById_shouldReturnFalse_whenStorageIsEmpty() {
			assertFalse(repository.deleteById(99L));
		}

		@Test
		void deleteById_shouldReturnFalse_whenThereIsNoEntityWithGivenId() {
			final List<News> storage = Arrays.asList(
				Util.createTestNews(null),
				Util.createTestNews(null)
			);
			storage.forEach(repository::create);

			assertFalse(repository.deleteById(99L));
			assertEquals(2, repository.readAll().size());
		}

		@Test
		void deleteById_shouldReturnTrue_whenEntityWithGivenIdDeleted() {
			final List<News> storage = new ArrayList<>();
			storage.add(Util.createTestNews(null));
			storage.add(Util.createTestNews(null));
			storage.add(Util.createTestNews(null));
			storage.forEach(repository::create);

			assertTrue(repository.deleteById(2L));
			final List<News> result = repository.readAll();
			assertEquals(2, repository.readAll().size());
			assertEquals(1L, result.get(0).getId());
			assertEquals(3L, result.get(1).getId());
		}

		@Test
		void deleteById_shouldDeleteEntity_whenItIsSingleEntityInTheStorage() {
			repository.create(Util.createTestNews(null));

			assertTrue(repository.deleteById(1L));
			assertTrue(repository.readAll().isEmpty());
		}
	}

	@Nested
	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
		scripts = {"classpath:truncate_db.sql"})
	class TestExistById {

		@Test
		void existById_shouldReturnFalse_whenStorageIsEmpty() {
			assertFalse(repository.existById(99L));
		}

		@Test
		void existById_shouldReturnFalse_whenThereIsNoEntityWithGivenId() {
			final List<News> storage = Arrays.asList(
				Util.createTestNews(null),
				Util.createTestNews(null)
			);
			storage.forEach(repository::create);

			assertFalse(repository.existById(99L));
		}

		@Test
		void existById_shouldReturnTrue_whenEntityWithGivenIdExists() {
			final List<News> storage = Arrays.asList(
				Util.createTestNews(null),
				Util.createTestNews(null)
			);
			storage.forEach(repository::create);

			assertTrue(repository.existById(2L));
		}
	}
}
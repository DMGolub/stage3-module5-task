package com.mjc.school.repository.impl;

import com.mjc.school.repository.RepositoryTestConfig;
import com.mjc.school.repository.TagRepository;
import com.mjc.school.repository.model.Tag;
import com.mjc.school.repository.util.Util;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
class TagRepositoryImplTest {

	@Autowired
	private TagRepository repository;

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
			final List<Tag> storage = Arrays.asList(
				Util.createTestTag(null),
				Util.createTestTag(null)
			);
			storage.forEach(repository::create);

			final List<Tag> result = repository.readAll();

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
			final List<Tag> storage = Arrays.asList(
				Util.createTestTag(null),
				Util.createTestTag(null)
			);
			storage.forEach(repository::create);

			assertEquals(Optional.empty(), repository.readById(3L));
		}

		@Test
		void readById_shouldReturnEntity_whenEntityWithGivenIdExists() {
			Tag tag = Util.createTestTag(null);
			repository.create(tag);

			final Optional<Tag> result = repository.readById(1L);

			assertTrue(result.isPresent());
			assertEquals(1L, result.get().getId());
			assertEquals(tag.getName(), result.get().getName());
		}
	}

	@Nested
	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
		scripts = {"classpath:truncate_db.sql"})
	class TestCreate {

		@Test
		void create_shouldSaveEntityAndReturnEntityWithId1_whenStorageIsEmpty() {
			final Tag tag = Util.createTestTag(null);

			final Tag result = repository.create(tag);

			assertNotNull(result);
			assertEquals(1L, result.getId());
			assertEquals(tag.getName(), result.getName());
			assertEquals(1, repository.readAll().size());
		}

		@Test
		void create_shouldSaveEntityAndReturnEntityWithId3_whenStorageContainsTwoEntities() {
			final List<Tag> storage = List.of(
				Util.createTestTag(null),
				Util.createTestTag(null)
			);
			storage.forEach(repository::create);

			final Tag tag = Util.createTestTag(null);
			final Tag result = repository.create(tag);

			assertNotNull(result);
			assertEquals(3L, result.getId());
			assertEquals(tag.getName(), result.getName());
			assertEquals(3, repository.readAll().size());
		}
	}

	@Nested
	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
		scripts = {"classpath:truncate_db.sql"})
	class TestUpdate {

		@Test
		void update_shouldReturnNull_whenThereIsNoEntityWithGivenId() {
			final List<Tag> storage = List.of(
				Util.createTestTag(null),
				Util.createTestTag(null)
			);
			storage.forEach(repository::create);

			final Tag updated = Util.createTestTag(99L);
			final Tag result = repository.update(updated);

			assertNull(result);
		}

		@Test
		void update_shouldReturnUpdatedEntity_whenEntityIsValid() {
			final List<Tag> storage = List.of(
				Util.createTestTag(null),
				Util.createTestTag(null)
			);
			storage.forEach(repository::create);

			final Tag updated = Util.createTestTag(2L);
			updated.setName("Updated name");
			final Tag result = repository.update(updated);

			assertNotNull(result);
			assertEquals(2L, result.getId());
			assertEquals(updated.getName(), result.getName());
		}
	}

	@Nested
	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
		scripts = {"classpath:truncate_db.sql"})
	class TestDeleteById {

		@Test
		void delete_shouldReturnFalse_whenIdIsNull() {
			final List<Tag> storage = Arrays.asList(
				Util.createTestTag(null),
				Util.createTestTag(null)
			);
			storage.forEach(repository::create);

			assertFalse(repository.deleteById(null));
			assertEquals(2, repository.readAll().size());
		}

		@Test
		void delete_shouldReturnFalse_whenStorageIsEmpty() {
			assertFalse(repository.deleteById(99L));
			assertTrue(repository.readAll().isEmpty());
		}

		@Test
		void delete_shouldReturnFalse_whenThereIsNoEntityWithGivenId() {
			final List<Tag> storage = Arrays.asList(
				Util.createTestTag(null),
				Util.createTestTag(null)
			);
			storage.forEach(repository::create);

			assertFalse(repository.deleteById(99L));
			assertEquals(2, repository.readAll().size());
		}

		@Test
		void delete_shouldReturnTrue_whenEntityWithGivenIdDeleted() {
			final List<Tag> storage = Arrays.asList(
				Util.createTestTag(null),
				Util.createTestTag(null),
				Util.createTestTag(null)
			);
			storage.forEach(repository::create);

			assertTrue(repository.deleteById(2L));
			final List<Tag> result = repository.readAll();
			assertEquals(2, result.size());
			assertEquals(1L, result.get(0).getId());
			assertEquals(3L, result.get(1).getId());
		}

		@Test
		void delete_shouldDeleteEntity_whenItIsSingleEntityInTheStorage() {
			repository.create(Util.createTestTag(null));

			assertTrue(repository.deleteById(1L));
			assertTrue(repository.readAll().isEmpty());
		}
	}

	@Nested
	@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
		scripts = {"classpath:truncate_db.sql"})
	class TestExistById {

		@Test
		void existById_shouldReturnFalse_whenIdIsNull() {
			assertFalse(repository.existById(null));
		}

		@Test
		void existById_shouldReturnFalse_whenStorageIsEmpty() {
			final long id = 99L;
			assertFalse(repository.existById(id));
		}

		@Test
		void existById_shouldReturnFalse_whenThereIsNoEntityWithGivenId() {
			final List<Tag> storage = Arrays.asList(
				Util.createTestTag(null),
				Util.createTestTag(null)
			);
			storage.forEach(repository::create);

			assertFalse(repository.existById(99L));
		}

		@Test
		void existById_shouldReturnTrue_whenEntityWithGivenIdExists() {
			final List<Tag> storage = Arrays.asList(
				Util.createTestTag(null),
				Util.createTestTag(null)
			);
			storage.forEach(repository::create);

			assertTrue(repository.existById(2L));
		}
	}
}
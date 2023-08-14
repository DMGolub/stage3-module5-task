package com.mjc.school.repository.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Author")
@EntityListeners(AuditingEntityListener.class)
public class Author implements BaseEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "author_id")
	private Long id;
	@Column(name = "author_name", nullable = false, unique = true)
	private String name;
	@CreatedDate
	@Column(name = "author_create_date", nullable = false)
	private LocalDateTime createDate;
	@LastModifiedDate
	@Column(name = "author_last_update_date", nullable = false)
	private LocalDateTime lastUpdateDate;
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "author", cascade = CascadeType.REMOVE)
	private List<News> news;

	public Author() {
		// Empty. Used by JPA
	}

	public Author(
		final Long id,
		final String name,
		final LocalDateTime createDate,
		final LocalDateTime lastUpdateDate
	) {
		this.id = id;
		this.name = name;
		this.createDate = createDate;
		this.lastUpdateDate = lastUpdateDate;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(final LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public LocalDateTime getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(final LocalDateTime lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public List<News> getNews() {
		return news;
	}

	public void setNews(final List<News> news) {
		this.news = news;
	}

	@Override
	public String toString() {
		return "Author{id=" + id +
			", name='" + name + '\'' +
			", createDate=" + createDate +
			", lastUpdateDate=" + lastUpdateDate +
			", news=" + news +
			'}';
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final Author that = (Author) o;
		return Objects.equals(id, that.id)
			&& Objects.equals(name, that.name)
			&& Objects.equals(createDate, that.createDate)
			&& Objects.equals(lastUpdateDate, that.lastUpdateDate);
	}

	@Override
	public int hashCode() {
		return 17;
	}
}
package com.mjc.school.repository.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Tag")
public class Tag implements BaseEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tag_id")
	private Long id;
	@Column(name = "tag_name", unique = true)
	private String name;
	@ManyToMany(mappedBy = "tags")
	private List<News> news;

	public Tag() {
		// Empty. Used by JPA
	}

	public Tag(final Long id, final String name) {
		this.id = id;
		this.name = name;
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

	public List<News> getNews() {
		return news;
	}

	public void setNews(final List<News> news) {
		this.news = news;
	}

	@Override
	public String toString() {
		return "Tag{id=" + id +
			", name='" + name + '\'' +
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
		final Tag tag = (Tag) o;
		return Objects.equals(id, tag.id)
			&& Objects.equals(name, tag.name);
	}

	@Override
	public int hashCode() {
		return 31;
	}
}
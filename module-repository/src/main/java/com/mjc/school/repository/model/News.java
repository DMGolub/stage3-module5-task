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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "News")
@EntityListeners(AuditingEntityListener.class)
public class News implements BaseEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "news_id")
	private Long id;
	@Column(name = "news_title", nullable = false, unique = true)
	private String title;
	@Column(name = "news_content", nullable = false)
	private String content;
	@CreatedDate
	@Column(name = "news_create_date", nullable = false)
	private LocalDateTime createDate;
	@LastModifiedDate
	@Column(name = "news_last_update_date", nullable = false)
	private LocalDateTime lastUpdateDate;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "author_id")
	private Author author;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "News_tags",
		joinColumns = @JoinColumn(name = "news_id"),
		inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private List<Tag> tags = new ArrayList<>();
	@OneToMany(mappedBy = "news", cascade = CascadeType.REMOVE)
	private List<Comment> comments = new ArrayList<>();

	public News() {
		// Empty. Used by JPA
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(final Long id) {
		this.id = id;
	}

	public News(
		final Long id,
		final String title,
		final String content,
		final LocalDateTime createDate,
		final LocalDateTime lastUpdateDate,
		final Author author,
		final List<Tag> tags,
		final List<Comment> comments
	) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.createDate = createDate;
		this.lastUpdateDate = lastUpdateDate;
		this.author = author;
		this.tags = tags;
		this.comments = comments;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
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

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(final Author author) {
		this.author = author;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(final List<Tag> tags) {
		this.tags = tags;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(final List<Comment> comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "News{id=" + id +
			", title='" + title + '\'' +
			", content='" + content + '\'' +
			", createDate=" + createDate +
			", lastUpdateDate=" + lastUpdateDate +
			", author=" + author.toString() +
			", tags=" + tags.toString() +
			", comments=" + comments.toString() +
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
		final News news = (News) o;
		return Objects.equals(id, news.id)
			&& Objects.equals(title, news.title)
			&& Objects.equals(content, news.content)
			&& Objects.equals(createDate, news.createDate)
			&& Objects.equals(lastUpdateDate, news.lastUpdateDate)
			&& Objects.equals(author, news.author);
	}

	@Override
	public int hashCode() {
		return 37;
	}
}
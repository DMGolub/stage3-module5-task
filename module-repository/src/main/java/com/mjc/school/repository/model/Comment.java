package com.mjc.school.repository.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "Comment")
@EntityListeners(AuditingEntityListener.class)
public class Comment implements BaseEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "commentId")
	private Long id;
	@Column(name = "comment_content", nullable = false)
	private String content;
	@ManyToOne
	@JoinColumn(name = "news_id")
	private News news;
	@CreatedDate
	@Column(name = "comment_create_date", nullable = false)
	private LocalDateTime createDate;
	@LastModifiedDate
	@Column(name = "comment_last_update_date", nullable = false)
	private LocalDateTime lastUpdateDate;

	public Comment() {
		// Empty. Used by JPA
	}

	public Comment(
		final Long id,
		final String content,
		final News news,
		final LocalDateTime createDate,
		final LocalDateTime lastUpdateDate
	) {
		this.id = id;
		this.content = content;
		this.news = news;
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

	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public News getNews() {
		return news;
	}

	public void setNews(final News news) {
		this.news = news;
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

	@Override
	public String toString() {
		return "Comment{id=" + id +
			", content='" + content + '\'' +
			", newsId=" + news.getId() +
			", createDate=" + createDate +
			", lastUpdateDate=" + lastUpdateDate +
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

		final Comment comment = (Comment) o;
		return Objects.equals(id, comment.id)
			&& Objects.equals(content, comment.content)
			&& Objects.equals(createDate, comment.createDate)
			&& Objects.equals(lastUpdateDate, comment.lastUpdateDate);
	}

	@Override
	public int hashCode() {
		return 37;
	}
}
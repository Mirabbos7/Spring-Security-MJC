package com.mjc.school.model;

import com.mjc.school.interfaces.BaseEntity;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "comment")
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Comment implements BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News newsModel;

    @CreatedDate
    @Column(name = "created")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, fallbackPatterns = {"M/d/yy", "dd.MM.yyyy"})
    private LocalDateTime created;
    @LastModifiedDate
    @Column(name = "modified")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, fallbackPatterns = {"M/d/yy", "dd.MM.yyyy"})
    private LocalDateTime modified;


    public Comment() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getModified() {
        return modified;
    }

    public void setModified(LocalDateTime modified) {
        this.modified = modified;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public News getNewsModel() {
        return newsModel;
    }

    public void setNewsModel(News newsModel) {
        this.newsModel = newsModel;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Comment authorModel = (Comment) obj;
        return id == authorModel.id &&
                (content == authorModel.content || (content != null && content.equals(authorModel.getContent()))) &&
                (created == authorModel.created || (created != null && created.equals(authorModel.getCreated()))) &&
                (modified == authorModel.modified || (modified != null && modified.equals(authorModel.getModified())));
    }

    public int hashCode() {
        return Objects.hash(id, content, created, modified);
    }

    public String toString() {
        return "Comment's ID: " + id + ", content: " + content + ", created: " + created + ", modified: " + modified;
    }
}

package com.mjc.school.repository.model;

import com.mjc.school.repository.interfaces.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "tags")
@EqualsAndHashCode
@ToString
public class TagModel implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(nullable = false, name = "name", unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<NewsModel> news = new ArrayList<>();

    public TagModel() {
    }
    public TagModel(String name) {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<NewsModel> getNews() {
        return news;
    }

    public void setNews(List<NewsModel> news) {
        this.news = news;
    }
}

package com.mjc.school.repository.impl;

import com.mjc.school.model.Author;
import com.mjc.school.model.News;
import com.mjc.school.model.Tag;
import com.mjc.school.repository.AbstractDBRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Optional;

@Repository("newsRepository")
public class NewsRepository extends AbstractDBRepository<News, Long> {
    public List<News> readListOfNewsByParams(List<String> tagName, List<Long> tagId, String authorName, String title, String content) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<News> query = criteriaBuilder.createQuery(News.class);
        Root<News> root = query.from(News.class);
        if (tagName != null && !tagName.isEmpty()) {
            Join<News, Tag> newsJoinTags = root.join("tags");
            Predicate tagN = criteriaBuilder.equal(newsJoinTags.get("name"), tagName);
            query.select(root).where(tagN);
        }
        if (tagId != null && !tagId.isEmpty()) {
            Join<News, Tag> newsJoinTags = root.join("tags");
            Predicate tagI = criteriaBuilder.in(newsJoinTags.get("id")).value(tagId);
            query.select(root).where(tagI);
        }
        if (authorName != null) {
            Join<News, Author> newsJoinAuthor = root.join("authorModel");
            Predicate authorN = criteriaBuilder.equal(newsJoinAuthor.get("name"),  authorName );
            query.select(root).where(authorN);
        }
        if (title != null) {
            Predicate titlePart = criteriaBuilder.like(root.get("title"), "%" + title + "%");
            query.select(root).where(titlePart);
        }
        if (content != null) {
            Predicate contentPart = criteriaBuilder.like(root.get("content"), "%" + content + "%");
            query.select(root).where(contentPart);
        }
        TypedQuery<News> result = entityManager.createQuery(query);
        return result.getResultList();
    }

    @Override
    public void update(News prevState, News nextState) {
        prevState.setTitle(nextState.getTitle());

        prevState.setContent(nextState.getContent());

        Author authorModel = nextState.getAuthorModel();
        prevState.setAuthorModel(nextState.getAuthorModel());

        List<Tag> tagModels = nextState.getTags();
        prevState.setTags(nextState.getTags());

    }

    public Optional<News> readNewsByTitle(String title) {
        TypedQuery<News> typedQuery = entityManager.createQuery("SELECT a FROM News a WHERE a.title LIKE:title", News.class).setParameter("title", title);
        try {
            return Optional.of(typedQuery.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

package com.mjc.school.repository.impl;

import com.mjc.school.model.Author;
import com.mjc.school.model.News;
import com.mjc.school.model.Tag;
import com.mjc.school.repository.AbstractDBRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class NewsRepository extends AbstractDBRepository<News, Long> {

    @PersistenceContext
    private EntityManager entityManager;

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
            Predicate authorN = criteriaBuilder.equal(newsJoinAuthor.get("name"), authorName);
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
        prevState.setAuthorModel(nextState.getAuthorModel());
        prevState.setTags(nextState.getTags());

    }

    public Optional<News> readNewsByTitle(String title) {
        TypedQuery<News> typedQuery = entityManager.createQuery("SELECT a FROM News a WHERE a.title LIKE:title", News.class).setParameter("title", title);
        try {
            return Optional.of(typedQuery.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteById(Long id) {
        if (id != null) {
            News news = entityManager.find(News.class, id);
            if (news != null) {
                try {
                    news.getTags().forEach(tag -> tag.getNews().remove(news));
                    news.getTags().clear();

                    entityManager.remove(news);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public long countNews() {
        return (long) entityManager.createQuery("select count(*) from News").getSingleResult();
    }

    public List<Author> getAuthor() {
        return entityManager.createQuery("select a from Author a", Author.class).getResultList();
    }

    public List<News> getAll() {
        return entityManager.createQuery("select n from News n", News.class).getResultList();
    }
}

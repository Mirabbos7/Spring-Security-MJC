package com.mjc.school.repository.impl;


import com.mjc.school.model.Author;


import com.mjc.school.repository.AbstractDBRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository("authorRepository")
public class AuthorRepository extends AbstractDBRepository<Author, Long> {
    @Override
    public List<Author> readAll(int page, int size, String sortBy) {
        if (sortBy.split(",")[0].equals("newsCount")) {
            String str = "SELECT a FROM Author a JOIN a.newsModelListWithId b GROUP BY a ORDER BY COUNT(b)" + sortBy.split(",")[1];
            return entityManager.createQuery(str).getResultList();
        }
        return super.readAll(page, size, sortBy);
    }

    public void update(Author prevState, Author nextState) {
        if (nextState.getName() != null && !nextState.getName().isBlank()) {
            prevState.setName(nextState.getName());
        }
    }

    public Optional<Author> readAuthorByNewsId(Long newsId) {
        Optional<Author> result = Optional.of(entityManager.createQuery("SELECT a FROM Author a INNER JOIN a.newsModelListWithId b WHERE b.id=:newsId", Author.class).setParameter("newsId", newsId).getSingleResult());
        return result;
    }

    public Optional<Author> readAuthorByName(String name) {
        TypedQuery<Author> typedQuery = entityManager.createQuery("SELECT a FROM Author a WHERE a.name LIKE:name", Author.class).setParameter("name",  name );
        try {
            return Optional.of(typedQuery.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    public Optional<Author> readAuthorByPartName(String name) {
        TypedQuery<Author> typedQuery = entityManager.createQuery("SELECT a FROM Author a WHERE a.name LIKE:name", Author.class).setParameter("name", "%" + name + "%" );
        try {
            return Optional.of(typedQuery.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

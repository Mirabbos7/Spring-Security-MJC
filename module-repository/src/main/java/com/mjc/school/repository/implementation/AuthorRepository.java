package com.mjc.school.repository.implementation;


import com.mjc.school.repository.model.AuthorModel;


import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository("authorRepository")
public class AuthorRepository extends AbstractDBRepository<AuthorModel, Long> {
    @Override
    public List<AuthorModel> readAll(int page, int size, String sortBy) {
        if (sortBy.split(",")[0].equals("newsCount")) {
            String str = "SELECT a FROM AuthorModel a JOIN a.newsModelListWithId b GROUP BY a ORDER BY COUNT(b)" + sortBy.split(",")[1];
            return entityManager.createQuery(str).getResultList();
        }
        return super.readAll(page, size, sortBy);

    }

    @Override
    void update(AuthorModel prevState, AuthorModel nextState) {
        if (nextState.getName() != null && !nextState.getName().isBlank()) {
            prevState.setName(nextState.getName());
        }
    }

    public Optional<AuthorModel> readAuthorByNewsId(Long newsId) {
        Optional<AuthorModel> result = Optional.of(entityManager.createQuery("SELECT a FROM AuthorModel a INNER JOIN a.newsModelListWithId b WHERE b.id=:newsId", AuthorModel.class).setParameter("newsId", newsId).getSingleResult());
        return result;
    }

    public Optional<AuthorModel> readAuthorByName(String name) {
        TypedQuery<AuthorModel> typedQuery = entityManager.createQuery("SELECT a FROM AuthorModel a WHERE a.name LIKE:name", AuthorModel.class).setParameter("name",  name );
        try {
            return Optional.of(typedQuery.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    public Optional<AuthorModel> readAuthorByPartName(String name) {
        TypedQuery<AuthorModel> typedQuery = entityManager.createQuery("SELECT a FROM AuthorModel a WHERE a.name LIKE:name", AuthorModel.class).setParameter("name", "%" + name + "%" );
        try {
            return Optional.of(typedQuery.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

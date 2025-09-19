package com.mjc.school.repository.implementation;

import com.mjc.school.repository.model.TagModel;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository("tagRepository")
public class TagRepository extends AbstractDBRepository<TagModel, Long> {


    public List<TagModel> readListOfTagsByNewsId(Long newsId) {
        List<TagModel> result = entityManager.createQuery("SELECT a FROM TagModel a INNER JOIN a.news b WHERE b.id=:newsId", TagModel.class).setParameter("newsId", newsId).getResultList();
        return result;
    }

    public Optional<TagModel> readTagByName(String name) {
        TypedQuery<TagModel> result = entityManager.createQuery("SELECT a FROM TagModel a WHERE a.name LIKE:name", TagModel.class).setParameter("name", "%" + name + "%");
        try {
            return Optional.of(result.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    void update(TagModel prevState, TagModel nextState) {
        if (nextState.getName() != null && !nextState.getName().isBlank()) {
            prevState.setName(nextState.getName());
        }
    }
}


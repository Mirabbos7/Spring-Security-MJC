package com.mjc.school.repository.impl;

import com.mjc.school.model.Tag;
import com.mjc.school.repository.AbstractDBRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository("tagRepository")
public class TagRepository extends AbstractDBRepository<Tag, Long> {


    public List<Tag> readListOfTagsByNewsId(Long newsId) {
        List<Tag> result = entityManager.createQuery("SELECT a FROM Tag a INNER JOIN a.news b WHERE b.id=:newsId", Tag.class).setParameter("newsId", newsId).getResultList();
        return result;
    }

    public Optional<Tag> readTagByName(String name) {
        TypedQuery<Tag> result = entityManager.createQuery("SELECT a FROM Tag a WHERE a.name LIKE:name", Tag.class).setParameter("name", "%" + name + "%");
        try {
            return Optional.of(result.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void update(Tag prevState, Tag nextState) {
        if (nextState.getName() != null && !nextState.getName().isBlank()) {
            prevState.setName(nextState.getName());
        }
    }
}


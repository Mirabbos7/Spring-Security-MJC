package com.mjc.school.repository.impl;

import com.mjc.school.model.Comment;
import com.mjc.school.repository.AbstractDBRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepository extends AbstractDBRepository<Comment, Long> {

    public List<Comment> readListOfCommentsByNewsId(Long newsId) {
        return entityManager.createQuery("SELECT a FROM Comment a INNER JOIN a.newsModel b WHERE b.id=:newsId", Comment.class).setParameter("newsId", newsId).getResultList();
    }

    @Override
    public void update(Comment prevState, Comment nextState) {
        if (nextState.getContent() != null && !nextState.getContent().isBlank()) {
            prevState.setContent(nextState.getContent());
        }

    }
}

package com.mjc.school.implementation;

import com.mjc.school.model.Comment;
import com.mjc.school.repository.AbstractDBRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("commentRepository")
public class CommentRepository extends AbstractDBRepository<Comment, Long> {

    public List<Comment> readListOfCommentsByNewsId(Long newsId) {
        List<Comment> result = entityManager.createQuery("SELECT a FROM Comment a INNER JOIN a.newsModel b WHERE b.id=:newsId", Comment.class).setParameter("newsId", newsId).getResultList();
        return result;
    }

    @Override
    public void update(Comment prevState, Comment nextState) {
        if (nextState.getContent() != null && !nextState.getContent().isBlank()) {
            prevState.setContent(nextState.getContent());
        }

    }
}

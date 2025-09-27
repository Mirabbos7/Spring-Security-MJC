package com.mjc.school.service.impl;


import com.mjc.school.repository.impl.CommentRepository;

import com.mjc.school.repository.impl.NewsRepository;
import com.mjc.school.model.Comment;

import com.mjc.school.model.News;
import com.mjc.school.dto.CommentDtoRequest;
import com.mjc.school.dto.CommentDtoResponse;
import com.mjc.school.exceptions.ElementNotFoundException;
import com.mjc.school.exceptions.ValidatorException;
import com.mjc.school.mapper.CommentMapper;
import com.mjc.school.service.CommentServiceInterface;
import com.mjc.school.validation.CustomValidator;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

import static com.mjc.school.exceptions.ErrorCodes.INVALID_VALUE_OF_SORTING;
import static com.mjc.school.exceptions.ErrorCodes.NO_COMMENTS_FOR_NEWS_ID;
import static com.mjc.school.exceptions.ErrorCodes.NO_COMMENT_WITH_PROVIDED_ID;

@Service("commentService")
@Transactional
public class CommentService implements CommentServiceInterface {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final NewsRepository newsRepository;
    private CustomValidator customValidator;

    public CommentService(CommentRepository commentRepository, CommentMapper commentMapper, NewsRepository newsRepository, CustomValidator customValidator) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.newsRepository = newsRepository;
        this.customValidator = customValidator;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDtoResponse> readAll(int page, int size, String sortBy) {
        try {
            return commentMapper.listModelToDtoList(commentRepository.readAll(page, size, sortBy));
        } catch (InvalidDataAccessApiUsageException e) {
            throw new ValidatorException(String.format(INVALID_VALUE_OF_SORTING.getErrorMessage()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDtoResponse readById(Long id) {
        Optional<Comment> opt = commentRepository.readById(id);
        return opt.map(commentMapper::ModelCommentToDto).orElseThrow(() -> new ElementNotFoundException(String.format(NO_COMMENT_WITH_PROVIDED_ID.getErrorMessage(), id)));

    }

    @Override
    @Transactional
    public CommentDtoResponse create(CommentDtoRequest createRequest) {
        customValidator.validateComment(createRequest);
        Comment commentModel = commentMapper.DtoCommentToModel(createRequest);
        News newsModel = newsRepository.readById(createRequest.newsId()).get();
        commentModel.setNewsModel(newsModel);
        Comment createCommentModel = commentRepository.create(commentModel);
        newsModel.addComment(createCommentModel);
        return commentMapper.ModelCommentToDto(createCommentModel);
    }

    @Override
    @Transactional
    public CommentDtoResponse update(Long id, CommentDtoRequest updateRequest) {
        if (commentRepository.existById(id)) {
            customValidator.validateComment(updateRequest);
            Comment commentModel = commentMapper.DtoCommentToModel(updateRequest);
            commentModel.setId(id);
            return commentMapper.ModelCommentToDto(commentRepository.update(commentModel));
        } else {
            throw new ElementNotFoundException(String.format(NO_COMMENT_WITH_PROVIDED_ID.getErrorMessage(), id));
        }
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (commentRepository.existById(id)) {
            return commentRepository.deleteById(id);
        } else {
            throw new ElementNotFoundException(String.format(NO_COMMENT_WITH_PROVIDED_ID.getErrorMessage(), id));
        }
    }

    @Override
    public List<CommentDtoResponse> readListOfCommentsByNewsId(Long newsId) {
        if (newsId != null && newsId >= 0) {
            return commentMapper.listModelToDtoList(commentRepository.readListOfCommentsByNewsId(newsId));
        } else {
            throw new ElementNotFoundException(String.format(NO_COMMENTS_FOR_NEWS_ID.getErrorMessage(), newsId));


        }
    }
}


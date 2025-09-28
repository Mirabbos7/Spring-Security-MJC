package com.mjc.school.service.impl;
import com.mjc.school.dto.CommentDtoRequest;
import com.mjc.school.dto.CommentDtoResponse;
import com.mjc.school.exception.ElementNotFoundException;
import com.mjc.school.exception.ValidatorException;
import com.mjc.school.mapper.CommentMapper;
import com.mjc.school.model.Author;
import com.mjc.school.model.Comment;
import com.mjc.school.model.News;
import com.mjc.school.repository.impl.CommentRepository;
import com.mjc.school.repository.impl.NewsRepository;
import com.mjc.school.validation.CustomValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.mjc.school.exception.ErrorCodes.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private CustomValidator customValidator;

    private Comment comment;
    private News news;
    private Author author;
    private CommentDtoRequest request;
    private CommentDtoResponse response;

    @BeforeEach
    void setUp() {
        author = new Author(1L, "John Doe", LocalDateTime.now(), LocalDateTime.now());
        news = new News(1L, "TITLE1", "CONTENT1", author);
        comment = new Comment(1L, "content", news, LocalDateTime.now(), LocalDateTime.now());
        request = new CommentDtoRequest("CONTENT", 1L);
        response = new CommentDtoResponse(1L, "CONTENT", "2025-09-27T10:00:00", "2025-09-27T10:00:00", 1L);
    }

    @Test
    void readAll_ShouldThrow_WhenInvalidSorting() {
        when(commentRepository.readAll(anyInt(), anyInt(), anyString()))
                .thenThrow(new InvalidDataAccessApiUsageException("bad sort"));

        assertThatThrownBy(() -> commentService.readAll(0, 10, "invalid"))
                .isInstanceOf(ValidatorException.class)
                .hasMessage(INVALID_VALUE_OF_SORTING.getErrorMessage());
    }

    @Test
    void readById_ShouldReturnMappedComment() {
        when(commentRepository.readById(1L)).thenReturn(Optional.of(comment));
        when(commentMapper.ModelCommentToDto(comment)).thenReturn(response);

        CommentDtoResponse result = commentService.readById(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void readById_ShouldThrow_WhenNotFound() {
        when(commentRepository.readById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.readById(1L))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessage(String.format(NO_COMMENT_WITH_PROVIDED_ID.getErrorMessage(), 1L));
    }

    @Test
    void create_ShouldReturnSavedComment() {
        when(commentMapper.DtoCommentToModel(request)).thenReturn(comment);
        when(newsRepository.readById(1L)).thenReturn(Optional.of(news));
        when(commentRepository.create(any(Comment.class))).thenReturn(comment);
        when(commentMapper.ModelCommentToDto(comment)).thenReturn(response);

        CommentDtoResponse result = commentService.create(request);

        assertThat(result).isEqualTo(response);
        verify(customValidator).validateComment(request);
    }

    @Test
    void update_ShouldReturnUpdatedComment() {
        when(commentRepository.existById(1L)).thenReturn(true);
        when(commentMapper.DtoCommentToModel(request)).thenReturn(comment);
        when(commentRepository.update(comment)).thenReturn(comment);
        when(commentMapper.ModelCommentToDto(comment)).thenReturn(response);

        CommentDtoResponse result = commentService.update(1L, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void update_ShouldThrow_WhenNotExists() {
        when(commentRepository.existById(1L)).thenReturn(false);

        assertThatThrownBy(() -> commentService.update(1L, request))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessage(String.format(NO_COMMENT_WITH_PROVIDED_ID.getErrorMessage(), 1L));
    }

    @Test
    void deleteById_ShouldReturnTrue_WhenExists() {
        when(commentRepository.existById(1L)).thenReturn(true);
        when(commentRepository.deleteById(1L)).thenReturn(true);

        boolean result = commentService.deleteById(1L);

        assertThat(result).isTrue();
    }

    @Test
    void deleteById_ShouldThrow_WhenNotExists() {
        when(commentRepository.existById(1L)).thenReturn(false);

        assertThatThrownBy(() -> commentService.deleteById(1L))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessage(String.format(NO_COMMENT_WITH_PROVIDED_ID.getErrorMessage(), 1L));
    }

    @Test
    void readListOfCommentsByNewsId_ShouldReturnList() {
        when(commentRepository.readListOfCommentsByNewsId(1L)).thenReturn(List.of(comment));
        when(commentMapper.listModelToDtoList(any())).thenReturn(List.of(response));

        List<CommentDtoResponse> result = commentService.readListOfCommentsByNewsId(1L);

        assertThat(result).containsExactly(response);
    }

    @Test
    void readListOfCommentsByNewsId_ShouldThrow_WhenInvalidNewsId() {
        assertThatThrownBy(() -> commentService.readListOfCommentsByNewsId(-1L))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessage(String.format(NO_COMMENTS_FOR_NEWS_ID.getErrorMessage(), -1L));
    }
}
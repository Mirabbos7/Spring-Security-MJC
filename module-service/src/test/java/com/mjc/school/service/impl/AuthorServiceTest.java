package com.mjc.school.service.impl;

import com.mjc.school.dto.AuthorDtoRequest;
import com.mjc.school.dto.AuthorDtoResponse;
import com.mjc.school.exception.ElementNotFoundException;
import com.mjc.school.exception.ValidatorException;
import com.mjc.school.mapper.AuthorMapper;
import com.mjc.school.model.Author;
import com.mjc.school.repository.impl.AuthorRepository;
import com.mjc.school.validation.CustomValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.mjc.school.exception.ErrorCodes.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @Mock
    private CustomValidator customValidator;

    @InjectMocks
    private AuthorService authorService;

    private Author author;
    private AuthorDtoRequest request;
    private AuthorDtoResponse response;

    @BeforeEach
    void setUp() {
        author = new Author(1L, "John Doe", LocalDateTime.now(), LocalDateTime.now());
        request = new AuthorDtoRequest("John Doe");
        response = new AuthorDtoResponse(
                1L,
                "John Doe",
                "2025-09-27T10:00:00",
                "2025-09-27T10:00:00"
        );
    }

    @Test
    void readAll_ShouldThrowValidatorException_WhenInvalidSort() {
        when(authorRepository.readAll(0, 10, "bad")).thenThrow(InvalidDataAccessApiUsageException.class);

        assertThatThrownBy(() -> authorService.readAll(0, 10, "bad"))
                .isInstanceOf(ValidatorException.class)
                .hasMessageContaining(INVALID_VALUE_OF_SORTING.getErrorMessage());
    }

    @Test
    void readById_ShouldReturnResponse_WhenFound() {
        when(authorRepository.readById(1L)).thenReturn(Optional.of(author));
        when(authorMapper.ModelAuthorToDTO(author)).thenReturn(response);

        AuthorDtoResponse result = authorService.readById(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void readById_ShouldThrow_WhenNotFound() {
        long id = 1L;
        when(authorRepository.readById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.readById(id))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessage(String.format(NO_AUTHOR_WITH_PROVIDED_ID.getErrorMessage(), id));
    }

    @Test
    void create_ShouldReturnSavedAuthor_WhenValidAndUnique() {
        when(authorRepository.readAuthorByName("John Doe")).thenReturn(Optional.empty());
        when(authorMapper.DtoAuthorToModel(request)).thenReturn(author);
        when(authorRepository.create(author)).thenReturn(author);
        when(authorMapper.ModelAuthorToDTO(author)).thenReturn(response);

        AuthorDtoResponse result = authorService.create(request);

        verify(customValidator).validateAuthor(request);
        assertThat(result).isEqualTo(response);
    }

    @Test
    void create_ShouldThrow_WhenAuthorAlreadyExists() {
        when(authorRepository.readAuthorByName("John Doe")).thenReturn(Optional.of(author));

        assertThatThrownBy(() -> authorService.create(request))
                .isInstanceOf(ValidatorException.class)
                .hasMessageContaining(NOT_UNIQUE_AUTHOR_NAME.getErrorMessage());
    }

    @Test
    void update_ShouldReturnUpdatedAuthor_WhenValidAndUnique() {
        when(authorRepository.existById(1L)).thenReturn(true);
        when(authorRepository.readAuthorByName("John Doe")).thenReturn(Optional.empty());
        when(authorMapper.DtoAuthorToModel(request)).thenReturn(author);
        when(authorRepository.update(any(Author.class))).thenReturn(author);
        when(authorMapper.ModelAuthorToDTO(author)).thenReturn(response);

        AuthorDtoResponse result = authorService.update(1L, request);

        verify(customValidator).validateAuthor(request);
        assertThat(result).isEqualTo(response);
        assertThat(author.getId()).isEqualTo(1L);
    }

    @Test
    void update_ShouldThrow_WhenAuthorDoesNotExist() {
        long id = 1L;
        when(authorRepository.existById(id)).thenReturn(false);

        assertThatThrownBy(() -> authorService.update(id, request))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessage(String.format(NO_AUTHOR_WITH_PROVIDED_ID.getErrorMessage(), id));
    }

    @Test
    void update_ShouldThrow_WhenNameNotUnique() {
        when(authorRepository.existById(1L)).thenReturn(true);
        when(authorRepository.readAuthorByName("John Doe")).thenReturn(Optional.of(author));

        assertThatThrownBy(() -> authorService.update(1L, request))
                .isInstanceOf(ValidatorException.class)
                .hasMessageContaining(NOT_UNIQUE_AUTHOR_NAME.getErrorMessage());
    }

    @Test
    void deleteById_ShouldReturnTrue_WhenExists() {
        when(authorRepository.existById(1L)).thenReturn(true);
        when(authorRepository.deleteById(1L)).thenReturn(true);

        boolean result = authorService.deleteById(1L);

        assertThat(result).isTrue();
    }

    @Test
    void deleteById_ShouldThrow_WhenNotExists() {
        // given
        long id = 1L;

        // when
        Throwable thrown = catchThrowable(() -> authorService.deleteById(id));

        // then
        assertThat(thrown)
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessage(String.format(
                        "errorMessage: Author with this id: %d does not exist.,  errorCode: 40402",
                        id
                ));    }

    @Test
    void readAuthorByNewsId_ShouldReturnAuthor_WhenFound() {
        when(authorRepository.readAuthorByNewsId(99L)).thenReturn(Optional.of(author));
        when(authorMapper.ModelAuthorToDTO(author)).thenReturn(response);

        AuthorDtoResponse result = authorService.readAuthorByNewsId(99L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void readAuthorByNewsId_ShouldThrow_WhenNotFound() {
        long newsId = 99L;
        when(authorRepository.readAuthorByNewsId(newsId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.readAuthorByNewsId(newsId))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessage(String.format(NO_AUTHOR_FOR_NEWS_ID.getErrorMessage(), newsId));
    }

}
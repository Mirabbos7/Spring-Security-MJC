package com.mjc.school.service.impl;

import com.mjc.school.dto.AuthorDtoResponse;
import com.mjc.school.dto.CommentDtoResponse;
import com.mjc.school.dto.NewsDtoRequest;
import com.mjc.school.dto.NewsDtoResponse;
import com.mjc.school.dto.NewsPageDtoResponse;
import com.mjc.school.dto.TagDtoResponse;
import com.mjc.school.exception.ElementNotFoundException;
import com.mjc.school.exception.ValidatorException;
import com.mjc.school.mapper.NewsMapper;
import com.mjc.school.model.Author;
import com.mjc.school.model.News;
import com.mjc.school.model.Tag;
import com.mjc.school.repository.impl.AuthorRepository;
import com.mjc.school.repository.impl.NewsRepository;
import com.mjc.school.repository.impl.TagRepository;
import com.mjc.school.validation.CustomValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.List;
import java.util.Optional;

import static com.mjc.school.exception.ErrorCodes.NO_NEWS_WITH_PROVIDED_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {

    @Mock
    private NewsRepository newsRepository;
    @Mock
    private NewsMapper newsMapper;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private CustomValidator customValidator;

    @InjectMocks
    private NewsServiceImpl newsService;

    private NewsDtoRequest request;
    private News news;
    private NewsDtoResponse response;

    @BeforeEach
    void setUp() {
        request = new NewsDtoRequest("Title", "Content", "Author", List.of("tag1"));
        news = new News();
        news.setId(1L);
        news.setTitle("Title");
        response = new NewsDtoResponse(1L, "Title", "Content", "2025-09-27T12:00:00", "2025-09-27T12:00:00", null, List.of(), List.of());
    }

    @Test
    void readAll_ShouldReturnPage() {
        when(newsRepository.readAll(0, 10, "id")).thenReturn(List.of(news));
        when(newsMapper.ModelListToDtoList(anyList())).thenReturn(List.of(response));

        NewsPageDtoResponse result = newsService.readAll(0, 10, "id");

        assertThat(result.getNewsList()).hasSize(1);
        assertThat(result.getTotalNewsCount()).isEqualTo(1);
    }

    @Test
    void readAll_ShouldThrow_WhenInvalidSort() {
        when(newsRepository.readAll(anyInt(), anyInt(), anyString()))
                .thenThrow(new InvalidDataAccessApiUsageException("bad sort"));

        assertThatThrownBy(() -> newsService.readAll(0, 10, "bad"))
                .isInstanceOf(ValidatorException.class);
    }

    @Test
    void readById_ShouldReturnNews() {
        when(newsRepository.readById(1L)).thenReturn(Optional.of(news));
        when(newsMapper.ModelNewsToDTO(news)).thenReturn(response);

        NewsDtoResponse result = newsService.readById(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void readById_ShouldThrow_WhenNotFound() {
        when(newsRepository.readById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> newsService.readById(1L))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining(
                        String.format(NO_NEWS_WITH_PROVIDED_ID.getErrorMessage(), 1L)
                );
    }

    @Test
    void create_ShouldReturnNews() {
        when(newsRepository.readNewsByTitle("Title")).thenReturn(Optional.empty());
        when(newsMapper.DTONewsToModel(request)).thenReturn(news);
        when(newsRepository.create(news)).thenReturn(news);
        when(newsMapper.ModelNewsToDTO(news)).thenReturn(response);

        NewsDtoResponse result = newsService.create(request);

        assertThat(result).isEqualTo(response);
        verify(authorRepository).create(any(Author.class));
        verify(tagRepository).create(any(Tag.class));
    }

    @Test
    void create_ShouldThrow_WhenEmptyAuthor() {
        NewsDtoRequest badRequest = new NewsDtoRequest("t", "c", " ", List.of("tag"));

        assertThatThrownBy(() -> newsService.create(badRequest))
                .isInstanceOf(ValidatorException.class)
                .hasMessageContaining("Author name cannot be empty");
    }

    @Test
    void create_ShouldThrow_WhenEmptyTags() {
        NewsDtoRequest badRequest = new NewsDtoRequest("t", "c", "Author", List.of());

        assertThatThrownBy(() -> newsService.create(badRequest))
                .isInstanceOf(ValidatorException.class)
                .hasMessageContaining("Please specify tag names");
    }

    @Test
    void create_ShouldThrow_WhenDuplicateTitle() {
        when(newsRepository.readNewsByTitle("Title")).thenReturn(Optional.of(news));

        assertThatThrownBy(() -> newsService.create(request))
                .isInstanceOf(ValidatorException.class)
                .hasMessageContaining("Title of news must be unique");
    }

    @Test
    void update_ShouldReturnNews() {
        when(newsRepository.existById(1L)).thenReturn(true);
        when(newsRepository.readNewsByTitle("Title")).thenReturn(Optional.empty());
        when(newsMapper.DTONewsToModel(request)).thenReturn(news);
        when(newsRepository.update(any(News.class))).thenReturn(news);
        when(newsMapper.ModelNewsToDTO(news)).thenReturn(response);

        NewsDtoResponse result = newsService.update(1L, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        when(newsRepository.existById(1L)).thenReturn(false);

        assertThatThrownBy(() -> newsService.update(1L, request))
                .isInstanceOf(ElementNotFoundException.class);
    }

    @Test
    void deleteById_ShouldReturnTrue_WhenExists() {
        when(newsRepository.existById(1L)).thenReturn(true);
        when(newsRepository.deleteById(1L)).thenReturn(true);

        boolean result = newsService.deleteById(1L);

        assertThat(result).isTrue();
    }

    @Test
    void deleteById_ShouldThrow_WhenNotFound() {
        when(newsRepository.existById(1L)).thenReturn(false);

        assertThatThrownBy(() -> newsService.deleteById(1L))
                .isInstanceOf(ElementNotFoundException.class);
    }

    @Test
    void update_ShouldThrowValidatorException_WhenTitleAlreadyExists() {
        Long id = 1L;

        when(newsRepository.existById(id)).thenReturn(true);
        when(newsRepository.readNewsByTitle(request.title()))
                .thenReturn(Optional.of(new News()));

        assertThrows(ValidatorException.class,
                () -> newsService.update(id, request));

        verify(customValidator).validateNews(request);
        verify(newsMapper, never()).DTONewsToModel(any());
        verify(newsRepository, never()).update(any());
    }

    @Test
    void readListOfNewsByParams_ShouldReturnMappedDtos() {
        List<News> newsModels = List.of(new News());

        AuthorDtoResponse authorDto = new AuthorDtoResponse(1L, "Author Name", "", "");
        List<TagDtoResponse> tags = List.of(new TagDtoResponse(1L, "tag1"));
        List<CommentDtoResponse> comments = List.of(new CommentDtoResponse(1L, "Nice article", "", "", 1L));

        List<NewsDtoResponse> dtoResponses = List.of(
                new NewsDtoResponse(
                        1L,
                        "Some title",
                        "Some content",
                        "2025-09-28",
                        "2025-09-28",
                        authorDto,
                        tags,
                        comments
                )
        );

        when(newsRepository.readListOfNewsByParams(
                anyList(), anyList(), anyString(), anyString(), anyString()))
                .thenReturn(newsModels);

        when(newsMapper.ModelListToDtoList(newsModels)).thenReturn(dtoResponses);

        List<NewsDtoResponse> result = newsService.readListOfNewsByParams(
                List.of("tag1"), List.of(1L), "Author Name", "Some title", "Some content");

        assertEquals(1, result.size());
        assertEquals("Some title", result.get(0).title());
        assertEquals("Author Name", result.get(0).authorDtoResponse().name());
        assertEquals("tag1", result.get(0).tagList().get(0).name());
        assertEquals("Nice article", result.get(0).commentList().get(0).content());

        verify(newsRepository).readListOfNewsByParams(anyList(), anyList(), anyString(), anyString(), anyString());
        verify(newsMapper).ModelListToDtoList(newsModels);
    }
    @Test
    void createNotExistTags_ShouldCreateTags_WhenValidAndNotExists() {
        List<String> tagNames = List.of("validTag");

        when(tagRepository.readTagByName("validTag")).thenReturn(Optional.empty());

        newsService.createNotExistTags(tagNames);

        verify(tagRepository).create(any(Tag.class));
    }

    @Test
    void createNotExistTags_ShouldThrow_WhenTagTooShort() {
        List<String> tagNames = List.of("ab");

        assertThrows(ValidatorException.class,
                () -> newsService.createNotExistTags(tagNames));

        verify(tagRepository, never()).create(any());
    }

    @Test
    void createNotExistTags_ShouldThrow_WhenTagTooLong() {
        List<String> tagNames = List.of("a".repeat(20));

        assertThrows(ValidatorException.class,
                () -> newsService.createNotExistTags(tagNames));

        verify(tagRepository, never()).create(any());
    }

    @Test
    void createNotExistTags_ShouldNotCreate_WhenTagAlreadyExists() {
        List<String> tagNames = List.of("validTag");

        when(tagRepository.readTagByName("validTag")).thenReturn(Optional.of(new Tag()));

        newsService.createNotExistTags(tagNames);

        verify(tagRepository, never()).create(any());
    }

    @Test
    void createNotExistAuthor_ShouldCreateAuthor_WhenValidAndNotExists() {
        String authorName = "ValidAuthor";

        when(authorRepository.readAuthorByName(authorName)).thenReturn(Optional.empty());

        newsService.createNotExistAuthor(authorName);

        verify(authorRepository).create(any(Author.class));
    }

    @Test
    void createNotExistAuthor_ShouldThrow_WhenNameTooShort() {
        String authorName = "aa";

        assertThrows(ValidatorException.class,
                () -> newsService.createNotExistAuthor(authorName));

        verify(authorRepository, never()).create(any());
    }

    @Test
    void createNotExistAuthor_ShouldThrow_WhenNameTooLong() {
        String authorName = "a".repeat(20);

        assertThrows(ValidatorException.class,
                () -> newsService.createNotExistAuthor(authorName));

        verify(authorRepository, never()).create(any());
    }

    @Test
    void createNotExistAuthor_ShouldNotCreate_WhenAuthorAlreadyExists() {
        String authorName = "ValidAuthor";

        when(authorRepository.readAuthorByName(authorName)).thenReturn(Optional.of(new Author()));

        newsService.createNotExistAuthor(authorName);

        verify(authorRepository, never()).create(any());
    }


}

package com.mjc.school.implementation;

import com.mjc.school.dto.*;
import com.mjc.school.service.AuthorServiceInterface;
import com.mjc.school.service.CommentServiceInterface;
import com.mjc.school.service.NewsServiceInterface;
import com.mjc.school.service.TagServiceInterface;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsServiceInterface<NewsDtoRequest, NewsDtoResponse, Long> newsService;

    @MockBean
    private AuthorServiceInterface authorService;

    @MockBean
    private TagServiceInterface tagService;

    @MockBean
    private CommentServiceInterface commentService;

    @Test
    @WithMockUser(roles = "USER")
    void readAll_shouldReturnNewsPage() throws Exception {
        NewsDtoResponse news = new NewsDtoResponse(
                1L, "Title", "Content", "2025-01-01", "2025-01-02",
                new AuthorDtoResponse(1L, "Author", "", ""),
                Collections.emptyList(),
                Collections.emptyList()
        );
        NewsPageDtoResponse response = new NewsPageDtoResponse(List.of(news), 1);

        Mockito.when(newsService.readAll(0, 5, "createDate,dsc")).thenReturn(response);

        mockMvc.perform(get("/api/v1/news")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "createDate,dsc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newsList[0].id", is(1)))
                .andExpect(jsonPath("$.totalNewsCount", is(1)));

        verify(newsService).readAll(0, 5, "createDate,dsc");
    }

    @Test
    @WithMockUser
    void readById_shouldReturnNews() throws Exception {
        NewsDtoResponse response = new NewsDtoResponse(
                1L, "Title", "Content", "2025-01-01", "2025-01-02",
                new AuthorDtoResponse(1L, "Author", "", ""),
                Collections.emptyList(),
                Collections.emptyList()
        );
        Mockito.when(newsService.readById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/news/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Title")));

        verify(newsService).readById(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void create_shouldReturnCreatedNews() throws Exception {
        NewsDtoResponse response = new NewsDtoResponse(
                2L, "New Title", "New Content", "2025-01-01", "2025-01-02",
                new AuthorDtoResponse(1L, "Author", "", ""),
                Collections.emptyList(),
                Collections.emptyList()
        );
        Mockito.when(newsService.create(any(NewsDtoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Title\",\"content\":\"New Content\",\"authorId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.title", is("New Title")));

        verify(newsService).create(any(NewsDtoRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnUpdatedNews() throws Exception {
        NewsDtoResponse response = new NewsDtoResponse(
                1L, "Updated Title", "Updated Content", "2025-01-01", "2025-01-02",
                new AuthorDtoResponse(1L, "Author", "", ""),
                Collections.emptyList(),
                Collections.emptyList()
        );
        Mockito.when(newsService.update(eq(1L), any(NewsDtoRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/news/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Title\",\"content\":\"Updated Content\",\"authorId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Title")));

        verify(newsService).update(eq(1L), any(NewsDtoRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/news/{id}", 1))
                .andExpect(status().isNoContent());

        verify(newsService).deleteById(1L);
    }

    @Test
    @WithMockUser
    void search_shouldReturnNewsList() throws Exception {
        NewsDtoResponse news = new NewsDtoResponse(
                3L, "Search Title", "Search Content", "2025-01-01", "2025-01-02",
                new AuthorDtoResponse(1L, "Author", "", ""),
                Collections.emptyList(),
                Collections.emptyList()
        );
        Mockito.when(newsService.readListOfNewsByParams(List.of("tag"), List.of(1L), "author", "Search Title", "Search Content"))
                .thenReturn(List.of(news));

        mockMvc.perform(get("/api/v1/news/search")
                        .param("tag_name", "tag")
                        .param("tag_id", "1")
                        .param("author_name", "author")
                        .param("title", "Search Title")
                        .param("content", "Search Content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Search Title")));

        verify(newsService).readListOfNewsByParams(List.of("tag"), List.of(1L), "author", "Search Title", "Search Content");
    }

    @Test
    @WithMockUser
    void readTagsByNewsId_shouldReturnTags() throws Exception {
        TagDtoResponse tag = new TagDtoResponse(1L, "Tag1");
        Mockito.when(tagService.readListOfTagsByNewsId(1L)).thenReturn(List.of(tag));

        mockMvc.perform(get("/api/v1/news/{id}/tag", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Tag1")));

        verify(tagService).readListOfTagsByNewsId(1L);
    }

    @Test
    @WithMockUser
    void readAuthorByNewsId_shouldReturnAuthor() throws Exception {
        AuthorDtoResponse author = new AuthorDtoResponse(1L, "Author Name", "", "");
        Mockito.when(authorService.readAuthorByNewsId(1L)).thenReturn(author);

        mockMvc.perform(get("/api/v1/news/{id}/author", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Author Name")));

        verify(authorService).readAuthorByNewsId(1L);
    }

    @Test
    @WithMockUser
    void readCommentsByNewsId_shouldReturnComments() throws Exception {
        CommentDtoResponse comment = new CommentDtoResponse(1L, "Comment Content", "2025-01-01", "2025-01-02", 1L);
        Mockito.when(commentService.readListOfCommentsByNewsId(1L)).thenReturn(List.of(comment));

        mockMvc.perform(get("/api/v1/news/{id}/comment", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].content", is("Comment Content")));

        verify(commentService).readListOfCommentsByNewsId(1L);
    }
}

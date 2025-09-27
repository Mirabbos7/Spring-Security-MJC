package com.mjc.school.implementation;

import com.mjc.school.dto.CommentDtoRequest;
import com.mjc.school.dto.CommentDtoResponse;
import com.mjc.school.service.CommentServiceInterface;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentServiceInterface commentService;

    @Test
    @WithMockUser(roles = "USER")
    void readAll_shouldReturnListOfComments() throws Exception {
        CommentDtoResponse response =
                new CommentDtoResponse(1L, "First Comment", "", "", 1L);
        Mockito.when(commentService.readAll(0, 5, "created,dsc"))
                .thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/v1/comment")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "created,dsc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].content", is("First Comment")));

        verify(commentService).readAll(0, 5, "created,dsc");
    }

    @Test
    @WithMockUser
    void readById_shouldReturnComment() throws Exception {
        CommentDtoResponse response =
                new CommentDtoResponse(1L, "Test Comment", "", "", 1L);

        Mockito.when(commentService.readById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/comment/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test Comment"));

        verify(commentService).readById(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void create_shouldReturnCreatedComment() throws Exception {
        CommentDtoResponse response =
                new CommentDtoResponse(2L, "New Comment", "", "", 1L);

        Mockito.when(commentService.create(any(CommentDtoRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"New Comment\",\"newsId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.content").value("New Comment"));

        verify(commentService).create(any(CommentDtoRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnUpdatedComment() throws Exception {
        CommentDtoResponse response =
                new CommentDtoResponse(1L, "Updated Comment", "", "", 1L);

        Mockito.when(commentService.update(eq(1L), any(CommentDtoRequest.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/v1/comment/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Updated Comment\",\"newsId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Updated Comment")));

        verify(commentService).update(eq(1L), any(CommentDtoRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/comment/{id}", 1))
                .andExpect(status().isNoContent());

        verify(commentService).deleteById(1L);
    }
}

package com.mjc.school.implementation;

import com.mjc.school.dto.AuthorDtoRequest;
import com.mjc.school.dto.AuthorDtoResponse;
import com.mjc.school.service.AuthorServiceInterface;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorServiceInterface authorService;

    @Test
    @WithMockUser(roles = "USER")
    void readAll_shouldReturnListOfAuthors() throws Exception {
        AuthorDtoResponse response = new AuthorDtoResponse(1L, "Test Author", "", "");
        Mockito.when(authorService.readAll(0, 5, "name,dsc"))
                .thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/v1/author/readAll")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "name,dsc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Author")));

        verify(authorService).readAll(0, 5, "name,dsc");
    }

    @Test
    @WithMockUser
    void readById_shouldReturnAuthor() throws Exception {
        AuthorDtoResponse response = new AuthorDtoResponse(1L, "Author One", "", "");

        Mockito.when(authorService.readById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/author/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Author One"));

        verify(authorService).readById(1L);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnCreatedAuthor() throws Exception {
        AuthorDtoResponse response = new AuthorDtoResponse(2L, "New Author", "", "");

        Mockito.when(authorService.create(any(AuthorDtoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Author\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("New Author"));

        verify(authorService).create(any(AuthorDtoRequest.class));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnUpdatedAuthor() throws Exception {
        AuthorDtoResponse response = new AuthorDtoResponse(1L, "Updated Author", "", "");

        Mockito.when(authorService.update(eq(1L), any(AuthorDtoRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/author/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Author\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Author")));

        verify(authorService).update(eq(1L), any(AuthorDtoRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/author/{id}", 1))
                .andExpect(status().isNoContent());

        verify(authorService).deleteById(1L);
    }
}

package com.mjc.school.implementation;

import com.mjc.school.dto.TagDtoRequest;
import com.mjc.school.dto.TagDtoResponse;
import com.mjc.school.service.TagService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TagsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @Test
    @WithMockUser(roles = "USER")
    void readAll_shouldReturnListOfTags() throws Exception {
        TagDtoResponse tag = new TagDtoResponse(1L, "Tag1");
        Mockito.when(tagService.readAll(0, 5, "name,asc"))
                .thenReturn(Collections.singletonList(tag));

        mockMvc.perform(get("/api/v1/tag")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Tag1")));

        verify(tagService).readAll(0, 5, "name,asc");
    }

    @Test
    @WithMockUser
    void readById_shouldReturnTag() throws Exception {
        TagDtoResponse tag = new TagDtoResponse(1L, "TagOne");
        Mockito.when(tagService.readById(1L)).thenReturn(tag);

        mockMvc.perform(get("/api/v1/tag/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("TagOne"));

        verify(tagService).readById(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void create_shouldReturnCreatedTag() throws Exception {
        TagDtoResponse tag = new TagDtoResponse(2L, "NewTag");
        Mockito.when(tagService.create(any(TagDtoRequest.class))).thenReturn(tag);

        mockMvc.perform(post("/api/v1/tag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"NewTag\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("NewTag")));

        verify(tagService).create(any(TagDtoRequest.class));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnUpdatedTag() throws Exception {
        TagDtoResponse tag = new TagDtoResponse(1L, "UpdatedTag");
        Mockito.when(tagService.update(eq(1L), any(TagDtoRequest.class))).thenReturn(tag);

        mockMvc.perform(patch("/api/v1/tag/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"UpdatedTag\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("UpdatedTag")));

        verify(tagService).update(eq(1L), any(TagDtoRequest.class));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/tag/{id}", 1))
                .andExpect(status().isNoContent());

        verify(tagService).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void update_withUserRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(patch("/api/v1/tag/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TagX\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_withoutAuth_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/tag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TagX\"}"))
                .andExpect(status().isUnauthorized());
    }
}

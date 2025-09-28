package com.mjc.school.service.impl;

import com.mjc.school.dto.TagDtoRequest;
import com.mjc.school.dto.TagDtoResponse;
import com.mjc.school.exception.ElementNotFoundException;
import com.mjc.school.exception.ValidatorException;
import com.mjc.school.mapper.TagMapper;
import com.mjc.school.model.Tag;
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

import static com.mjc.school.exception.ErrorCodes.INVALID_VALUE_OF_SORTING;
import static com.mjc.school.exception.ErrorCodes.NOT_UNIQUE_TAGS_NAME;
import static com.mjc.school.exception.ErrorCodes.NO_NEWS_WITH_PROVIDED_ID;
import static com.mjc.school.exception.ErrorCodes.NO_TAGS_FOR_NEWS_ID;
import static com.mjc.school.exception.ErrorCodes.NO_TAG_WITH_PROVIDED_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagsServiceTest {

    @Mock private TagRepository tagRepository;
    @Mock private TagMapper tagMapper;
    @Mock private CustomValidator customValidator;

    @InjectMocks
    private TagsServiceImpl tagsService;

    private TagDtoRequest request;
    private Tag tag;
    private TagDtoResponse response;

    @BeforeEach
    void setUp() {
        request = new TagDtoRequest("Tech");
        tag = new Tag();
        tag.setId(1L);
        tag.setName("Tech");
        response = new TagDtoResponse(1L, "Tech");
    }

    @Test
    void readAll_ShouldReturnList() {
        when(tagRepository.readAll(0, 10, "id")).thenReturn(List.of(tag));
        when(tagMapper.listModelToDtoList(anyList())).thenReturn(List.of(response));

        List<TagDtoResponse> result = tagsService.readAll(0, 10, "id");

        assertThat(result).hasSize(1).containsExactly(response);
    }

    @Test
    void readAll_ShouldThrow_WhenInvalidSort() {
        when(tagRepository.readAll(anyInt(), anyInt(), anyString()))
                .thenThrow(new InvalidDataAccessApiUsageException("bad sort"));

        assertThatThrownBy(() -> tagsService.readAll(0, 10, "bad"))
                .isInstanceOf(ValidatorException.class)
                .hasMessageContaining(INVALID_VALUE_OF_SORTING.getErrorMessage());
    }

    @Test
    void readById_ShouldReturnTag() {
        when(tagRepository.readById(1L)).thenReturn(Optional.of(tag));
        when(tagMapper.ModelTagsToDto(tag)).thenReturn(response);

        TagDtoResponse result = tagsService.readById(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void readById_ShouldThrow_WhenNotFound() {
        when(tagRepository.readById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagsService.readById(1L))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining(String.format(NO_TAG_WITH_PROVIDED_ID.getErrorMessage(), 1L));
    }

    @Test
    void create_ShouldReturnTag() {
        when(tagRepository.readTagByName("Tech")).thenReturn(Optional.empty());
        when(tagMapper.DtoTagsToModel(request)).thenReturn(tag);
        when(tagRepository.create(tag)).thenReturn(tag);
        when(tagMapper.ModelTagsToDto(tag)).thenReturn(response);

        TagDtoResponse result = tagsService.create(request);

        assertThat(result).isEqualTo(response);
        verify(customValidator).validateTag(request);
    }

    @Test
    void create_ShouldThrow_WhenDuplicateName() {
        when(tagRepository.readTagByName("Tech")).thenReturn(Optional.of(tag));

        assertThatThrownBy(() -> tagsService.create(request))
                .isInstanceOf(ValidatorException.class)
                .hasMessageContaining(
                        String.format(NOT_UNIQUE_TAGS_NAME.getErrorMessage(), "Tech")
                );
    }

    @Test
    void update_ShouldReturnTag() {
        when(tagRepository.existById(1L)).thenReturn(true);
        when(tagRepository.readTagByName("Tech")).thenReturn(Optional.empty());
        when(tagMapper.DtoTagsToModel(request)).thenReturn(tag);
        when(tagRepository.update(any(Tag.class))).thenReturn(tag);
        when(tagMapper.ModelTagsToDto(tag)).thenReturn(response);

        TagDtoResponse result = tagsService.update(1L, request);

        assertThat(result).isEqualTo(response);
        verify(customValidator).validateTag(request);
    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        when(tagRepository.existById(1L)).thenReturn(false);

        assertThatThrownBy(() -> tagsService.update(1L, request))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining(
                        String.format(NO_TAG_WITH_PROVIDED_ID.getErrorMessage(), 1L)
                );
    }

    @Test
    void update_ShouldThrow_WhenDuplicateName() {
        when(tagRepository.existById(1L)).thenReturn(true);
        when(tagRepository.readTagByName("Tech")).thenReturn(Optional.of(tag));

        assertThatThrownBy(() -> tagsService.update(1L, request))
                .isInstanceOf(ValidatorException.class)
                .hasMessageContaining(
                        String.format(NOT_UNIQUE_TAGS_NAME.getErrorMessage(), "Tech")
                );
    }

    @Test
    void deleteById_ShouldReturnTrue_WhenExists() {
        when(tagRepository.existById(1L)).thenReturn(true);
        when(tagRepository.deleteById(1L)).thenReturn(true);

        boolean result = tagsService.deleteById(1L);

        assertThat(result).isTrue();
    }

    @Test
    void deleteById_ShouldThrow_WhenNotFound() {
        when(tagRepository.existById(1L)).thenReturn(false);

        assertThatThrownBy(() -> tagsService.deleteById(1L))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining(
                        String.format(NO_TAG_WITH_PROVIDED_ID.getErrorMessage(), 1L)
                );
    }

    @Test
    void readListOfTagsByNewsId_ShouldReturnList() {
        when(tagRepository.readListOfTagsByNewsId(1L)).thenReturn(List.of(tag));
        when(tagMapper.listModelToDtoList(anyList())).thenReturn(List.of(response));

        List<TagDtoResponse> result = tagsService.readListOfTagsByNewsId(1L);

        assertThat(result).hasSize(1).containsExactly(response);
    }

    @Test
    void readListOfTagsByNewsId_ShouldThrow_WhenRepoFails() {
        when(tagRepository.readListOfTagsByNewsId(1L)).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> tagsService.readListOfTagsByNewsId(1L))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining(
                        String.format(NO_TAGS_FOR_NEWS_ID.getErrorMessage(), 1L)
                );
    }

    @Test
    void readListOfTagsByNewsId_ShouldThrow_WhenIdInvalid() {
        assertThatThrownBy(() -> tagsService.readListOfTagsByNewsId(-1L))
                .isInstanceOf(ElementNotFoundException.class)
                .hasMessageContaining(
                        String.format(NO_NEWS_WITH_PROVIDED_ID.getErrorMessage(), -1L)
                );
    }
}

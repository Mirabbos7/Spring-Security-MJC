package com.mjc.school.service.impl;

import com.mjc.school.repository.impl.TagRepository;
import com.mjc.school.model.Tag;
import com.mjc.school.dto.TagDtoRequest;
import com.mjc.school.dto.TagDtoResponse;
import com.mjc.school.exception.ElementNotFoundException;
import com.mjc.school.exception.ValidatorException;
import com.mjc.school.mapper.TagMapper;
import com.mjc.school.service.TagService;
import com.mjc.school.validation.CustomValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.mjc.school.exception.ErrorCodes.INVALID_VALUE_OF_SORTING;
import static com.mjc.school.exception.ErrorCodes.NOT_UNIQUE_TAGS_NAME;
import static com.mjc.school.exception.ErrorCodes.NO_NEWS_WITH_PROVIDED_ID;
import static com.mjc.school.exception.ErrorCodes.NO_TAGS_FOR_NEWS_ID;
import static com.mjc.school.exception.ErrorCodes.NO_TAG_WITH_PROVIDED_ID;

@Service("tagsService")
@Transactional
@RequiredArgsConstructor
public class TagsServiceImpl implements TagService {
    private final TagRepository tagsRepository;
    private final TagMapper tagMapper;
    private final CustomValidator customValidator;

    @Override
    @Transactional(readOnly = true)
    public List<TagDtoResponse> readAll(int page, int size, String sortBy) {
        try {
            return tagMapper.listModelToDtoList(tagsRepository.readAll(page, size, sortBy));
        } catch (InvalidDataAccessApiUsageException e) {
            throw new ValidatorException(String.format(INVALID_VALUE_OF_SORTING.getErrorMessage()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TagDtoResponse readById(Long id) {
        Optional<Tag> opt = tagsRepository.readById(id);
        return opt.map(tagMapper::ModelTagsToDto).orElseThrow(() -> new ElementNotFoundException(String.format(NO_TAG_WITH_PROVIDED_ID.getErrorMessage(), id)));

    }

    @Override
    @Transactional
    public TagDtoResponse create(TagDtoRequest createRequest) {
        customValidator.validateTag(createRequest);
        if (tagsRepository.readTagByName(createRequest.name()).isPresent()) {
            throw new ValidatorException(String.format(NOT_UNIQUE_TAGS_NAME.getErrorMessage(), createRequest.name()));
        }
        Tag tagModel = tagMapper.DtoTagsToModel(createRequest);
        return tagMapper.ModelTagsToDto(tagsRepository.create(tagModel));
    }

    @Override
    @Transactional
    public TagDtoResponse update(Long id, TagDtoRequest updateRequest) {
        if (tagsRepository.existById(id)) {
            customValidator.validateTag(updateRequest);
            if (tagsRepository.readTagByName(updateRequest.name()).isPresent()) {
                throw new ValidatorException(String.format(NOT_UNIQUE_TAGS_NAME.getErrorMessage(), updateRequest.name()));
            }
            Tag tagModel = tagMapper.DtoTagsToModel(updateRequest);
            tagModel.setId(id);
            return tagMapper.ModelTagsToDto(tagsRepository.update(tagModel));
        } else {
            throw new ElementNotFoundException(String.format(NO_TAG_WITH_PROVIDED_ID.getErrorMessage(), id));
        }

    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (tagsRepository.existById(id)) {
            return tagsRepository.deleteById(id);
        } else {
            throw new ElementNotFoundException(String.format(NO_TAG_WITH_PROVIDED_ID.getErrorMessage(), id));
        }
    }

    public List<TagDtoResponse> readListOfTagsByNewsId(Long newsId) {
        if (newsId != null && newsId >= 0) {
            try {
                return tagMapper.listModelToDtoList(tagsRepository.readListOfTagsByNewsId(newsId));
            } catch (Exception e) {
                throw new ElementNotFoundException(String.format(NO_TAGS_FOR_NEWS_ID.getErrorMessage(), newsId));
            }
        } else {
            throw new ElementNotFoundException(String.format(NO_NEWS_WITH_PROVIDED_ID.getErrorMessage(), newsId));

        }
    }
}


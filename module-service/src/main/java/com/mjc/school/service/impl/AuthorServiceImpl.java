package com.mjc.school.service.impl;


import com.mjc.school.repository.impl.AuthorRepository;
import com.mjc.school.model.Author;
import com.mjc.school.dto.AuthorDtoRequest;
import com.mjc.school.dto.AuthorDtoResponse;
import com.mjc.school.exception.ElementNotFoundException;
import com.mjc.school.exception.ValidatorException;
import com.mjc.school.mapper.AuthorMapper;
import com.mjc.school.service.AuthorService;
import com.mjc.school.validation.CustomValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static com.mjc.school.exception.ErrorCodes.INVALID_VALUE_OF_SORTING;
import static com.mjc.school.exception.ErrorCodes.NOT_UNIQUE_AUTHOR_NAME;
import static com.mjc.school.exception.ErrorCodes.NO_AUTHOR_FOR_NEWS_ID;
import static com.mjc.school.exception.ErrorCodes.NO_AUTHOR_WITH_PROVIDED_ID;

@Service("authorService")
@Transactional
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final CustomValidator customValidator;

    @Override
    @Transactional(readOnly = true)
    public List<AuthorDtoResponse> readAll(int page, int size, String sortBy) {
        try {
            return authorMapper.ModelListToDtoList(authorRepository.readAll(page, size, sortBy));
        } catch (InvalidDataAccessApiUsageException e) {
            throw new ValidatorException(String.format(INVALID_VALUE_OF_SORTING.getErrorMessage()));
        }
    }


    @Override
    @Transactional(readOnly = true)
    public AuthorDtoResponse readById(Long id) {
        Optional<Author> opt = authorRepository.readById(id);
        return opt.map(authorMapper::ModelAuthorToDTO).orElseThrow(() -> new ElementNotFoundException(String.format(NO_AUTHOR_WITH_PROVIDED_ID.getErrorMessage(), id)));
    }

    @Override
    @Transactional
    public AuthorDtoResponse create(@Valid AuthorDtoRequest createRequest) {
        customValidator.validateAuthor(createRequest);
        if (authorRepository.readAuthorByName(createRequest.name()).isPresent()) {
            throw new ValidatorException(String.format(NOT_UNIQUE_AUTHOR_NAME.getErrorMessage(), createRequest.name()));
        }
        Author authorModel = authorMapper.DtoAuthorToModel(createRequest);

        return authorMapper.ModelAuthorToDTO(authorRepository.create(authorModel));

    }


    @Override
    @Transactional
    public AuthorDtoResponse update(Long id, @Valid AuthorDtoRequest updateRequest) {
        if (authorRepository.existById(id)) {
            customValidator.validateAuthor(updateRequest);
            if (authorRepository.readAuthorByName(updateRequest.name()).isPresent()) {
                throw new ValidatorException(String.format(NOT_UNIQUE_AUTHOR_NAME.getErrorMessage(), updateRequest.name()));
            }
            Author authorModel = authorMapper.DtoAuthorToModel(updateRequest);
            authorModel.setId(id);
            return authorMapper.ModelAuthorToDTO(authorRepository.update(authorModel));
        } else {
            throw new ElementNotFoundException(String.format(NO_AUTHOR_WITH_PROVIDED_ID.getErrorMessage(), id));

        }
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (authorRepository.existById(id)) {
            return authorRepository.deleteById(id);
        } else {
            throw new ElementNotFoundException(String.format(NO_AUTHOR_WITH_PROVIDED_ID.getErrorMessage(), id));
        }
    }


    @Override
    public AuthorDtoResponse readAuthorByNewsId(Long newsId) {
        return authorRepository.readAuthorByNewsId(newsId).map(authorMapper::ModelAuthorToDTO).orElseThrow(() -> new ElementNotFoundException(String.format(NO_AUTHOR_FOR_NEWS_ID.getErrorMessage(), newsId)));
    }
}
package com.mjc.school.service.impl;

import com.mjc.school.repository.impl.AuthorRepository;
import com.mjc.school.repository.impl.NewsRepository;
import com.mjc.school.repository.impl.TagRepository;
import com.mjc.school.model.Author;
import com.mjc.school.model.News;
import com.mjc.school.model.Tag;
import com.mjc.school.dto.NewsDtoRequest;
import com.mjc.school.dto.NewsDtoResponse;
import com.mjc.school.dto.NewsPageDtoResponse;
import com.mjc.school.exception.ElementNotFoundException;
import com.mjc.school.exception.ErrorCodes;
import com.mjc.school.exception.ValidatorException;
import com.mjc.school.mapper.NewsMapper;
import com.mjc.school.service.NewsServiceInterface;
import com.mjc.school.validation.CustomValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.mjc.school.exception.ErrorCodes.INVALID_VALUE_OF_SORTING;
import static com.mjc.school.exception.ErrorCodes.NO_NEWS_WITH_PROVIDED_ID;
import static com.mjc.school.validation.CustomValidator.AUTHOR_NAME_MAX_LENGTH;
import static com.mjc.school.validation.CustomValidator.AUTHOR_NAME_MIN_LENGTH;
import static com.mjc.school.validation.CustomValidator.TAG_NAME_MAX_LENGTH;
import static com.mjc.school.validation.CustomValidator.TAG_NAME_MIN_LENGTH;

@Service("newsService")
@Transactional
@RequiredArgsConstructor
public class NewsService implements NewsServiceInterface<NewsDtoRequest, NewsDtoResponse, Long> {
    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;
    private final CustomValidator customValidator;

    @Override
    @Transactional(readOnly = true)
    public NewsPageDtoResponse readAll(int page, int size, String sortBy) {
        try {
            List <NewsDtoResponse> newsList = newsMapper.ModelListToDtoList((newsRepository.readAll(page, size, sortBy)));
            long totalNewsCount = newsRepository.readAll(page, size, sortBy).stream().count();
            return new NewsPageDtoResponse(newsList, totalNewsCount);
        } catch (InvalidDataAccessApiUsageException e) {
            throw new ValidatorException(String.format(INVALID_VALUE_OF_SORTING.getErrorMessage()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public NewsDtoResponse readById(Long id) {
        Optional<News> opt = newsRepository.readById(id);
        return opt.map(newsMapper::ModelNewsToDTO).orElseThrow(() -> new ElementNotFoundException(String.format(NO_NEWS_WITH_PROVIDED_ID.getErrorMessage(), id)));

    }


    @Override
    @Transactional
    public NewsDtoResponse create(NewsDtoRequest createRequest) {
        if (createRequest.authorName().isBlank()) {
            throw new ValidatorException("Author name cannot be empty");
        }
        customValidator.validateNews(createRequest);
        createNotExistAuthor(createRequest.authorName());
        if (createRequest.tagNames() == null || createRequest.tagNames().isEmpty() || createRequest.tagNames().equals("")) {
            throw new ValidatorException("Please specify tag names");
        }
        createNotExistTags(createRequest.tagNames());

        if (newsRepository.readNewsByTitle(createRequest.title()).isPresent()) {
            throw new ValidatorException("Title of news must be unique");
        }
        News newsModel = newsMapper.DTONewsToModel(createRequest);
        return newsMapper.ModelNewsToDTO(newsRepository.create(newsModel));
    }


    @Override
    @Transactional
    public NewsDtoResponse update(Long id, NewsDtoRequest updateRequest) {
        if (newsRepository.existById(id)) {
            customValidator.validateNews(updateRequest);
            createNotExistAuthor(updateRequest.authorName());
            createNotExistTags(updateRequest.tagNames());
            if (newsRepository.readNewsByTitle(updateRequest.title()).isPresent()) {
                throw new ValidatorException("Title of news must be unique");
            }
            News newsModel = newsMapper.DTONewsToModel(updateRequest);
            newsModel.setId(id);

            return newsMapper.ModelNewsToDTO(newsRepository.update(newsModel));
        } else {
            throw new ElementNotFoundException(String.format(NO_NEWS_WITH_PROVIDED_ID.getErrorMessage(), id));
        }

    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        if (newsRepository.existById(id)) {
            return newsRepository.deleteById(id);
        } else {
            throw new ElementNotFoundException(String.format(NO_NEWS_WITH_PROVIDED_ID.getErrorMessage(), id));
        }
    }

    @Override
    public List<NewsDtoResponse> readListOfNewsByParams(List<String> tagName, List<Long> tagId, String authorName, String title, String content) {

        return newsMapper.ModelListToDtoList(newsRepository.readListOfNewsByParams(tagName, tagId, authorName, title, content));
    }

    private void createNotExistAuthor(String authorName) {
        if (authorName != null && !authorName.equals("")) {
            if (authorName.length() < AUTHOR_NAME_MAX_LENGTH && authorName.length() > AUTHOR_NAME_MIN_LENGTH && !authorName.isBlank()) {
                if (authorRepository.readAuthorByName(authorName).isEmpty()) {
                    Author authorModel = new Author();
                    authorModel.setName(authorName);
                    authorRepository.create(authorModel);
                }
            } else {
                throw new ValidatorException(String.format(ErrorCodes.VALIDATION.getErrorMessage(), "Length of author`s name must be between 15 and 3"));
            }

        }
    }

    private void createNotExistTags(List<String> tagNames) {
        tagNames.stream().filter(name -> tagRepository.readTagByName(name).isEmpty()).map(name -> {
            if (name.length() >= TAG_NAME_MIN_LENGTH && name.length() < TAG_NAME_MAX_LENGTH && !name.isBlank()) {
                Tag tagModel = new Tag();
                tagModel.setName(name);
                return tagModel;
            } else {
                throw new ValidatorException(String.format(ErrorCodes.VALIDATION.getErrorMessage(), "Length of tag`s name must be between 15 and 3"));
            }
        }).forEach(tagRepository::create);
    }
}




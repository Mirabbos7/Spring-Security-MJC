package com.mjc.school.validation;

import com.mjc.school.dto.AuthorDtoRequest;
import com.mjc.school.dto.CommentDtoRequest;
import com.mjc.school.dto.NewsDtoRequest;
import com.mjc.school.dto.TagDtoRequest;
import com.mjc.school.exception.ErrorCodes;
import com.mjc.school.exception.ValidatorException;
import com.mjc.school.model.Author;
import com.mjc.school.model.Tag;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;


@Component
public class CustomValidator {
    public static final int TITLE_MAX_LENGTH = 30;
    public static final int TITLE_MIN_LENGTH = 5;
    public static final int NEWS_CONTENT_MAX_LENGTH = 255;
    public static final int NEWS_CONTENT_MIN_LENGTH = 5;
    public static final int COMMENTS_CONTENT_MAX_LENGTH = 255;
    public static final int COMMENTS_CONTENT_MIN_LENGTH = 3;
    public static final int AUTHOR_NAME_MAX_LENGTH = 15;
    public static final int AUTHOR_NAME_MIN_LENGTH = 3;
    public static final int TAG_NAME_MAX_LENGTH = 15;
    public static final int TAG_NAME_MIN_LENGTH = 3;
    public static final String ERROR_OF_AUTHORS_NAME = "Length of author`s name must be between " + AUTHOR_NAME_MIN_LENGTH + " and " + AUTHOR_NAME_MAX_LENGTH;
    public static final String ERROR_OF_TAGS_NAME = "Length of tag`s name must be between " + TAG_NAME_MIN_LENGTH + " and " + TAG_NAME_MAX_LENGTH;
    public static final String ERROR_OF_NEWS_TITLE = "Length of title must be between " + TITLE_MIN_LENGTH + " and " + TITLE_MAX_LENGTH;
    public static final String ERROR_OF_NEWS_CONTENT = "Length of content must be between " + NEWS_CONTENT_MIN_LENGTH + " and " + NEWS_CONTENT_MAX_LENGTH;
    public static final String ERROR_OF_COMMENTS_CONTENT = "Length of comment`s content must be between " + COMMENTS_CONTENT_MIN_LENGTH + " and " + COMMENTS_CONTENT_MAX_LENGTH;

    public void validateLength(String param, int minLength, int maxLength, String error) {
        if (param.length() < minLength || param.length() > maxLength) {
            throw new ValidationException(String.format(ErrorCodes.VALIDATION.getErrorMessage(), error));
        }
    }

    public void validateNews(NewsDtoRequest news) {
        validateLength(news.title(), TITLE_MIN_LENGTH, TITLE_MAX_LENGTH, ERROR_OF_NEWS_TITLE);
        validateLength(news.content(), NEWS_CONTENT_MIN_LENGTH, NEWS_CONTENT_MAX_LENGTH, ERROR_OF_NEWS_CONTENT);
    }

    public void validateAuthor(AuthorDtoRequest author) {
        validateLength(author.name(), AUTHOR_NAME_MIN_LENGTH, AUTHOR_NAME_MAX_LENGTH, ERROR_OF_AUTHORS_NAME);
    }

    public void validateTag(TagDtoRequest tag) {
        validateLength(tag.name(), TAG_NAME_MIN_LENGTH, TAG_NAME_MAX_LENGTH, ERROR_OF_TAGS_NAME);
    }

    public void validateComment(CommentDtoRequest comment) {
        validateLength(comment.content(), COMMENTS_CONTENT_MIN_LENGTH, COMMENTS_CONTENT_MAX_LENGTH, ERROR_OF_COMMENTS_CONTENT);
    }

    public void validateAuthorNameAndExistence(String authorName, Optional<Author> existingAuthor) {
        if (authorName != null && !authorName.isBlank()) {
            if (authorName.length() <= AUTHOR_NAME_MIN_LENGTH || authorName.length() >= AUTHOR_NAME_MAX_LENGTH) {
                throw new ValidatorException(
                        String.format(ErrorCodes.VALIDATION.getErrorMessage(),
                                "Length of author`s name must be between 3 and 15"));
            }
            if (existingAuthor.isPresent()) {
                throw new ValidatorException("Author already exists");
            }
        } else {
            throw new ValidatorException("Author name cannot be empty");
        }
    }

    public void validateTagNamesAndExistence(List<String> tagNames, List<Optional<Tag>> existingTags) {
        for (int i = 0; i < tagNames.size(); i++) {
            String tagName = tagNames.get(i);
            if (tagName.isBlank() || tagName.length() < TAG_NAME_MIN_LENGTH || tagName.length() >= TAG_NAME_MAX_LENGTH) {
                throw new ValidatorException(
                        String.format(ErrorCodes.VALIDATION.getErrorMessage(),
                                "Length of tag`s name must be between 3 and 15"));
            }
            if (existingTags.get(i).isPresent()) {
                throw new ValidatorException("Tag with name '" + tagName + "' already exists");
            }
        }
    }
}


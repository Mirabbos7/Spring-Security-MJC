package com.mjc.school.controller.hateoas;

import com.mjc.school.controller.implementation.AuthorController;
import com.mjc.school.controller.implementation.CommentController;
import com.mjc.school.controller.implementation.NewsController;
import com.mjc.school.controller.implementation.TagsController;
import com.mjc.school.service.dto.AuthorDtoResponse;
import com.mjc.school.service.dto.CommentDtoResponse;
import com.mjc.school.service.dto.NewsDtoResponse;
import com.mjc.school.service.dto.TagDtoResponse;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class LinkHelper {
    private LinkHelper() {
    }

    public static void addLinkToTags(EntityModel<TagDtoResponse> tagModel) {
        TagDtoResponse content = tagModel.getContent();
        if (content == null) return;
        tagModel.add(linkTo(methodOn(TagsController.class).readById(content.id())).withSelfRel());
        tagModel.add(linkTo(methodOn(NewsController.class).readById(content.id())).withRel("news"));
    }

    public static void addLinkToComments(EntityModel<CommentDtoResponse> commentModel) {
        CommentDtoResponse content = commentModel.getContent();
        if (content == null) return;
        commentModel.add(linkTo(methodOn(CommentController.class).readById(content.id())).withSelfRel());
        commentModel.add(linkTo(methodOn(NewsController.class).readById(content.newsId())).withRel("news"));
    }

    public static void addLinkToAuthors(EntityModel<AuthorDtoResponse> authorModel) {
        AuthorDtoResponse content = authorModel.getContent();
        if (content == null) return;
        authorModel.add(linkTo(methodOn(AuthorController.class).readById(content.id())).withSelfRel());
    }

    public static void addLinkToNews(EntityModel<NewsDtoResponse> newsModel) {
        NewsDtoResponse content = newsModel.getContent();
        if (content == null) return;
        newsModel.add(linkTo(methodOn(NewsController.class).readById(content.id())).withRel("news"));
        newsModel.add(linkTo(methodOn(NewsController.class).readAuthorByNewsId(content.id())).withRel("author"));
        newsModel.add(linkTo(methodOn(NewsController.class).readListOfTagsByNewsId(content.id())).withRel("tags"));
        newsModel.add(linkTo(methodOn(NewsController.class).readListOfCommentsByNewsId(content.id())).withRel("comments"));
    }
}

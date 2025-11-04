package com.mjc.school.controller;

import com.mjc.school.annotation.CommandParam;
import com.mjc.school.dto.AuthorDtoResponse;
import com.mjc.school.dto.CommentDtoResponse;
import com.mjc.school.dto.NewsDtoRequest;
import com.mjc.school.dto.NewsDtoResponse;
import com.mjc.school.dto.NewsPageDtoResponse;
import com.mjc.school.dto.TagDtoResponse;
import com.mjc.school.hateoas.LinkHelper;
import com.mjc.school.repository.impl.NewsRepository;
import com.mjc.school.service.AuthorService;
import com.mjc.school.service.CommentService;
import com.mjc.school.service.NewsService;
import com.mjc.school.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1/news", produces = "application/json")
@Api(value = "News", description = "Operations for creating, updating, retrieving and deleting news in the application")
@CrossOrigin(
        origins = "http://localhost:3000",
        methods = {RequestMethod.PUT, RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PATCH}
)
public class NewsController implements NewsControllerInterface<NewsDtoRequest, NewsDtoResponse, Long> {

    private final NewsService<NewsDtoRequest, NewsDtoResponse, Long> newsService;
    private final AuthorService authorService;
    private final TagService tagService;
    private final CommentService commentService;
    private final NewsRepository newsRepository;

    @GetMapping
    @Override
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all news with pagination", response = NewsPageDtoResponse.class)
    public EntityModel<NewsPageDtoResponse> readAll(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "createDate,dsc") String sortBy) {
        NewsPageDtoResponse response = newsService.readAll(page, size, sortBy);
        return EntityModel.of(response);
    }

    @GetMapping("/getAll")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all news")
    public ResponseEntity<List<NewsDtoResponse>> getAllNews(){
        try {
            NewsPageDtoResponse pageResponse = newsService.readAll(0, 1000, "createDate,desc");
            return ResponseEntity.ok(pageResponse.newsList());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get news count")
    public ResponseEntity<Long> countNews(){
        try {
            return ResponseEntity.ok(newsRepository.countNews());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/authors")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all authors")
    public ResponseEntity<List<AuthorDtoResponse>> getAuthors(){
        try {
            List<NewsDtoResponse> allNews = newsService.readAll(0, 1000, "createDate,desc").newsList();
            List<AuthorDtoResponse> authors = allNews.stream()
                    .map(news -> authorService.readAuthorByNewsId(news.id()))
                    .distinct()
                    .toList();
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @Override
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<NewsDtoResponse> readById(@CommandParam("newsId") @PathVariable Long id) {
        EntityModel<NewsDtoResponse> model = EntityModel.of(newsService.readById(id));
        LinkHelper.addLinkToNews(model);
        return model;
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public EntityModel<NewsDtoResponse> create(@RequestBody NewsDtoRequest createRequest) {
        EntityModel<NewsDtoResponse> model = EntityModel.of(newsService.create(createRequest));
        LinkHelper.addLinkToNews(model);
        return model;
    }

    @Override
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public EntityModel<NewsDtoResponse> update(@PathVariable Long id, @RequestBody NewsDtoRequest updateRequest) {
        EntityModel<NewsDtoResponse> model = EntityModel.of(newsService.update(id, updateRequest));
        LinkHelper.addLinkToNews(model);
        return model;
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteById(@CommandParam("newsId") @PathVariable Long id) {
        newsService.deleteById(id);
    }

    @GetMapping(value = "/search")
    @ResponseStatus(HttpStatus.OK)
    public List<NewsDtoResponse> readListOfNewsByParams(
            @RequestParam(name = "tag_name", required = false) List<String> tagName,
            @RequestParam(name = "tag_id", required = false) List<Long> tagId,
            @RequestParam(name = "author_name", required = false) String authorName,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "content", required = false) String content) {
        return newsService.readListOfNewsByParams(tagName, tagId, authorName, title, content);
    }

    @GetMapping(value = "/{newsId:\\d+}/tag")
    @ResponseStatus(HttpStatus.OK)
    public List<EntityModel<TagDtoResponse>> readListOfTagsByNewsId(@PathVariable Long newsId) {
        List<EntityModel<TagDtoResponse>> tagModels = tagService.readListOfTagsByNewsId(newsId).stream().map(EntityModel::of).toList();
        tagModels.forEach(LinkHelper::addLinkToTags);
        return tagModels;
    }

    @GetMapping(value = "/{newsId:\\d+}/author")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<AuthorDtoResponse> readAuthorByNewsId(@PathVariable Long newsId) {
        EntityModel<AuthorDtoResponse> model = EntityModel.of(authorService.readAuthorByNewsId(newsId));
        LinkHelper.addLinkToAuthors(model);
        return model;
    }

    @GetMapping(value = "/{newsId:\\d+}/comment")
    @ResponseStatus(HttpStatus.OK)
    public List<EntityModel<CommentDtoResponse>> readListOfCommentsByNewsId(@PathVariable Long newsId) {
        List<EntityModel<CommentDtoResponse>> commentModels = commentService.readListOfCommentsByNewsId(newsId).stream().map(EntityModel::of).toList();
        commentModels.forEach(LinkHelper::addLinkToComments);
        return commentModels;
    }
}
package com.mjc.school.controller;

import com.mjc.school.hateoas.LinkHelper;
import com.mjc.school.dto.AuthorDtoRequest;
import com.mjc.school.dto.AuthorDtoResponse;
import com.mjc.school.service.AuthorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1/author", produces = "application/json")
@Api(value = "Authors", description = "Operations for creating, updating, retrieving and deleting author in the application")
@Validated
@RequiredArgsConstructor
public class AuthorController implements BaseController<AuthorDtoRequest, AuthorDtoResponse, Long> {

    private final AuthorService authorService;

    @Override
    @GetMapping("/readAll")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get all authors", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully fetched all authors"),
            @ApiResponse(code = 400, message = "Invalid request from the client"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public List<AuthorDtoResponse> readAll(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "name,dsc") String sortBy) {
        return this.authorService.readAll(page, size, sortBy);
    }

    @Override
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Get author by ID", response = AuthorDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully fetched author by ID"),
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public EntityModel<AuthorDtoResponse> readById(@PathVariable Long id) {
        EntityModel<AuthorDtoResponse> model = EntityModel.of(authorService.readById(id));
        LinkHelper.addLinkToAuthors(model);
        return model;
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create an author", response = AuthorDtoResponse.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created an author"),
            @ApiResponse(code = 400, message = "Invalid request from the client"),
            @ApiResponse(code = 401, message = "User is unauthorised"),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public EntityModel<AuthorDtoResponse> create(@Valid @RequestBody AuthorDtoRequest createRequest) {
        EntityModel<AuthorDtoResponse> model = EntityModel.of(authorService.create(createRequest));
        LinkHelper.addLinkToAuthors(model);
        System.out.println(createRequest.name());
        return model;

    }


    @Override
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update an author", response = AuthorDtoResponse.class)
    @PreAuthorize(value = "hasRole('ROLE_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated an author"),
            @ApiResponse(code = 400, message = "Invalid request from the client"),
            @ApiResponse(code = 401, message = "User is unauthorised"),
            @ApiResponse(code = 403, message = "User don`t have permission to access."),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public EntityModel<AuthorDtoResponse> update(@PathVariable Long id, @RequestBody AuthorDtoRequest updateRequest) {
        EntityModel<AuthorDtoResponse> model = EntityModel.of(authorService.update(id, updateRequest));
        LinkHelper.addLinkToAuthors(model);
        return model;
    }


    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Delete author by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted author by ID"),
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 401, message = "User is unauthorised"),
            @ApiResponse(code = 403, message = "User don`t have permission to access."),
            @ApiResponse(code = 404, message = "Resource is not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public void deleteById(@PathVariable Long id) {
        authorService.deleteById(id);
    }

}
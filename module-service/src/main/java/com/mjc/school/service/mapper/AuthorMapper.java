package com.mjc.school.service.mapper;

import com.mjc.school.repository.model.AuthorModel;
import com.mjc.school.service.dto.AuthorDtoRequest;
import com.mjc.school.service.dto.AuthorDtoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {


    @Mappings(value = {@Mapping(target = "createDate", ignore = true),
            @Mapping(target = "lastUpdateDate", ignore = true),
            @Mapping(target = "newsModelListWithId", ignore = true),
            @Mapping(target = "id", ignore = true)})
    AuthorModel DtoAuthorToModel(AuthorDtoRequest authorDtoRequest);

    @Mapping(target = "createDate", expression = "java(authorModel.getCreateDate().format(java.time.format.DateTimeFormatter.ISO_DATE_TIME))")
    @Mapping(target = "lastUpdateDate", expression = "java(authorModel.getLastUpdateDate().format(java.time.format.DateTimeFormatter.ISO_DATE_TIME))")
    AuthorDtoResponse ModelAuthorToDTO(AuthorModel authorModel);

    List<AuthorDtoResponse> ModelListToDtoList(List<AuthorModel> modelList);


}

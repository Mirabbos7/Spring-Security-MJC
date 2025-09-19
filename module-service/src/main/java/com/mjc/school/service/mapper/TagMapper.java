package com.mjc.school.service.mapper;

import com.mjc.school.repository.model.TagModel;
import com.mjc.school.service.dto.TagDtoRequest;
import com.mjc.school.service.dto.TagDtoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {
    @Mappings(value = {@Mapping(target = "news", ignore = true),
            @Mapping(target = "id", ignore = true)})
    TagModel DtoTagsToModel(TagDtoRequest tagDtoRequest);

    List<TagDtoResponse> listModelToDtoList(List<TagModel> tags);

    TagDtoResponse ModelTagsToDto(TagModel tagModel);
}

package com.mjc.school.mapper;

import com.mjc.school.model.Tag;
import com.mjc.school.dto.TagDtoRequest;
import com.mjc.school.dto.TagDtoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {
    @Mappings(value = {@Mapping(target = "news", ignore = true),
            @Mapping(target = "id", ignore = true)})
    Tag DtoTagsToModel(TagDtoRequest tagDtoRequest);

    List<TagDtoResponse> listModelToDtoList(List<Tag> tags);

    TagDtoResponse ModelTagsToDto(Tag tagModel);
}

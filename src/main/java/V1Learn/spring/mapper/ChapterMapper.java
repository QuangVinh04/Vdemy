package V1Learn.spring.mapper;


import V1Learn.spring.dto.request.ChapterRequest;

import V1Learn.spring.dto.response.ChapterResponse;
import V1Learn.spring.entity.Chapter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChapterMapper {

    @Mapping(target = "lessons", ignore = true)
    Chapter toChapter(ChapterRequest request);

    @Mapping(target = "lessons", ignore = true)
    ChapterResponse toChapterResponse(Chapter chapter);


}

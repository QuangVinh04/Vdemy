package V1Learn.spring.Mapper;


import V1Learn.spring.DTO.Request.ChapterRequest;

import V1Learn.spring.DTO.Response.ChapterResponse;
import V1Learn.spring.Entity.Chapter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChapterMapper {

    @Mapping(target = "lessons", ignore = true)
    Chapter toChapter(ChapterRequest request);

    @Mapping(target = "lessons", ignore = true)
    ChapterResponse toChapterResponse(Chapter chapter);


}

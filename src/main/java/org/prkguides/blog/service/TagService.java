package org.prkguides.blog.service;

import org.prkguides.blog.dto.PaginationResponse;
import org.prkguides.blog.dto.PostSummaryDto;
import org.prkguides.blog.dto.TagCreateDto;
import org.prkguides.blog.dto.TagDto;

import java.util.List;

public interface TagService {

    // CRUD operations
    TagDto createTag(TagCreateDto tagCreateDto);
    TagDto updateTag(Long id, TagCreateDto tagUpdateDto);
    TagDto getTagById(Long id);
    TagDto getTagBySlug(String slug);
    void deleteTag(Long id);

    // Listing and search
    PaginationResponse<TagDto> getAllTags(int pageNo, int pageSize);
    List<TagDto> getPopularTags(int limit);
    List<TagDto> searchTags(String query);

    // Post associations
    PaginationResponse<PostSummaryDto> getPostsByTag(Long tagId, int pageNo, int pageSize);

    // Statistics
    Long getTotalTagCount();
}

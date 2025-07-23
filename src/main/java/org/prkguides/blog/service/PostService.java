package org.prkguides.blog.service;

import org.prkguides.blog.dto.*;

import java.util.List;

public interface PostService {

    // CRUD operations
    PostDto createPost(PostCreateDto postCreateDto, String authorUsername);
    PostDto updatePost(Long id, PostCreateDto postUpdateDto);
    PostDto getPostById(Long id);
    PostDto getPostBySlug(String slug);
    void deletePost(Long id);

    // Listing and pagination
    PaginationResponse<PostSummaryDto> getAllPosts(int pageNo, int pageSize);
    PaginationResponse<PostSummaryDto> getPublishedPosts(int pageNo, int pageSize);
    PaginationResponse<PostSummaryDto> getFeaturedPosts(int pageNo, int pageSize);
    PaginationResponse<PostSummaryDto> getPostsByAuthor(String username, int pageNo, int pageSize);
    PaginationResponse<PostSummaryDto> getPostsByTag(String tagName, int pageNo, int pageSize);

    // Search functionality
    PaginationResponse<PostSummaryDto> searchPosts(SearchRequestDto searchRequest);

    // Related and popular content
    List<PostSummaryDto> getRelatedPosts(Long postId, int limit);
    List<PostSummaryDto> getPopularPosts(int limit);
    List<PostSummaryDto> getRecentPosts(int limit);

    // View tracking
    void incrementViewCount(Long id);

    // Publishing workflow
    PostDto publishPost(Long id);
    PostDto unpublishPost(Long id);
    PostDto schedulePost(Long id, String publishDate);

    // Featured posts management
    PostDto toggleFeatured(Long id);

    // Statistics
    Long getTotalPostCount();
    Long getPublishedPostCount();
    Long getDraftPostCount();
}

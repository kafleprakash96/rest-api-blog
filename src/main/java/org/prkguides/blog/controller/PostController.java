package org.prkguides.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prkguides.blog.dto.*;
import org.prkguides.blog.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@CrossOrigin(origins = {"http://localhost:4200", "https://yourdomain.com"})
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Blog post management endpoints")
public class PostController {

    private final PostService postService;


    @Operation(summary = "Create a new blog post", description = "Creates a new blog post. Requires admin privileges.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<APIResponse<PostDto>> createPost(
            @Valid @RequestBody PostCreateDto postCreateDto,
            Authentication authentication) {

        log.info("Creating new post: {}", postCreateDto.getTitle());
        PostDto createdPost = postService.createPost(postCreateDto, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success("Post created successfully", createdPost));
    }

    @Operation(summary = "Get all posts with pagination", description = "Retrieves all posts with pagination support")
    @GetMapping
    public ResponseEntity<APIResponse<PaginationResponse<PostSummaryDto>>> getAllPosts(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @Parameter(description = "Page size")
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        PaginationResponse<PostSummaryDto> posts = postService.getAllPosts(pageNo, pageSize);

        if (posts.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(APIResponse.success("No posts found", posts));
        }

        return ResponseEntity.ok(APIResponse.success("Posts retrieved successfully", posts));
    }

    @Operation(summary = "Get published posts", description = "Retrieves only published posts with pagination")
    @GetMapping("/published")
    public ResponseEntity<APIResponse<PaginationResponse<PostSummaryDto>>> getPublishedPosts(
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        PaginationResponse<PostSummaryDto> posts = postService.getPublishedPosts(pageNo, pageSize);
        return ResponseEntity.ok(APIResponse.success("Published posts retrieved successfully", posts));
    }

    @Operation(summary = "Get featured posts", description = "Retrieves featured posts")
    @GetMapping("/featured")
    public ResponseEntity<APIResponse<PaginationResponse<PostSummaryDto>>> getFeaturedPosts(
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "6") int pageSize) {

        PaginationResponse<PostSummaryDto> posts = postService.getFeaturedPosts(pageNo, pageSize);
        return ResponseEntity.ok(APIResponse.success("Featured posts retrieved successfully", posts));
    }

    @Operation(summary = "Get post by ID", description = "Retrieves a specific post by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<PostDto>> getPostById(
            @Parameter(description = "Post ID") @PathVariable Long id) {

        PostDto post = postService.getPostById(id);

        // Increment view count for published posts
        if (post.getStatus().name().equals("PUBLISHED")) {
            postService.incrementViewCount(id);
        }

        return ResponseEntity.ok(APIResponse.success("Post retrieved successfully", post));
    }

    @Operation(summary = "Get post by slug", description = "Retrieves a specific post by its URL slug")
    @GetMapping("/slug/{slug}")
    public ResponseEntity<APIResponse<PostDto>> getPostBySlug(
            @Parameter(description = "Post slug") @PathVariable String slug) {

        PostDto post = postService.getPostBySlug(slug);

        // Increment view count for published posts
        if (post.getStatus().name().equals("PUBLISHED")) {
            postService.incrementViewCount(post.getId());
        }

        return ResponseEntity.ok(APIResponse.success("Post retrieved successfully", post));
    }

    @Operation(summary = "Update a blog post", description = "Updates an existing blog post. Requires admin privileges.")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<PostDto>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostCreateDto postUpdateDto) {

        log.info("Updating post with ID: {}", id);
        PostDto updatedPost = postService.updatePost(id, postUpdateDto);

        return ResponseEntity.ok(APIResponse.success("Post updated successfully", updatedPost));
    }

    @Operation(summary = "Delete a blog post", description = "Deletes a blog post. Requires admin privileges.")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deletePost(@PathVariable Long id) {

        log.info("Deleting post with ID: {}", id);
        postService.deletePost(id);

        return ResponseEntity.ok(APIResponse.success("Post deleted successfully",
                "Post with ID " + id + " has been deleted"));
    }

    @Operation(summary = "Search posts", description = "Search posts with various filters")
    @PostMapping("/search")
    public ResponseEntity<APIResponse<PaginationResponse<PostSummaryDto>>> searchPosts(
            @Valid @RequestBody SearchRequestDto searchRequest) {

        PaginationResponse<PostSummaryDto> posts = postService.searchPosts(searchRequest);
        return ResponseEntity.ok(APIResponse.success("Search completed successfully", posts));
    }

    @Operation(summary = "Get posts by author", description = "Retrieves posts by a specific author")
    @GetMapping("/author/{username}")
    public ResponseEntity<APIResponse<PaginationResponse<PostSummaryDto>>> getPostsByAuthor(
            @PathVariable String username,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        PaginationResponse<PostSummaryDto> posts = postService.getPostsByAuthor(username, pageNo, pageSize);
        return ResponseEntity.ok(APIResponse.success("Posts by author retrieved successfully", posts));
    }

    @Operation(summary = "Get posts by tag", description = "Retrieves posts with a specific tag")
    @GetMapping("/tag/{tagName}")
    public ResponseEntity<APIResponse<PaginationResponse<PostSummaryDto>>> getPostsByTag(
            @PathVariable String tagName,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        PaginationResponse<PostSummaryDto> posts = postService.getPostsByTag(tagName, pageNo, pageSize);
        return ResponseEntity.ok(APIResponse.success("Posts by tag retrieved successfully", posts));
    }

    @Operation(summary = "Get related posts", description = "Retrieves posts related to a specific post")
    @GetMapping("/{id}/related")
    public ResponseEntity<APIResponse<List<PostSummaryDto>>> getRelatedPosts(
            @PathVariable Long id,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {

        List<PostSummaryDto> relatedPosts = postService.getRelatedPosts(id, limit);
        return ResponseEntity.ok(APIResponse.success("Related posts retrieved successfully", relatedPosts));
    }

    @Operation(summary = "Get popular posts", description = "Retrieves most viewed posts")
    @GetMapping("/popular")
    public ResponseEntity<APIResponse<List<PostSummaryDto>>> getPopularPosts(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<PostSummaryDto> popularPosts = postService.getPopularPosts(limit);
        return ResponseEntity.ok(APIResponse.success("Popular posts retrieved successfully", popularPosts));
    }

    @Operation(summary = "Get recent posts", description = "Retrieves most recent posts")
    @GetMapping("/recent")
    public ResponseEntity<APIResponse<List<PostSummaryDto>>> getRecentPosts(
            @RequestParam(value = "limit", defaultValue = "5") int limit) {

        List<PostSummaryDto> recentPosts = postService.getRecentPosts(limit);
        return ResponseEntity.ok(APIResponse.success("Recent posts retrieved successfully", recentPosts));
    }

    // Admin-only operations
    @Operation(summary = "Publish a post", description = "Publishes a draft post. Requires admin privileges.")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/publish")
    public ResponseEntity<APIResponse<PostDto>> publishPost(@PathVariable Long id) {

        PostDto publishedPost = postService.publishPost(id);
        return ResponseEntity.ok(APIResponse.success("Post published successfully", publishedPost));
    }

    @Operation(summary = "Unpublish a post", description = "Unpublishes a post. Requires admin privileges.")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/unpublish")
    public ResponseEntity<APIResponse<PostDto>> unpublishPost(@PathVariable Long id) {

        PostDto unpublishedPost = postService.unpublishPost(id);
        return ResponseEntity.ok(APIResponse.success("Post unpublished successfully", unpublishedPost));
    }

    @Operation(summary = "Toggle featured status", description = "Toggles the featured status of a post. Requires admin privileges.")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/toggle-featured")
    public ResponseEntity<APIResponse<PostDto>> toggleFeatured(@PathVariable Long id) {

        PostDto post = postService.toggleFeatured(id);
        return ResponseEntity.ok(APIResponse.success("Featured status toggled successfully", post));
    }
}


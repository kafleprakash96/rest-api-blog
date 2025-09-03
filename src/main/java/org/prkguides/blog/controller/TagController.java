package org.prkguides.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prkguides.blog.dto.*;
import org.prkguides.blog.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/tags")
@CrossOrigin(origins = {"http://localhost:4200"})
@RequiredArgsConstructor
@Tag(name = "Tags", description = "Tag management endpoints")
public class TagController {

    private final TagService tagService;

    @Operation(summary = "Create a new tag", description = "Creates a new tag. Requires admin privileges.")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<APIResponse<TagDto>> createTag(
            @Valid @RequestBody TagCreateDto tagCreateDto) {

        log.info("Creating new tag: {}", tagCreateDto.getName());
        TagDto createdTag = tagService.createTag(tagCreateDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success("Tag created successfully", createdTag));
    }

    @Operation(summary = "Get all tags", description = "Retrieves all tags with optional pagination")
    @GetMapping
    public ResponseEntity<APIResponse<PaginationResponse<TagDto>>> getAllTags(
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "50") int pageSize) {

        PaginationResponse<TagDto> tags = tagService.getAllTags(pageNo, pageSize);
        return ResponseEntity.ok(APIResponse.success("Tags retrieved successfully", tags));
    }

    @Operation(summary = "Get popular tags", description = "Retrieves popular tags based on post count")
    @GetMapping("/popular")
    public ResponseEntity<APIResponse<List<TagDto>>> getPopularTags(
            @RequestParam(value = "limit", defaultValue = "20") int limit) {

        List<TagDto> popularTags = tagService.getPopularTags(limit);
        return ResponseEntity.ok(APIResponse.success("Popular tags retrieved successfully", popularTags));
    }

    @Operation(summary = "Get tag by ID", description = "Retrieves a specific tag by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<TagDto>> getTagById(
            @Parameter(description = "Tag ID") @PathVariable Long id) {

        TagDto tag = tagService.getTagById(id);
        return ResponseEntity.ok(APIResponse.success("Tag retrieved successfully", tag));
    }

    @Operation(summary = "Get tag by slug", description = "Retrieves a specific tag by its slug")
    @GetMapping("/slug/{slug}")
    public ResponseEntity<APIResponse<TagDto>> getTagBySlug(
            @Parameter(description = "Tag slug") @PathVariable String slug) {

        TagDto tag = tagService.getTagBySlug(slug);
        return ResponseEntity.ok(APIResponse.success("Tag retrieved successfully", tag));
    }

    @Operation(summary = "Update tag", description = "Updates an existing tag. Requires admin privileges.")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<TagDto>> updateTag(
            @PathVariable Long id,
            @Valid @RequestBody TagCreateDto tagUpdateDto) {

        log.info("Updating tag with ID: {}", id);
        TagDto updatedTag = tagService.updateTag(id, tagUpdateDto);

        return ResponseEntity.ok(APIResponse.success("Tag updated successfully", updatedTag));
    }

    @Operation(summary = "Delete tag", description = "Deletes a tag. Requires admin privileges.")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteTag(@PathVariable Long id) {

        log.info("Deleting tag with ID: {}", id);
        tagService.deleteTag(id);

        return ResponseEntity.ok(APIResponse.success("Tag deleted successfully",
                "Tag with ID " + id + " has been deleted"));
    }

    @Operation(summary = "Search tags", description = "Search tags by name")
    @GetMapping("/search")
    public ResponseEntity<APIResponse<List<TagDto>>> searchTags(
            @RequestParam String query) {

        List<TagDto> tags = tagService.searchTags(query);
        return ResponseEntity.ok(APIResponse.success("Tag search completed successfully", tags));
    }

    @Operation(summary = "Get posts by tag", description = "Get all posts associated with a specific tag")
    @GetMapping("/{id}/posts")
    public ResponseEntity<APIResponse<PaginationResponse<PostSummaryDto>>> getPostsByTag(
            @PathVariable Long id,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        PaginationResponse<PostSummaryDto> posts = tagService.getPostsByTag(id, pageNo, pageSize);
        return ResponseEntity.ok(APIResponse.success("Posts by tag retrieved successfully", posts));
    }
}

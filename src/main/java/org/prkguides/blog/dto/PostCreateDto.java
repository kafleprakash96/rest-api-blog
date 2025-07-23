package org.prkguides.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.prkguides.blog.enums.PostStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Post creation request")
public class PostCreateDto {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    @Schema(description = "Post title", example = "Getting Started with Spring Boot")
    private String title;

    @Size(max = 500, message = "Excerpt must not exceed 500 characters")
    @Schema(description = "Post excerpt/summary")
    private String excerpt;

    @NotBlank(message = "Content is required")
    @Schema(description = "Post content in HTML format")
    private String content;

    @Schema(description = "Featured image URL")
    private String featuredImageUrl;

    @Min(value = 1, message = "Reading time must be at least 1 minute")
    @Schema(description = "Estimated reading time in minutes", example = "5")
    private Integer readingTimeMinutes;

    @NotNull(message = "Status is required")
    @Schema(description = "Post status", example = "PUBLISHED")
    private PostStatus status;

    @Schema(description = "Publication date (for scheduled posts)")
    private LocalDateTime publishedDate;

    @Size(max = 160, message = "Meta description must not exceed 160 characters")
    @Schema(description = "SEO meta description")
    private String metaDescription;

    @Size(max = 255, message = "Meta keywords must not exceed 255 characters")
    @Schema(description = "SEO meta keywords")
    private String metaKeywords;

    @Schema(description = "Tag names to associate with the post")
    private Set<String> tagNames;

    @Schema(description = "Whether post is featured", example = "false")
    private Boolean isFeatured = false;

    @Schema(description = "Whether comments are allowed", example = "true")
    private Boolean allowComments = true;
}


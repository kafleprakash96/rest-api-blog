package org.prkguides.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.prkguides.blog.enums.PostStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Post summary for listings")
public class PostSummaryDto {

    @Schema(description = "Post ID", example = "1")
    private Long id;

    @Schema(description = "Post title", example = "Getting Started with Spring Boot")
    private String title;

    @Schema(description = "URL-friendly slug", example = "getting-started-with-spring-boot")
    private String slug;

    @Schema(description = "Post excerpt/summary")
    private String excerpt;

    @Schema(description = "Featured image URL")
    private String featuredImageUrl;

    @Schema(description = "Estimated reading time in minutes", example = "5")
    private Integer readingTimeMinutes;

    @Schema(description = "Number of views", example = "1250")
    private Long viewCount;

    @Schema(description = "Post status", example = "PUBLISHED")
    private PostStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Publication date")
    private LocalDateTime publishedDate;

    @Schema(description = "Post author information")
    private UserSummaryDto author;

    @Schema(description = "Post tags")
    private Set<TagDto> tags;

    @Schema(description = "Whether post is featured", example = "true")
    private Boolean isFeatured;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
}

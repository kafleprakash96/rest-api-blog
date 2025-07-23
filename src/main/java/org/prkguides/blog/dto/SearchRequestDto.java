package org.prkguides.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.prkguides.blog.enums.PostStatus;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Search request parameters")
public class SearchRequestDto {

    @Schema(description = "Search query", example = "spring boot tutorial")
    private String query;

    @Schema(description = "Filter by tags")
    private Set<String> tags;

    @Schema(description = "Filter by author username")
    private String author;

    @Schema(description = "Filter by post status", example = "PUBLISHED")
    private PostStatus status;

    @Schema(description = "Only featured posts", example = "true")
    private Boolean featuredOnly;

    @Schema(description = "Page number (0-based)", example = "0")
    private Integer page = 0;

    @Schema(description = "Page size", example = "10")
    private Integer size = 10;

    @Schema(description = "Sort field", example = "publishedDate")
    private String sortBy = "publishedDate";

    @Schema(description = "Sort direction", example = "desc")
    private String sortDirection = "desc";
}

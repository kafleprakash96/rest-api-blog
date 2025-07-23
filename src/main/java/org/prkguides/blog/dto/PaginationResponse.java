package org.prkguides.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Paginated response wrapper")
public class PaginationResponse<T> {

    @Schema(description = "Page content")
    private List<T> content;

    @Schema(description = "Current page number (0-based)", example = "0")
    private Integer pageNo;

    @Schema(description = "Page size", example = "10")
    private Integer pageSize;

    @Schema(description = "Total number of elements", example = "150")
    private Long totalElements;

    @Schema(description = "Total number of pages", example = "15")
    private Integer totalPages;

    @Schema(description = "Whether this is the last page", example = "false")
    private Boolean last;

    @Schema(description = "Whether this is the first page", example = "true")
    private Boolean first;

    @Schema(description = "Whether the page is empty", example = "false")
    private Boolean empty;
}

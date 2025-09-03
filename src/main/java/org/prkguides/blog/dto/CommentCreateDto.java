package org.prkguides.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Comment creation request")
public class CommentCreateDto {

    @NotBlank(message = "Content is required")
    @Size(min = 5, max = 1000, message = "Content must be between 5 and 1000 characters")
    @Schema(description = "Comment content")
    private String content;

    @NotNull(message = "Post ID is required")
    @Schema(description = "Post ID this comment belongs to", example = "1")
    private Long postId;

    @Schema(description = "Parent comment ID (for replies)", example = "1")
    private Long parentCommentId;
}

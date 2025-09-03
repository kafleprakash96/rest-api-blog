package org.prkguides.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.prkguides.blog.enums.CommentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Comment information")
public class CommentDto {

    @Schema(description = "Comment ID", example = "1")
    private Long id;

    @Schema(description = "Comment content")
    private String content;

    @Schema(description = "Comment status", example = "APPROVED")
    private CommentStatus status;

    @Schema(description = "Post ID this comment belongs to", example = "1")
    private Long postId;

    @Schema(description = "Parent comment ID (for replies)", example = "1")
    private Long parentCommentId;

    @Schema(description = "Comment author information")
    private UserSummaryDto author;

    @Schema(description = "Replies to this comment")
    private List<CommentDto> replies;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}

package org.prkguides.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Comment update request")
public class CommentUpdateDto {

    @NotBlank(message = "Content is required")
    @Size(min = 5, max = 1000, message = "Content must be between 5 and 1000 characters")
    @Schema(description = "Updated comment content")
    private String content;
}

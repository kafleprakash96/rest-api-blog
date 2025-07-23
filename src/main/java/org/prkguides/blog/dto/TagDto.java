package org.prkguides.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Tag data transfer object")
public class TagDto {

    @Schema(description = "Tag ID", example = "1")
    private Long id;

    @NotBlank(message = "Tag name is required")
    @Size(min = 2, max = 50, message = "Tag name must be between 2 and 50 characters")
    @Schema(description = "Tag name", example = "Spring Boot")
    private String name;

    @Schema(description = "URL-friendly slug", example = "spring-boot")
    private String slug;

    @Size(max = 200, message = "Description must not exceed 200 characters")
    @Schema(description = "Tag description")
    private String description;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Color must be a valid hex color")
    @Schema(description = "Tag color in hex format", example = "#3498db")
    private String color;

    @Schema(description = "Number of posts with this tag", example = "15")
    private Integer postCount;
}


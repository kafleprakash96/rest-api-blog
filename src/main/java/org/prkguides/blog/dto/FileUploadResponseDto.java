package org.prkguides.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "File upload response")
public class FileUploadResponseDto {

    @Schema(description = "Original filename", example = "image.jpg")
    private String originalFilename;

    @Schema(description = "Stored filename", example = "uuid-image.jpg")
    private String filename;

    @Schema(description = "File URL", example = "/api/v1/files/images/uuid-image.jpg")
    private String url;

    @Schema(description = "File size in bytes", example = "1024")
    private Long size;

    @Schema(description = "File content type", example = "image/jpeg")
    private String contentType;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Upload timestamp")
    private LocalDateTime uploadedAt;
}

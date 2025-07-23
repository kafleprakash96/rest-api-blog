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
@Schema(description = "Standard API response wrapper")
public class APIResponse<T> {

    @Schema(description = "Response status", example = "success")
    private String status;

    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;

    @Schema(description = "Response data")
    private T data;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Response timestamp")
    private LocalDateTime timestamp;

    public APIResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> APIResponse<T> success(String message, T data) {
        return new APIResponse<>("success", message, data);
    }

    public static <T> APIResponse<T> success(T data) {
        return new APIResponse<>("success", "Operation completed successfully", data);
    }

    public static <T> APIResponse<T> error(String message) {
        return new APIResponse<>("error", message, null);
    }
}

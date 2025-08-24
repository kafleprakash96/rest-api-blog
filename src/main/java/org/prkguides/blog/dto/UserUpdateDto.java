package org.prkguides.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User profile update request")
public class UserUpdateDto {

    @Email(message = "Email should be valid")
    @Schema(description = "Email address", example = "john@example.com")
    private String email;

    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Schema(description = "First name", example = "John")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    @Schema(description = "User bio")
    private String bio;

    @Schema(description = "Avatar image URL")
    private String avatarUrl;

    @Schema(description = "Website URL")
    private String websiteUrl;
}


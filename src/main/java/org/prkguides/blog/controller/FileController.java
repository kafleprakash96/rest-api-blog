package org.prkguides.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prkguides.blog.dto.APIResponse;
import org.prkguides.blog.dto.FileUploadResponseDto;
import org.prkguides.blog.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@CrossOrigin(origins = {"http://localhost:4200"})
@RequiredArgsConstructor
@Tag(name = "Files", description = "File management endpoints")
public class FileController {

    private final FileService fileService;

    @Operation(summary = "Upload image", description = "Upload an image file. Requires admin privileges.")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<APIResponse<FileUploadResponseDto>> uploadImage(
            @Parameter(description = "Image file to upload")
            @RequestParam("file") MultipartFile file) {

        log.info("File upload attempt: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(APIResponse.error("File is empty"));
        }

        FileUploadResponseDto uploadResponse = fileService.uploadImage(file);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success("File uploaded successfully", uploadResponse));
    }

    @Operation(summary = "Upload multiple images", description = "Upload multiple image files. Requires admin privileges.")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload/multiple")
    public ResponseEntity<APIResponse<List<FileUploadResponseDto>>> uploadMultipleImages(
            @Parameter(description = "Image files to upload")
            @RequestParam("files") MultipartFile[] files) {

        log.info("Multiple file upload attempt: {} files", files.length);

        List<FileUploadResponseDto> uploadResponses = fileService.uploadMultipleImages(files);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success("Files uploaded successfully", uploadResponses));
    }

    @Operation(summary = "Get image", description = "Retrieve an uploaded image")
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(
            @Parameter(description = "Image filename")
            @PathVariable String filename) {

        try {
            Resource resource = fileService.getImage(filename);

            if (resource.exists() && resource.isReadable()) {
                String contentType = fileService.getContentType(filename);

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error retrieving image: {}", filename, e);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete image", description = "Delete an uploaded image. Requires admin privileges.")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/images/{filename:.+}")
    public ResponseEntity<APIResponse<String>> deleteImage(
            @Parameter(description = "Image filename")
            @PathVariable String filename) {

        log.info("Delete image attempt: {}", filename);

        boolean deleted = fileService.deleteImage(filename);

        if (deleted) {
            return ResponseEntity.ok(APIResponse.success("Image deleted successfully", filename));
        } else {
            return ResponseEntity.notFound()
                    .build();
        }
    }

    @Operation(summary = "Get all uploaded images", description = "Get list of all uploaded images. Requires admin privileges.")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/images")
    public ResponseEntity<APIResponse<List<FileUploadResponseDto>>> getAllImages() {

        List<FileUploadResponseDto> images = fileService.getAllImages();
        return ResponseEntity.ok(APIResponse.success("Images retrieved successfully", images));
    }
}

package org.prkguides.blog.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prkguides.blog.dto.FileUploadResponseDto;
import org.prkguides.blog.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${blog.file.upload-dir:uploads}")
    private String uploadDir;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    public FileUploadResponseDto uploadImage(MultipartFile file) {
        log.info("Uploading image: {}", file.getOriginalFilename());

        validateImage(file);

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Generate unique filename
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + "." + fileExtension;

            // Copy file to target location
            Path targetLocation = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("Image uploaded successfully: {}", uniqueFilename);

            return new FileUploadResponseDto(
                    originalFilename,
                    uniqueFilename,
                    "/api/v1/files/images/" + uniqueFilename,
                    file.getSize(),
                    file.getContentType(),
                    LocalDateTime.now()
            );

        } catch (IOException e) {
            log.error("Failed to upload image: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    @Override
    public List<FileUploadResponseDto> uploadMultipleImages(MultipartFile[] files) {
        List<FileUploadResponseDto> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                responses.add(uploadImage(file));
            }
        }

        return responses;
    }

    @Override
    public Resource getImage(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + filename);
            }
        } catch (MalformedURLException e) {
            log.error("File not found: {}", filename, e);
            throw new RuntimeException("File not found: " + filename);
        }
    }

    @Override
    public boolean deleteImage(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filename);
            boolean deleted = Files.deleteIfExists(filePath);

            if (deleted) {
                log.info("Image deleted successfully: {}", filename);
            } else {
                log.warn("Image not found for deletion: {}", filename);
            }

            return deleted;
        } catch (IOException e) {
            log.error("Failed to delete image: {}", filename, e);
            return false;
        }
    }

    @Override
    public List<FileUploadResponseDto> getAllImages() {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

            if (!Files.exists(uploadPath)) {
                return new ArrayList<>();
            }

            try (Stream<Path> files = Files.list(uploadPath)) {
                return files
                        .filter(Files::isRegularFile)
                        .filter(this::isImageFile)
                        .map(this::pathToFileResponse)
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            }
        } catch (IOException e) {
            log.error("Failed to list images", e);
            return new ArrayList<>();
        }
    }

    @Override
    public String getContentType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds maximum allowed size of 5MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new RuntimeException("Invalid filename");
        }

        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new RuntimeException("File type not allowed. Allowed types: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("File is not a valid image");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    private boolean isImageFile(Path path) {
        String filename = path.getFileName().toString();
        String extension = getFileExtension(filename).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    private FileUploadResponseDto pathToFileResponse(Path path) {
        try {
            String filename = path.getFileName().toString();
            long size = Files.size(path);
            String contentType = getContentType(filename);

            return new FileUploadResponseDto(
                    filename,
                    filename,
                    "/api/v1/files/images/" + filename,
                    size,
                    contentType,
                    LocalDateTime.now() // Note: actual creation time would need file attributes
            );
        } catch (IOException e) {
            log.error("Failed to get file info for: {}", path.getFileName(), e);
            return null;
        }
    }
}

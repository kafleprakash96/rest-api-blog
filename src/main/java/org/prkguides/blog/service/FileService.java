package org.prkguides.blog.service;

import org.prkguides.blog.dto.FileUploadResponseDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    FileUploadResponseDto uploadImage(MultipartFile file);
    List<FileUploadResponseDto> uploadMultipleImages(MultipartFile[] files);
    Resource getImage(String filename);
    boolean deleteImage(String filename);
    List<FileUploadResponseDto> getAllImages();
    String getContentType(String filename);
}


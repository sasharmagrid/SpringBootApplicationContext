package com.example.demo.service;

import com.example.demo.models.Image;
import com.example.demo.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    private final String uploadDir = "/Users/sasharma/Documents/GalleryApp/src/main/java/images";

    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElse(null);
    }

    public Image saveImage(Image image) {
        return imageRepository.save(image);
    }

    public boolean deleteImage(Long id) {
        if (imageRepository.existsById(id)) {
            imageRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Image saveFileImage(MultipartFile file) {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            String filePath = uploadDir + "/" + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            Image image = new Image();
            image.setName(file.getOriginalFilename());
            image.setPath(filePath);
            return imageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public Resource loadFileAsResource(Long id) {
        Image image = getImageById(id);
        if (image == null) return null;

        File file = new File(image.getPath());
        if (!file.exists()) return null;

        return new FileSystemResource(file);
    }
}

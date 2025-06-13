package com.example.demo.controller;

import com.example.demo.models.Image;
import com.example.demo.models.Metadata;
import com.example.demo.service.ImageService;
import com.example.demo.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private MetadataService metadataService;

    @GetMapping
    public List<Image> listImages() {
        return imageService.getAllImages();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Image> getImage(@PathVariable Long id) {
        Image image = imageService.getImageById(id);
        return image != null ? ResponseEntity.ok(image) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Image> saveImage(@RequestBody Image image) {
        return ResponseEntity.ok(imageService.saveImage(image));
    }

    @PutMapping("/{id}/metadata")
    public ResponseEntity<String> updateMetadata(@PathVariable Long id, @RequestBody Metadata metadata) {
        boolean updated = metadataService.updateMetadata(id, metadata);
        return updated ? ResponseEntity.ok("Metadata updated") : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable Long id) {
        boolean deleted = imageService.deleteImage(id);
        return deleted ? ResponseEntity.ok("Image deleted") : ResponseEntity.notFound().build();
    }
}

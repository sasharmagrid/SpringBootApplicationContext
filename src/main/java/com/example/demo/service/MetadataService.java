package com.example.demo.service;

import com.example.demo.models.Metadata;
import com.example.demo.repository.MetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MetadataService {

    @Autowired
    private MetadataRepository metadataRepository;

    public boolean updateMetadata(Long imageId, Metadata newMetadata) {
        return metadataRepository.findById(imageId).map(existing -> {
            existing.setLocation(newMetadata.getLocation());
            existing.setWidth(newMetadata.getWidth());
            existing.setHeight(newMetadata.getHeight());
            existing.setModifiedAt(LocalDateTime.now());
            metadataRepository.save(existing);
            return true;
        }).orElse(false);
    }
}
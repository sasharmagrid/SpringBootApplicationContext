package com.example.demo.service;

import com.example.demo.models.Metadata;
import com.example.demo.repository.MetadataRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MetadataServiceTest {

    @Mock
    private MetadataRepository metadataRepository;

    @InjectMocks
    private MetadataService metadataService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateMetadata_Success() {
        Long imageId = 1L;

        Metadata existingMetadata = new Metadata();
//        existingMetadata.setId(imageId);
        existingMetadata.setLocation("Old Location");
        existingMetadata.setWidth(500);
        existingMetadata.setHeight(300);
        existingMetadata.setModifiedAt(LocalDateTime.now().minusDays(1));

        Metadata newMetadata = new Metadata();
        newMetadata.setLocation("New Location");
        newMetadata.setWidth(800);
        newMetadata.setHeight(600);

        when(metadataRepository.findById(imageId)).thenReturn(Optional.of(existingMetadata));

        boolean result = metadataService.updateMetadata(imageId, newMetadata);

        assertThat(result).isTrue();
        assertThat(existingMetadata.getLocation()).isEqualTo("New Location");
        assertThat(existingMetadata.getWidth()).isEqualTo(800);
        assertThat(existingMetadata.getHeight()).isEqualTo(600);
        assertThat(existingMetadata.getModifiedAt()).isNotNull();

        verify(metadataRepository, times(1)).findById(imageId);
        verify(metadataRepository, times(1)).save(existingMetadata);
    }

    @Test
    public void testUpdateMetadata_NotFound() {
        Long imageId = 1L;

        Metadata newMetadata = new Metadata();
        newMetadata.setLocation("New Location");
        newMetadata.setWidth(800);
        newMetadata.setHeight(600);

        when(metadataRepository.findById(imageId)).thenReturn(Optional.empty());

        boolean result = metadataService.updateMetadata(imageId, newMetadata);

        assertThat(result).isFalse();
        verify(metadataRepository, times(1)).findById(imageId);
        verify(metadataRepository, never()).save(any(Metadata.class));
    }
}

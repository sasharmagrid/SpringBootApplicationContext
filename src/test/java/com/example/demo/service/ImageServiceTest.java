package com.example.demo.service;

import com.example.demo.models.Image;
import com.example.demo.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ImageServiceTest {

    @Autowired
    private ImageService imageService;

    @MockBean
    private ImageRepository imageRepository;

    @BeforeEach
    public void setUp() {
        // No need to initialize mocks, Spring does it automatically
    }

    @Test
    void testSaveFileImage() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "dummy image content".getBytes()
        );

        Image savedImage = new Image();
        savedImage.setId(1L);
        savedImage.setName("test.jpg");

        when(imageRepository.save(any(Image.class))).thenReturn(savedImage);

        Image result = imageService.saveFileImage(mockFile);

        assertThat(result).isNotNull();
        verify(imageRepository, times(1)).save(any(Image.class));
    }

    @Test
    void testGetImageById() {
        Image image = new Image();
        image.setId(1L);
        image.setName("test.jpg");

        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));

        Image result = imageService.getImageById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("test.jpg");
    }

    @Test
    void testSaveImage() {
        Image image = new Image();
        image.setName("test.jpg");

        when(imageRepository.save(any(Image.class))).thenReturn(image);

        Image savedImage = imageService.saveImage(image);

        assertThat(savedImage).isNotNull();
        assertThat(savedImage.getName()).isEqualTo("test.jpg");
        verify(imageRepository, times(1)).save(image);
    }

    @Test
    void testDeleteImage_Success() {
        Long imageId = 1L;

        when(imageRepository.existsById(imageId)).thenReturn(true);
        doNothing().when(imageRepository).deleteById(imageId);

        boolean result = imageService.deleteImage(imageId);

        assertThat(result).isTrue();
        verify(imageRepository, times(1)).existsById(imageId);
        verify(imageRepository, times(1)).deleteById(imageId);
    }

    @Test
    void testDeleteImage_Failure() {
        Long imageId = 1L;

        when(imageRepository.existsById(imageId)).thenReturn(false);

        boolean result = imageService.deleteImage(imageId);

        assertThat(result).isFalse();
        verify(imageRepository, times(1)).existsById(imageId);
        verify(imageRepository, never()).deleteById(imageId);
    }

    @Test
    void testGetAllImages() {
        List<Image> images = Arrays.asList(new Image(), new Image());

        when(imageRepository.findAll()).thenReturn(images);

        List<Image> result = imageService.getAllImages();

        assertThat(result).hasSize(2);
        verify(imageRepository, times(1)).findAll();
    }

    @Test
    void testLoadFileAsResource_Success() {
        Long imageId = 1L;
        String filePath = "src/test/resources/test-image.jpg";

        File file = new File(filePath);
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (Exception e) {
            throw new RuntimeException("Could not create test file");
        }

        Image image = new Image();
        image.setId(imageId);
        image.setPath(filePath);

        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));

        Resource resource = imageService.loadFileAsResource(imageId);

        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();

        file.delete(); // Clean up
    }

    @Test
    void testLoadFileAsResource_ImageNotFound() {
        Long imageId = 1L;

        when(imageRepository.findById(imageId)).thenReturn(Optional.empty());

        Resource resource = imageService.loadFileAsResource(imageId);

        assertThat(resource).isNull();
    }
}

package com.example.demo.controller;

import com.example.demo.models.Image;
import com.example.demo.service.ImageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @Test
    void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "dummy image content".getBytes()
        );

        Image image = new Image();
        image.setId(1L);
        image.setName("test-image.jpg");

        Mockito.when(imageService.saveFileImage(any())).thenReturn(image);

        mockMvc.perform(multipart("/files/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test-image.jpg"));
    }

    @Test
    void testDownloadFile_Success() throws Exception {
        byte[] fileContent = "dummy image content".getBytes();
        Resource resource = new ByteArrayResource(fileContent);

        Mockito.when(imageService.loadFileAsResource(1L)).thenReturn(resource);

        mockMvc.perform(get("/files/download/1"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(fileContent))
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    void testDownloadFile_NotFound() throws Exception {
        Mockito.when(imageService.loadFileAsResource(1L)).thenReturn(null);

        mockMvc.perform(get("/files/download/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testViewImage_Success() throws Exception {
        byte[] fileContent = "dummy image content".getBytes();
        Resource resource = new ByteArrayResource(fileContent);

        Mockito.when(imageService.loadFileAsResource(1L)).thenReturn(resource);

        mockMvc.perform(get("/files/view/1"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(fileContent))
                .andExpect(header().exists("Content-Type")); // Content-Type should resolve dynamically
    }

    @Test
    void testViewImage_NotFound() throws Exception {
        Mockito.when(imageService.loadFileAsResource(1L)).thenReturn(null);

        mockMvc.perform(get("/files/view/1"))
                .andExpect(status().isNotFound());
    }
}

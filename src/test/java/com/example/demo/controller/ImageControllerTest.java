package com.example.demo.controller;

import com.example.demo.models.Image;
import com.example.demo.models.Metadata;
import com.example.demo.service.ImageService;
import com.example.demo.service.MetadataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageController.class)
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @MockBean
    private MetadataService metadataService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testListImages() throws Exception {
        Image image1 = new Image();
        image1.setId(1L);
        image1.setName("Image1");

        Image image2 = new Image();
        image2.setId(2L);
        image2.setName("Image2");

        List<Image> images = Arrays.asList(image1, image2);

        Mockito.when(imageService.getAllImages()).thenReturn(images);

        mockMvc.perform(get("/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Image1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Image2"));
    }

    @Test
    void testGetImage_Found() throws Exception {
        Image image = new Image();
        image.setId(1L);
        image.setName("Test Image");

        Mockito.when(imageService.getImageById(1L)).thenReturn(image);

        mockMvc.perform(get("/images/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Image"));
    }

    @Test
    void testGetImage_NotFound() throws Exception {
        Mockito.when(imageService.getImageById(1L)).thenReturn(null);

        mockMvc.perform(get("/images/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSaveImage() throws Exception {
        Image image = new Image();
        image.setId(1L);
        image.setName("New Image");

        Mockito.when(imageService.saveImage(any(Image.class))).thenReturn(image);

        mockMvc.perform(post("/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(image)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New Image"));
    }

    @Test
    void testUpdateMetadata_Success() throws Exception {
        Metadata metadata = new Metadata();
        metadata.setLocation("New Location");

        Mockito.when(metadataService.updateMetadata(eq(1L), any(Metadata.class))).thenReturn(true);

        mockMvc.perform(put("/images/1/metadata")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metadata)))
                .andExpect(status().isOk())
                .andExpect(content().string("Metadata updated"));
    }

    @Test
    void testUpdateMetadata_NotFound() throws Exception {
        Metadata metadata = new Metadata();
        metadata.setLocation("New Location");

        Mockito.when(metadataService.updateMetadata(eq(1L), any(Metadata.class))).thenReturn(false);

        mockMvc.perform(put("/images/1/metadata")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(metadata)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteImage_Success() throws Exception {
        Mockito.when(imageService.deleteImage(1L)).thenReturn(true);

        mockMvc.perform(delete("/images/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Image deleted"));
    }

    @Test
    void testDeleteImage_NotFound() throws Exception {
        Mockito.when(imageService.deleteImage(1L)).thenReturn(false);

        mockMvc.perform(delete("/images/1"))
                .andExpect(status().isNotFound());
    }
}

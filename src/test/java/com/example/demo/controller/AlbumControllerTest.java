package com.example.demo.controller;

import com.example.demo.models.Album;
import com.example.demo.service.AlbumService;
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

@WebMvcTest(AlbumController.class)
public class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AlbumService albumService;

    @Test
    void testGetAllAlbums() throws Exception {
        Album album1 = new Album();
        album1.setName("Album1");

        Album album2 = new Album();
        album2.setName("Album2");

        Mockito.when(albumService.listAlbums()).thenReturn(Arrays.asList(album1, album2));

        mockMvc.perform(get("/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Album1"))
                .andExpect(jsonPath("$[1]").value("Album2"));
    }

    @Test
    void testCreateAlbum() throws Exception {
        Mockito.doNothing().when(albumService).createAlbum("New Album");

        mockMvc.perform(post("/albums")
                        .param("name", "New Album"))
                .andExpect(status().isOk())
                .andExpect(content().string("Created album: New Album"));

        Mockito.verify(albumService).createAlbum("New Album");
    }

    @Test
    void testAddImages_Success() throws Exception {
        Long albumId = 1L;
        List<Long> imageIds = List.of(101L, 102L);

        Mockito.when(albumService.addImagesToAlbum(eq(albumId), any())).thenReturn(true);

        mockMvc.perform(post("/albums/{albumId}/images", albumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[101, 102]"))
                .andExpect(status().isOk())
                .andExpect(content().string("Images added to album 1"));
    }

    @Test
    void testAddImages_AlbumNotFound() throws Exception {
        Long albumId = 1L;

        Mockito.when(albumService.addImagesToAlbum(eq(albumId), any())).thenReturn(false);

        mockMvc.perform(post("/albums/{albumId}/images", albumId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[101, 102]"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Album not found: 1"));
    }
}

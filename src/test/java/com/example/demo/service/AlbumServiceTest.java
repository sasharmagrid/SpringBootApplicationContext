package com.example.demo.service;

import com.example.demo.models.Album;
import com.example.demo.models.Image;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.ImageRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private AlbumService albumService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListAlbums() {
        Album album1 = new Album();
        album1.setId(1L);
        album1.setName("Vacation");

        Album album2 = new Album();
        album2.setId(2L);
        album2.setName("Family");

        when(albumRepository.findAll()).thenReturn(Arrays.asList(album1, album2));

        List<Album> albums = albumService.listAlbums();

        assertThat(albums).hasSize(2);
        assertThat(albums.get(0).getName()).isEqualTo("Vacation");
        assertThat(albums.get(1).getName()).isEqualTo("Family");

        verify(albumRepository, times(1)).findAll();
    }

    @Test
    public void testCreateAlbum() {
        String albumName = "Birthday";

        albumService.createAlbum(albumName);

        ArgumentCaptor<Album> albumCaptor = ArgumentCaptor.forClass(Album.class);
        verify(albumRepository, times(1)).save(albumCaptor.capture());

        Album savedAlbum = albumCaptor.getValue();
        assertThat(savedAlbum.getName()).isEqualTo("Birthday");
    }

    @Test
    public void testAddImagesToAlbum_Success() {
        Long albumId = 1L;
        List<Long> imageIds = Arrays.asList(10L, 20L);

        Album album = new Album();
        album.setId(albumId);
        album.setImages(new ArrayList<>());

        Image image1 = new Image();
        image1.setId(10L);

        Image image2 = new Image();
        image2.setId(20L);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(imageRepository.findAllById(imageIds)).thenReturn(Arrays.asList(image1, image2));

        boolean result = albumService.addImagesToAlbum(albumId, imageIds);

        assertThat(result).isTrue();
        assertThat(album.getImages()).containsExactlyInAnyOrder(image1, image2);

        verify(albumRepository, times(1)).save(album);
        verify(imageRepository, times(1)).findAllById(imageIds);
    }

    @Test
    public void testAddImagesToAlbum_AlbumNotFound() {
        Long albumId = 99L;
        List<Long> imageIds = Arrays.asList(10L, 20L);

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        boolean result = albumService.addImagesToAlbum(albumId, imageIds);

        assertThat(result).isFalse();
        verify(albumRepository, never()).save(any(Album.class));
    }
}

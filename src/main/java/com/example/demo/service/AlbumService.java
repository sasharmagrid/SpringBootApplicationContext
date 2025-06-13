package com.example.demo.service;

import com.example.demo.models.Album;
import com.example.demo.models.Image;
import com.example.demo.repository.AlbumRepository;
import com.example.demo.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ImageRepository imageRepository;

    public List<Album> listAlbums() {
        return albumRepository.findAll();
    }

    public void createAlbum(String name) {
        Album album = new Album();
        album.setName(name);
        albumRepository.save(album);
    }

    public boolean addImagesToAlbum(Long albumId, List<Long> imageIds) {
        return albumRepository.findById(albumId).map(album -> {
            List<Image> images = imageRepository.findAllById(imageIds);
            album.getImages().addAll(images);
            albumRepository.save(album);
            return true;
        }).orElse(false);
    }
}
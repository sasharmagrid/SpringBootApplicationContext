package com.example.demo.controller;

import com.example.demo.models.Album;
import com.example.demo.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @GetMapping
    public List<String> getAllAlbums() {
        return albumService.listAlbums().stream().map(Album::getName).toList();
    }

    @PostMapping
    public ResponseEntity<String> createAlbum(@RequestParam String name) {
        albumService.createAlbum(name);
        return ResponseEntity.ok("Created album: " + name);
    }

    @PostMapping("/{albumId}/images")
    public ResponseEntity<String> addImages(@PathVariable Long albumId, @RequestBody List<Long> imageIds) {
        boolean res = albumService.addImagesToAlbum(albumId, imageIds);
        return res ? ResponseEntity.ok("Images added to album " + albumId) : ResponseEntity.badRequest().body("Album not found: " + albumId);
    }
}

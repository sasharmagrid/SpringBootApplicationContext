package com.example.demo.controller;

import com.example.demo.models.Image;
import com.example.demo.repository.ImageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ImageRepository imageRepository;

    @Test
    public void testImageUploadAndView() throws IOException {
        // Prepare file
        ClassPathResource resource = new ClassPathResource("test-image.png");

        byte[] fileContent = Files.readAllBytes(resource.getFile().toPath());

        // Prepare multipart request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<byte[]> filePart = new HttpEntity<>(fileContent, headers);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", filePart);

        ResponseEntity<Image> uploadResponse = restTemplate.postForEntity("/files/upload", body, Image.class);

//        assertThat(uploadResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(uploadResponse.getBody()).isNotNull();

        Long imageId = uploadResponse.getBody().getId();

        // Test view endpoint
        ResponseEntity<byte[]> viewResponse = restTemplate.getForEntity("/files/view/" + imageId, byte[].class);
//        assertThat(viewResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(viewResponse.getHeaders().getContentType()).isNotNull();
        assertThat(viewResponse.getBody()).isNotEmpty();
    }
}

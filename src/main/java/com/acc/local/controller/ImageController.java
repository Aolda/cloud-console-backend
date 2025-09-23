package com.acc.local.controller;

import com.acc.local.dto.image.CreateImageRequestDto;
import com.acc.local.dto.image.CreateImageResponseDto;
import com.acc.local.dto.image.ImageResponse;
import com.acc.local.service.ports.ImageServicePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class ImageController {

    private final ImageServicePort imageServicePort;
    @Autowired
    private ObjectMapper objectMapper;
    @GetMapping
    public List<ImageResponse> getImages(@RequestHeader("X-Auth-Token") String token) {
        return imageServicePort.getImages(token);
    }

    @GetMapping("/{id}")
    public ImageResponse getImageDetail(@RequestHeader("X-Auth-Token") String token,
                                        @PathVariable("id") String imageId) {
        return imageServicePort.getImageDetail(token, imageId);
    }

    @GetMapping("/public")
    public List<ImageResponse> getPublicImages(@RequestHeader("X-Auth-Token") String token) {
        return imageServicePort.getPublicImages(token);
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CreateImageResponseDto createImage(
            @RequestHeader("X-Auth-Token") String token,
            @Parameter(description = """
        JSON 문자열 형태로 metadata를 전달해야 합니다.
        """)
            @RequestPart("metadata") String metadata,
            @RequestPart("file") MultipartFile file
    ) {
        return imageServicePort.createImage(token, metadata, file);
    }
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteProjectImage(@RequestHeader("X-Auth-Token") String token,
                                                   @PathVariable String imageId) {
        imageServicePort.deleteProjectImage(token, imageId);
        return ResponseEntity.noContent().build();
    }
}

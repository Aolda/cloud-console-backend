package com.acc.local.controller;

import com.acc.local.dto.image.ImageResponse;
import com.acc.local.service.ports.ImagePort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class ImageController {

    private final ImagePort imagePort;

    @GetMapping
    public List<ImageResponse> getImages(@RequestHeader("X-Auth-Token") String token) {
        return imagePort.getImages(token);
    }

    @GetMapping("/{id}")
    public ImageResponse getImageDetail(@RequestHeader("X-Auth-Token") String token,
                                        @PathVariable("id") String imageId) {
        return imagePort.getImageDetail(token, imageId);
    }
}

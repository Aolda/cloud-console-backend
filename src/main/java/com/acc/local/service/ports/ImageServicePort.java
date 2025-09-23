package com.acc.local.service.ports;

import com.acc.local.dto.image.CreateImageRequestDto;
import com.acc.local.dto.image.CreateImageResponseDto;
import com.acc.local.dto.image.ImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageServicePort {
    List<ImageResponse> getImages(String token);
    ImageResponse getImageDetail(String token, String imageId);
    List<ImageResponse> getPublicImages(String token);
    void deleteProjectImage(String token, String imageId);
    CreateImageResponseDto createImage(String token, String metadata, MultipartFile file);
}

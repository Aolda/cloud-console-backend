package com.acc.local.service.ports;

import com.acc.local.dto.image.ImageResponse;

import java.util.List;

public interface ImagePort {
    List<ImageResponse> getImages(String token);
    ImageResponse getImageDetail(String token, String imageId);

}

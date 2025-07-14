package com.acc.local.service.adapters.image;

import com.acc.local.service.modules.image.ImageModule;
import com.acc.local.service.ports.ImagePort;
import com.acc.local.dto.image.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Primary
@RequiredArgsConstructor
public class ImageAdapter implements ImagePort {
    private final ImageModule imageModule;

    @Override
    public List<ImageResponse> getImages(String token) {
        return imageModule.getImages(token);
    }

    @Override
    public ImageResponse getImageDetail(String token, String imageId) {
        return imageModule.getImageDetail(token, imageId);
    }
}

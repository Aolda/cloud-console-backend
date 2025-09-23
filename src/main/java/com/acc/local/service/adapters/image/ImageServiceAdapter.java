package com.acc.local.service.adapters.image;
import com.acc.global.exception.image.ImageErrorCode;
import com.acc.global.exception.image.ImageException;
import com.acc.local.dto.image.CreateImageRequestDto;
import com.acc.local.dto.image.CreateImageResponseDto;
import com.acc.local.service.modules.image.ImageModule;
import com.acc.local.service.ports.ImageServicePort;
import com.acc.local.dto.image.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@Service
@Primary
@RequiredArgsConstructor
public class ImageServiceAdapter implements ImageServicePort {
    private final ImageModule imageModule;

    @Override
    public List<ImageResponse> getImages(String token) {
        return imageModule.getImages(token);
    }

    @Override
    public ImageResponse getImageDetail(String token, String imageId) {
        return imageModule.getImageDetail(token, imageId);
    }

    @Override
    public List<ImageResponse> getPublicImages(String token){
        return imageModule.getPublicImages(token);
    }

    @Override
    public void deleteProjectImage(String token,String imageId) {
        imageModule.deleteProjectImage(token,imageId);
    }

    @Override
    public CreateImageResponseDto createImage(String token, String metadata, MultipartFile file) {
        try {
            return imageModule.createImage(token, metadata, file);
        } catch (IOException e) {
            throw new ImageException(ImageErrorCode.IMAGE_CREATION_FAILURE, e);
        }
    }



}

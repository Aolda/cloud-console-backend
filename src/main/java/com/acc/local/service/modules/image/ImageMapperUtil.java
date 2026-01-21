package com.acc.local.service.modules.image;

import com.acc.global.exception.image.ImageErrorCode;
import com.acc.global.exception.image.ImageException;
import com.acc.local.dto.image.*;
import com.acc.local.external.dto.glance.response.GlanceImageResponse;
import com.acc.local.external.dto.glance.response.GlanceImagesResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class ImageMapperUtil {

    public List<GlanceImageSummary> toImageListResponse(GlanceImagesResponse glanceResponse) {
        try {
            if (glanceResponse == null || glanceResponse.getImages() == null) {
                throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA);
            }
            List<GlanceImageSummary> list = new ArrayList<>();

            for (GlanceImagesResponse.Image img : glanceResponse.getImages()) {
                list.add(
                        GlanceImageSummary.builder()
                                .id(img.getId())
                                .name(img.getName())
                                .architecture(null) // Not in GlanceImagesResponse.Image
                                .projectName(null)
                                .description(null)
                                .diskFormat(img.getDiskFormat())
                                .status(img.getStatus())
                                .visibility(img.getVisibility())
                                .size(img.getSize())
                                .hidden(null) // Not in GlanceImagesResponse.Image
                                .minDisk(null) // Not in GlanceImagesResponse.Image
                                .minRam(null) // Not in GlanceImagesResponse.Image
                                .createdAt(img.getCreatedAt())
                                .build()
                );
            }
            return list;
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA, e);
        }
    }

    public List<GlanceImageSummary> sortGlanceImageSummary(List<GlanceImageSummary> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        List<GlanceImageSummary> sorted =
                new ArrayList<>(list);

        sorted.sort(Comparator.comparing(
                img -> Instant.parse(img.createdAt()),
                Comparator.reverseOrder()
        ));

        return sorted;
    }


    public ImageDetailResponse toImageDetailResponse(GlanceImageResponse glanceImage) {
        try {
            if (glanceImage == null) {
                throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA);
            }

            return ImageDetailResponse.builder()
                    .id(glanceImage.getId())
                    .name(glanceImage.getName())
                    .architecture(null) // Can add if needed: glanceImage.getArchitecture()
                    .projectName(null)
                    .description(null)
                    .diskFormat(glanceImage.getDiskFormat())
                    .status(glanceImage.getStatus())
                    .visibility(glanceImage.getVisibility())
                    .size(glanceImage.getSize())
                    .hidden(glanceImage.getOsHidden())
                    .minDisk(glanceImage.getMinDisk())
                    .minRam(glanceImage.getMinRam())
                    .createdAt(glanceImage.getCreatedAt())
                    .updatedAt(glanceImage.getUpdatedAt())
                    .tags(glanceImage.getTags())
                    .build();
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA, e);
        }
    }

    public ImageUploadAckResponse toUploadAck(GlanceImageResponse glanceImage) {
        try {
            if (glanceImage == null || glanceImage.getId() == null) {
                throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA);
            }

            return ImageUploadAckResponse.builder()
                    .imageId(glanceImage.getId())
                    .name(glanceImage.getName())
                    .status(glanceImage.getStatus())
                    .message("Image metadata created")
                    .build();
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA, e);
        }
    }
}

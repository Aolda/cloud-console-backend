package com.acc.local.service.modules.image;

import com.acc.global.exception.image.ImageErrorCode;
import com.acc.global.exception.image.ImageException;
import com.acc.local.dto.image.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
public class ImageJsonMapperModule {

    public List<GlanceImageSummary> toImageListResponse(JsonNode json) {
        try {
            if (json == null) throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA);
            List<GlanceImageSummary> list = new ArrayList<>();

            JsonNode imagesNode = json.get("images");
            if (imagesNode != null && imagesNode.isArray()) {
                for (JsonNode img : imagesNode) {
                    list.add(
                            GlanceImageSummary.builder()
                                    .id(text(img, "id"))
                                    .name(text(img, "name"))
                                    .architecture(text(img, "architecture"))
                                    .projectName(null)
                                    .description(null)
                                    .diskFormat(text(img, "disk_format"))
                                    .status(text(img, "status"))
                                    .visibility(text(img, "visibility"))
                                    .size(longOrNull(img, "size"))
                                    .hidden(boolOrNull(img, "os_hidden"))
                                    .minDisk(intOrNull(img, "min_disk"))
                                    .minRam(intOrNull(img, "min_ram"))
                                    .createdAt(text(img, "created_at"))
                                    .build()
                    );
                }
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


    public ImageDetailResponse toImageDetailResponse(JsonNode json) {
        try {
            if (json == null) throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA);

            return ImageDetailResponse.builder()
                    .id(text(json, "id"))
                    .name(text(json, "name"))
                    .architecture(text(json, "architecture"))
                    .projectName(null)
                    .description(null)
                    .diskFormat(text(json, "disk_format"))
                    .status(text(json, "status"))
                    .visibility(text(json, "visibility"))
                    .size(longOrNull(json, "size"))
                    .hidden(boolOrNull(json, "os_hidden"))
                    .minDisk(intOrNull(json, "min_disk"))
                    .minRam(intOrNull(json, "min_ram"))
                    .createdAt(text(json, "created_at"))
                    .updatedAt(text(json, "updated_at"))
                    .tags(listOrNull(json.get("tags")))
                    .build();
        } catch (Exception e) {
            System.out.println("파서");
            throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA, e);
        }
    }

    public ImageUploadAckResponse toUploadAck(JsonNode node) {
        try {
            if (node == null || node.get("id") == null) throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA);

            return ImageUploadAckResponse.builder()
                    .imageId(node.get("id").asText())
                    .name(node.get("name").asText())
                    .status(node.get("status") != null ? node.get("status").asText() : null)
                    .message("Image metadata created")
                    .build();
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA, e);
        }
    }

    private String text(JsonNode node, String field) {
        if (node == null) return null;
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asText();
    }

    private Long longOrNull(JsonNode node, String field) {
        if (node == null) return null;
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asLong();
    }

    private Integer intOrNull(JsonNode node, String field) {
        if (node == null) return null;
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asInt();
    }

    private List<String> listOrNull(JsonNode node) {
        if (node == null || !node.isArray()) return null;

        List<String> list = new ArrayList<>();
        for (JsonNode n : node) {
            if (n != null && !n.isNull()) list.add(n.asText());
        }
        return list;
    }
    private Boolean boolOrNull(JsonNode node, String field) {
        if (node == null) return null;
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asBoolean();
    }
}

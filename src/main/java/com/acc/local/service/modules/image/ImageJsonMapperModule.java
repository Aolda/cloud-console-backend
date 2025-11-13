package com.acc.local.service.modules.image;

import com.acc.local.dto.image.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ImageJsonMapperModule {

    public ImageListResponse toImageListResponse(JsonNode json) {
        List<ImageListResponse.GlanceImageSummary> list = new ArrayList<>();

        JsonNode imagesNode = json.get("images");
        if (imagesNode != null && imagesNode.isArray()) {
            for (JsonNode img : imagesNode) {
                list.add(
                        ImageListResponse.GlanceImageSummary.builder()
                                .id(text(img, "id"))
                                .name(text(img, "name"))
                                // Glance에서 projectName 없음 → service 계층에서 보강
                                .projectName(null)
                                // Glance description 없음 → 추후 custom metadata 붙이면 채움
                                .description(null)
                                .diskFormat(text(img, "disk_format"))
                                .status(text(img, "status"))
                                .visibility(text(img, "visibility"))
                                .size(longOrNull(img, "size"))
                                .minDisk(intOrNull(img, "min_disk"))
                                .minRam(intOrNull(img, "min_ram"))
                                .createdAt(text(img, "created_at"))
                                .build()
                );
            }
        }

        return ImageListResponse.builder()
                .images(list)
                .build();
    }

    public ImageDetailResponse toImageDetailResponse(JsonNode json) {

        return ImageDetailResponse.builder()
                .id(text(json, "id"))
                .name(text(json, "name"))

                // service 계층에서 보강 예정
                .projectName(null)

                // Glance 기본 응답에는 description 없음
                .description(null)

                .diskFormat(text(json, "disk_format"))
                .status(text(json, "status"))
                .visibility(text(json, "visibility"))
                .size(longOrNull(json, "size"))
                .minDisk(intOrNull(json, "min_disk"))
                .minRam(intOrNull(json, "min_ram"))
                .createdAt(text(json, "created_at"))
                .updatedAt(text(json, "updated_at"))
                .tags(listOrNull(json.get("tags")))
                .build();
    }

    private String text(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asText();
    }

    private Long longOrNull(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asLong();
    }

    private Integer intOrNull(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asInt();
    }

    private List<String> listOrNull(JsonNode node) {
        if (node == null || !node.isArray()) return null;

        List<String> list = new ArrayList<>();
        for (JsonNode n : node) {
            if (n != null && !n.isNull()) {
                list.add(n.asText());
            }
        }
        return list;
    }

}

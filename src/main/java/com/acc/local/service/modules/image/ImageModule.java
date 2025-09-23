package com.acc.local.service.modules.image;
import com.acc.global.exception.image.ImageErrorCode;
import com.acc.global.exception.image.ImageException;
import com.acc.local.dto.image.CreateImageRequestDto;
import com.acc.local.dto.image.CreateImageResponseDto;
import com.acc.local.dto.image.ImageResponse;
import com.acc.local.service.modules.auth.AuthModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageModule {

    private final WebClient glanceWebClient;
    private final AuthModule authModule;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<ImageResponse> getImages(String token) {
        return glanceWebClient.get()
                .uri("/image/v2/images")
                .header("X-Auth-Token", token)
                .header("Content-Type", "application/json")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(root -> {
                    List<ImageResponse> list = new ArrayList<>();
                    root.get("images").forEach(node -> list.add(parseImage(node)));
                    return list;
                })
                .block();
    }

    public ImageResponse getImageDetail(String token, String imageId) {
        return glanceWebClient.get()
                .uri("/image/v2/images/{id}", imageId)
                .header("X-Auth-Token", token)
                .header("Content-Type", "application/json")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::parseImage)
                .block();
    }

    public List<ImageResponse> getPublicImages(String token) {
        return getImages(token).stream()
                .filter(image -> image.visibility().equals("public"))
                .toList();
    }

    public void deleteProjectImage(String token, String imageId) {
        ImageResponse image = getImageDetail(token, imageId);
        String projectId = authModule.getProjectIdFromToken(token);
        boolean isOwner = Objects.equals(projectId, image.owner());
        List<String> roles = authModule.getRolesFromToken(token);
        boolean isAdminOrManager = roles.contains("admin") || roles.contains("manager");

        if (!isAdminOrManager) {
            if (!"private".equalsIgnoreCase(image.visibility())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비공개(private) 이미지만 삭제할 수 있습니다.");
            }

            if (!isOwner) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 프로젝트 소속 이미지가 아닙니다.");
            }
        }

        glanceWebClient.delete()
                .uri("/image/v2/images/{id}", imageId)
                .header("X-Auth-Token", token)
                .header("Content-Type", "application/json")
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    private ImageResponse parseImage(JsonNode node) {
        return new ImageResponse(
                node.get("id").asText(),
                node.get("name").asText(),
                node.get("status").asText(),
                node.get("visibility").asText(),
                node.hasNonNull("size") ? node.get("size").asLong() : null,
                node.get("disk_format").asText(),
                node.get("container_format").asText(),
                node.get("created_at").asText(),
                node.get("updated_at").asText(),
                node.get("owner").asText()

        );
    }

    public CreateImageResponseDto createImage(String token, String request, MultipartFile file) throws IOException {

        String imageId = createImageMetaData(token, request);
        uploadImageFile(token, imageId, file);
        return fetchImageDetail(token, imageId);
    }

    private String createImageMetaData(String token, String requestJson) {
        try {
            // 1. String → DTO (역직렬화)
            CreateImageRequestDto request = objectMapper.readValue(requestJson, CreateImageRequestDto.class);

            JsonNode created = glanceWebClient.post()
                    .uri("/image/v2/images")
                    .header("X-Auth-Token", token)
                    .header("Content-Type", "application/json")
                    .bodyValue(request.toMap())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            return created.path("id").asText();

        } catch (JsonProcessingException e) {
            throw new ImageException(ImageErrorCode.INVALID_IMAGE_METADATA, e);
        }
    }
    private void uploadImageFile(String token, String imageId, MultipartFile file) {
        try {
            InputStreamResource resource = new InputStreamResource(file.getInputStream()) {
                @Override
                public long contentLength() throws IOException {
                    return file.getSize();
                }
            };

            glanceWebClient.put()
                    .uri("/image/v2/images/{imageId}/file", imageId)
                    .header("X-Auth-Token", token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .bodyValue(resource)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

        } catch (IOException e) {
            throw new ImageException(ImageErrorCode.IMAGE_UPLOAD_FAILURE, e);
        }
    }


    private CreateImageResponseDto fetchImageDetail(String token, String imageId) {
        //3단계. 이미지 상세조회
        JsonNode detail = glanceWebClient.get()
                .uri("/image/v2/images/{imageId}", imageId)
                .header("X-Auth-Token", token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return new CreateImageResponseDto(
                detail.path("id").asText(),
                detail.path("name").asText(),
                detail.path("status").asText(),
                detail.path("disk_format").asText(),
                detail.path("container_format").asText(),
                detail.path("visibility").asText(),
                detail.path("protected").asBoolean(),
                detail.path("min_ram").asInt(),
                detail.path("min_disk").asInt(),
                detail.path("size").asLong(),
                detail.path("virtual_size").asLong(),
                detail.path("checksum").asText(),
                detail.path("owner").asText(),
                detail.path("created_at").asText(),
                detail.path("updated_at").asText(),
                detail.path("os_hidden").asBoolean(),
                detail.path("os_hash_algo").asText(),
                detail.path("os_hash_value").asText()
        );
    }


}

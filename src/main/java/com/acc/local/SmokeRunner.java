package com.acc.local;

import com.acc.local.external.dto.glance.image.CreateImageRequest;
import com.acc.local.external.dto.glance.image.FetchImagesRequestParam;
import com.acc.local.external.dto.glance.image.ImportImageRequest;
import com.acc.local.external.dto.glance.image.UpdateImageRequest;
import com.acc.local.external.modules.OpenstackAPICallModule;
import com.acc.local.external.modules.glance.GlanceImageAPIModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// 앱 시작 직후 실행. 실패/성공만 눈으로 확인하고 종료.
@Component
@RequiredArgsConstructor
@Order(1)
public class SmokeRunner implements CommandLineRunner {

    // SMOKE=1 일 때만 실행 (기본 off)
    private boolean enabled = true;
    private final GlanceImageAPIModule glance;

    @Override
    public void run(String... args) throws Exception {
        if (!enabled) return;

        String token = "gAAAAABpCaGXgMg9K1Y59k0BnSvObd88BX__rPPx-NUIUxfV1dx9BvACzOuyfUeHjn47JbGlE_X4r1HKX-gB9S825NV5xdY0OAfhOPQDaF1ODSrvhwo77A1DY6cni2YzGUyOm6aXOhI3grbikKQjHI326IpKYyflVgVU5MDy_iTjiYm5lBC3RTk";

//        try {
//            ResponseEntity<JsonNode> res = glance.fetchImageList(token, FetchImagesRequestParam.builder().build());
//            System.out.println("[SMOKE] fetchImageList OK");
//            System.out.println(res.getBody().toPrettyString());
//        } catch (Exception e) {
//            System.out.println("[SMOKE] fetchImageList FAIL");
//            e.printStackTrace(System.out);
//        }
        // fetchImage
//        try {
//            ResponseEntity<JsonNode> res = glance.fetchImage(token, "ca11a35b-f858-438b-8705-1773752fea1a");
//            System.out.println("[SMOKE] fetchImage OK");
//            System.out.println(res.getBody().toPrettyString());
//        } catch (Exception e) {
//            System.out.println("[SMOKE] fetchImage FAIL");
//            e.printStackTrace(System.out);
//        }
//
        // fetchImageTask
        try {
            ResponseEntity<JsonNode> res = glance.fetchImageTask(token, "63bf0789-169f-4b56-b7fd-b7643c575f8e ");
            System.out.println("[SMOKE] fetchImageTask OK");
            System.out.println(res.getBody().toPrettyString());
        } catch (Exception e) {
            System.out.println("[SMOKE] fetchImageTask FAIL");
            e.printStackTrace(System.out);
        }

//        // updateImage
//        ResponseEntity<JsonNode> updateRes = null;
//        try {
//            UpdateImageRequest req = new UpdateImageRequest();
//
//            req.add(UpdateImageRequest.Operation.builder()
//                    .op("replace")
//                    .path("/name")
//                    .value("smoke-import-ubuntu-2404-updated")
//                    .build());
//
//            List<String> tags = List.of("smoke", "web-download");
//            req.add(UpdateImageRequest.Operation.builder()
//                    .op("add")
//                    .path("/tags")
//                    .value(tags)
//                    .build());
//
//
//            updateRes = glance.updateImage(token, "ca11a35b-f858-438b-8705-1773752fea1a", req);
//            System.out.println("[SMOKE] updateImage OK");
//            if (updateRes.getBody() != null) System.out.println(updateRes.getBody().toPrettyString());
//        } catch (Exception e) {
//            System.out.println("[SMOKE] updateImage FAIL");
//            if (updateRes != null) {
//                System.out.println("Status = " + updateRes.getStatusCode());
//                System.out.println("Body = " + updateRes.getBody());
//            }
//            e.printStackTrace(System.out);
//        }

        //createImage
//        try {
//            CreateImageRequest req = CreateImageRequest.builder()
//                    .name("smoke-import-ubuntu-2404")
//                    .diskFormat("qcow2")
//                    .containerFormat("bare")
//                    .visibility("private")
//                    .tags(java.util.List.of("smoke", "ubuntu-24.04"))
//                    .minDisk(10)
//                    .minRam(0)
//                    .build();
//
//            ObjectMapper om = new ObjectMapper()
//                    .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
//            String payload = om.writeValueAsString(req);
//            System.out.println("[DEBUG] createImage payload = " + payload);
//
//            ResponseEntity<JsonNode> res = glance.createImage(token, req);
//            System.out.println("[SMOKE] createImage OK");
//            System.out.println(res.getBody().toPrettyString());
//        } catch (org.springframework.web.reactive.function.client.WebClientResponseException ex) {
//            System.out.println("[SMOKE] createImage FAIL");
//            System.out.println("Status  = " + ex.getStatusCode());
//            System.out.println("Headers = " + ex.getHeaders());
//            System.out.println("Body    = " + ex.getResponseBodyAsString()); // ★ 여기서 이유 나옴
//        }

////        // import Image
//        try {
//            ImportImageRequest req = ImportImageRequest.builder()
//                    .method(ImportImageRequest.Method.builder()
//                            .name("web-download") // import 방식
//                            .uri("https://cloud-images.ubuntu.com/noble/current/noble-server-cloudimg-amd64.img")
//                            .build())
//                    .allStores(true)
//                    .allStoresMustSucceed(false)
//                    .build();
//            System.out.println(req.toString());
//            ResponseEntity<JsonNode> res = glance.importImage(
//                    token,
//                    "63bf0789-169f-4b56-b7fd-b7643c575f8e",
//                    req
//            );
//            System.out.println("[SMOKE] importImage OK");
//            if (res.getBody() != null) {
//                System.out.println(res.getBody().toPrettyString());
//            } else {
//                System.out.println("No body (204 No Content expected).");
//            }
//        } catch (Exception e) {
//            System.out.println("[SMOKE] importImage FAIL");
//            e.printStackTrace(System.out);
//        }
    }
}

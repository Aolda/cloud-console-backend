package com.acc.server.local.controller;

import com.acc.server.local.dto.google.GoogleFormRequest;
import com.acc.server.local.service.ports.GoogleServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/google")
public class GoogleController {

    private final GoogleServicePort googleServicePort;

    @GetMapping("/sheets")
    public ResponseEntity<?> readSheet() {
        return ResponseEntity.ok(googleServicePort.readSheetData());
    }

    @PostMapping("/form-submissions")
    public ResponseEntity<Void> receiveGoogleForm(@RequestBody GoogleFormRequest request) {
        googleServicePort.receiveFormSubmission(request);
        return ResponseEntity.ok().build();
    }
}

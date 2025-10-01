package com.acc.local.controller;

import com.acc.local.external.dto.google.GoogleFormRequest;
import com.acc.local.external.ports.GoogleExternalPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/google")
public class GoogleController {

    private final GoogleExternalPort googleExternalPort;

    @GetMapping("/sheets")
    public ResponseEntity<List<GoogleFormRequest>> readSheet() {
        return ResponseEntity.ok(googleExternalPort.readSheetData());
    }

    @PostMapping("/form-submissions")
    public ResponseEntity<Void> receiveGoogleForm(@RequestBody GoogleFormRequest request) {
        googleExternalPort.receiveFormSubmission(request);
        return ResponseEntity.ok().build();
    }
}

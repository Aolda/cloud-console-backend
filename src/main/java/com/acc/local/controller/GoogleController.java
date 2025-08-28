package com.acc.local.controller;

import com.acc.local.dto.google.GoogleFormRequest;
import com.acc.local.service.ports.GoogleServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/google")
public class GoogleController {

    private final GoogleServicePort googlePort;

    @GetMapping("/sheets")
    public ResponseEntity<List<GoogleFormRequest>> readSheet() {
        return ResponseEntity.ok(googlePort.readSheetData());
    }

    @PostMapping("/form-submissions")
    public ResponseEntity<Void> receiveGoogleForm(@RequestBody GoogleFormRequest request) {
        googlePort.receiveFormSubmission(request);
        return ResponseEntity.ok().build();
    }
}

package com.acc.local.service.ports;

import com.acc.local.dto.google.GoogleFormRequest;

import java.util.List;

public interface GoogleServicePort {

    List<GoogleFormRequest> readSheetData();
    void receiveFormSubmission(GoogleFormRequest request);
}

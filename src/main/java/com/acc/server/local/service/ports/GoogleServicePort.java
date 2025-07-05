package com.acc.server.local.service.ports;

import com.acc.server.local.dto.google.GoogleFormRequest;

import java.util.List;

public interface GoogleServicePort {

    List<List<Object>> readSheetData();
    void receiveFormSubmission(GoogleFormRequest request);
}

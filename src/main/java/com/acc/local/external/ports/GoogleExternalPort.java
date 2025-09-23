package com.acc.local.external.ports;

import com.acc.local.external.dto.google.GoogleFormRequest;

import java.util.List;

public interface GoogleExternalPort {

    List<GoogleFormRequest> readSheetData();
    void receiveFormSubmission(GoogleFormRequest request);
}

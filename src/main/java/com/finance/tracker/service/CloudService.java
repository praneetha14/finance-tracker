package com.finance.tracker.service;

import com.finance.tracker.model.enums.MonthEnum;

import java.util.UUID;

public interface CloudService {
    String uploadFileToCLoudStorage(byte[] fileContent, UUID userId, MonthEnum monthEnum, int year, String fileName);
    String generatePreSignedUrl(String fileKey);
}

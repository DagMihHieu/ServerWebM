package com.lowquality.serverwebm.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlUtils {

    private static String uploadDir;

    @Value("${file.upload-dir}")
    public void setUploadDir(String value) {
        UrlUtils.uploadDir = value;
    }

    public static String toPublicUrl(String filePath) {
        return filePath != null && uploadDir != null
                ? filePath.replace(uploadDir, "/upload")
                : null;
    }
}
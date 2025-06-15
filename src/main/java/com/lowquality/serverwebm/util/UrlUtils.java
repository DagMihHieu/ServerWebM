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
        if (filePath == null || uploadDir == null) {
            return null;
        }

        // Chỉ thay thế nếu đường dẫn bắt đầu bằng uploadDir
        if (filePath.startsWith(uploadDir)) {
            return filePath.replace(uploadDir, "http://localhost:8080/upload/");
        }

        // Nếu không khớp, trả về nguyên bản
        return filePath;
    }
}
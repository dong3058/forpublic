package com.roulette.roulette.post.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class ImageConvertService {

    public static String encodeFileToBase64Binary(Path filePath) throws IOException {
        byte[] fileContent = Files.readAllBytes(filePath);
        return Base64.getEncoder().encodeToString(fileContent);
    }
}

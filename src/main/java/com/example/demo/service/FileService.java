package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {
    @Value("${imthang.upload-file.base-uri}")
    private String baseUri;

    public void createDirectory(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri);
        File file = new File(path.toString());
        if (!file.isDirectory()) {
            try {
                Files.createDirectory(file.toPath());
                System.out.println(">>> Create new directory successfully, path:" + file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String storeFile(MultipartFile multipartFile, String folder) throws URISyntaxException, IOException {
        // create unique filename
        String fileName = System.currentTimeMillis() + "-" + multipartFile.getOriginalFilename();

        URI uri = new URI(baseUri + folder + "/" + fileName);
        Path path = Paths.get(uri);
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }
        return fileName;
    }
}

package com.example.demo.controller;

import com.example.demo.domain.file.ResUploadFileDTO;
import com.example.demo.domain.response.job.ResUpdateJobDTO;
import com.example.demo.service.FileService;
import com.example.demo.util.annontation.ApiMessage;
import com.example.demo.util.error.StorageException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    @Value("${imthang.upload-file.base-uri}")
    private String baseUri;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> uploadFile(@RequestParam(name = "file", required = false) MultipartFile file,
                                                       @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {

        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty. Please upload a file.");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jdg", "jpeg", "png", "doc", "docx", "jfif");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
        if (!isValid) {
            throw new StorageException("File is not valid. Please upload a file.");
        }

        //create a directory if not exist
        this.fileService.createDirectory(baseUri + folder);
        //store file
        String uploadFile = this.fileService.storeFile(file, folder);
        ResUploadFileDTO res = new ResUploadFileDTO(uploadFile, Instant.now());

        return ResponseEntity.ok().body(res);
    }
}
package com.nico.store.store.service.impl;

import com.nico.store.store.service.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileUploadServiceImpl implements FileUploadService {
    private static String UPLOADED_FOLDER = System.getProperty("user.dir") +
            "src/main/resources/static/images/article/pictures/";

    @Override
    public String upload(MultipartFile file) {
        if (file.isEmpty())
            return null;

        String fileName = null;
        try {
            fileName = generateFileName(file.getOriginalFilename());

            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + fileName);
            Files.write(path, bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    private String generateFileName(String file) {
        String ext = file.substring(file.lastIndexOf("."));
        String fileName = System.currentTimeMillis() + ext;
        return fileName;
    }

}

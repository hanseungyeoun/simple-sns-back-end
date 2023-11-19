package com.example.simplesms.service.upload;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileUploder {

    List<String> storeFiles(List<MultipartFile> multipartFiles) throws IOException;
    String storeFile(MultipartFile multipartFile) throws IOException;
    void deleteFile(String fileName) throws IOException;

}

package com.example.simplesms.service.upload;

import com.example.simplesms.response.exception.IllegalStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class LocalFileUploader implements FileUploder {

    @Value("${download-path}")
    private String fileDir;

    @Value("${image-server-url}")
    private String imageServer;

    @Override
    public List<String> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<String> storeFiles = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFiles.add(storeFile(multipartFile));
            }
        }
        return storeFiles;
    }

    @Override
    public String storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        multipartFile.transferTo(new File(getFullPath(storeFileName)));
        return imageServer+storeFileName;
    }

    @Override
    public void deleteFile(String fileName) throws IOException {
        Path filePath = Paths.get(getFullPath(fileName));

        try {
            Files.delete(filePath);
        } catch (NoSuchFileException e) {
            throw new IllegalStatusException("삭제하려는 파일/디렉토리가 없습니다");
        }  catch (IOException e) {
            throw new IllegalStatusException("이미지 파일 삭제에 실패 하였습니다.");
        }
    }

    private String getFullPath(String filename) {
        return fileDir + filename;
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}

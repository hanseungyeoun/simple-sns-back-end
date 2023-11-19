package com.example.simplesms.service.upload;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class S3Uploader implements FileUploder {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.dir}")
    private String dir;

    private final AmazonS3 amazonS3;

    @Override
    public List<String> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<String> storeFileResult = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    @Override
    public String storeFile(MultipartFile multipartFile) throws IOException {
        String s3FileName = dir + "/" + UUID.randomUUID() + "." + multipartFile.getOriginalFilename();
        ObjectMetadata objMeta = new ObjectMetadata();

        objMeta.setContentType(multipartFile.getContentType());
        objMeta.setContentLength(multipartFile.getSize());
        amazonS3.putObject(bucket, s3FileName, multipartFile.getInputStream(), objMeta);
        return amazonS3.getUrl(bucket, s3FileName).toString();
    }

    @Override
    public void deleteFile(String fileName) throws IOException {
        log.info("file name : "+ fileName);
        try {
            amazonS3.deleteObject(bucket, (fileName).replace(File.separatorChar, '/'));
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        }
    }
}
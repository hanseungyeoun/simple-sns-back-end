package com.example.simplesms.config;

import com.amazonaws.services.s3.AmazonS3Client;
import com.example.simplesms.service.upload.FileUploder;
import com.example.simplesms.service.upload.LocalFileUploader;
import com.example.simplesms.service.upload.S3Uploader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class UploderConfig {

    @Profile("!(prod)")
    @Bean
    FileUploder localFileUploader() {
        return new LocalFileUploader();
    }

    @Profile("prod")
    @Bean
    FileUploder s3Uploader(AmazonS3Client amazonS3Client) {
        return new S3Uploader(amazonS3Client);
    }
}

package com.example.simplesms.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.MalformedURLException;

@RequiredArgsConstructor
@RequestMapping
public class S3FileController {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3Client;

    @GetMapping("s3/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws
            MalformedURLException {

        return  new UrlResource(amazonS3Client.getUrl(bucket, filename));
    }
}

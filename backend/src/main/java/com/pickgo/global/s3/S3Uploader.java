package com.pickgo.global.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3Uploader {
    String upload(MultipartFile file, String dirName);

    void delete(String url);
}

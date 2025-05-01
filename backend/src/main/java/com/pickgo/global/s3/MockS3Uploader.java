package com.pickgo.global.s3;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Profile("test")
public class MockS3Uploader implements S3Uploader {

    @Override
    public String upload(MultipartFile file, String dirName) {
        return "https://mock-s3.com/" + dirName + "/" + file.getOriginalFilename();
    }

    @Override
    public void delete(String url) {
    }
}

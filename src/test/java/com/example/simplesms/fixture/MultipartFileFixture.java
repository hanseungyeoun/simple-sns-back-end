package com.example.simplesms.fixture;

import com.example.simplesms.domain.post.Hashtag;
import com.example.simplesms.dto.post.PostUpdateRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.InputStream;

public class MultipartFileFixture {

    public static MockMultipartFile createMockMultipartFile(String name){
        ClassPathResource resource = new ClassPathResource("images/image.jpg");
        InputStream inputStream = null;
        MockMultipartFile mockMultipartFile = null;
        try {
            inputStream = resource.getInputStream();
            mockMultipartFile = new MockMultipartFile(name,
                    resource.getFilename(),
                    MediaType.IMAGE_JPEG_VALUE,
                    inputStream);

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mockMultipartFile;
    }




}

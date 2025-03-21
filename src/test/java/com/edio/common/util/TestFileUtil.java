package com.edio.common.util;

import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

public class TestFileUtil {

    private TestFileUtil() { }

    public static MultipartFile mockMultipartFile(boolean isEmpty, String contentType) {
        MultipartFile file = mock(MultipartFile.class);
        lenient().when(file.isEmpty()).thenReturn(isEmpty);
        lenient().when(file.getContentType()).thenReturn(contentType);
        return file;
    }
}

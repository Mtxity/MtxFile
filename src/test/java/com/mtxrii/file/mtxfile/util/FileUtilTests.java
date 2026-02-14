package com.mtxrii.file.mtxfile.util;

import com.mtxrii.file.mtxfile.api.model.enumeration.FileType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileUtilTests {

    @Nested
    class GetFileTypeTests {
        @Mock
        MultipartFile mockFile = mock(MultipartFile.class);

        @Test
        void testGetFileType_nullFile() {
            assertEquals(FileType.UNKNOWN, FileUtil.getFileType(null));
        }

        @Test
        void testGetFileType_emptyFile() {
            when(mockFile.isEmpty()).thenReturn(true);
            assertEquals(FileType.UNKNOWN, FileUtil.getFileType(mockFile));
        }

        @Test
        void testGetFileType_containsNothing() {
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getOriginalFilename()).thenReturn("");
            assertEquals(FileType.UNKNOWN, FileUtil.getFileType(mockFile));
        }

        @Test
        void testGetFileType_containsNoExtension() {
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getOriginalFilename()).thenReturn("test");
            assertEquals(FileType.UNKNOWN, FileUtil.getFileType(mockFile));
        }

        @Test
        void testGetFileType_validFiles() {
            for (FileType fileType : FileType.values()) {
                when(mockFile.getOriginalFilename()).thenReturn("test." + fileType.extension);
                assertEquals(fileType, FileUtil.getFileType(mockFile));
            }
        }
    }
}

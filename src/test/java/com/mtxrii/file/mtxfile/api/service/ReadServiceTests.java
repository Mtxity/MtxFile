package com.mtxrii.file.mtxfile.api.service;

import com.mtxrii.file.mtxfile.FileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReadServiceTests {
    private ReadService readService;

    @BeforeEach
    void setUp() {
        readService = new ReadService();
    }

    @Nested
    class StringJoinTests {

        @Test
        void testStringJoin_nullDelimiter() {
            assertThrows(NullPointerException.class, () -> readService.stringJoin(null, " or ", "a", "b"));
        }

        @Test
        void testStringJoin_nullLastDelimiter() {
            assertThrows(NullPointerException.class, () -> readService.stringJoin(", ", null, "a", "b"));
        }

        @Test
        void testStringJoin_nullElements() {
            assertThrows(NullPointerException.class, () -> readService.stringJoin(", ", " or ", null));
        }

        @Test
        void testStringJoin_zeroElements() {
            assertEquals("", readService.stringJoin(", ", " or "));
        }

        @Test
        void testStringJoin_oneElement_null() {
            assertThrows(NullPointerException.class, () -> readService.stringJoin(", ", " or ", new String[]{null}));
        }

        @Test
        void testStringJoin_oneElement() {
            assertEquals("1", readService.stringJoin(", ", " or ", "1"));
        }

        @Test
        void testStringJoin_twoElements_null1st() {
            assertThrows(NullPointerException.class, () -> readService.stringJoin(", ", " or ", new String[]{null, "2"}));
        }

        @Test
        void testStringJoin_twoElements_null2nd() {
            assertThrows(NullPointerException.class, () -> readService.stringJoin(", ", " or ", new String[]{"2", null}));
        }

        @Test
        void testStringJoin_twoElements() {
            assertEquals("1 or 2", readService.stringJoin(", ", " or ", "1", "2"));
        }

        @Test
        void testStringJoin_threeElements() {
            assertEquals("1, 2 or 3", readService.stringJoin(", ", " or ", "1", "2", "3"));
        }

        @Test
        void testStringJoin_manyElements() {
            int testCount = 999;
            String[] testElements = new String[testCount];
            for (int i = 0; i < testCount; i++) {
                testElements[i] = String.valueOf(i);
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < testCount - 2; i++) {
                sb.append(testElements[i]).append(", ");
            }
            sb.append(testElements[testCount - 2]).append(" or ").append(testElements[testCount - 1]);
            assertEquals(sb.toString(), readService.stringJoin(", ", " or ", testElements));
        }

        @Test
        void testStringJoin_manyElements_nullElement() {
            int testCount = 20;
            String[] testElements = new String[testCount];
            for (int i = 0; i < testCount; i++) {
                testElements[i] = String.valueOf(i);
            }
            testElements[10] = null;
            assertThrows(NullPointerException.class, () -> readService.stringJoin(", ", " or ", testElements));
        }
    }

    @Nested
    class GetFileTypeTests {
        @Mock
        MultipartFile mockFile = mock(MultipartFile.class);

        @Test
        void testGetFileType_nullFile() {
            assertEquals(FileType.UNKNOWN, readService.getFileType(null));
        }

        @Test
        void testGetFileType_emptyFile() {
            when(mockFile.isEmpty()).thenReturn(true);
            assertEquals(FileType.UNKNOWN, readService.getFileType(mockFile));
        }

        @Test
        void testGetFileType_containsNothing() {
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getOriginalFilename()).thenReturn("");
            assertEquals(FileType.UNKNOWN, readService.getFileType(mockFile));
        }

        @Test
        void testGetFileType_containsNoExtension() {
            when(mockFile.isEmpty()).thenReturn(false);
            when(mockFile.getOriginalFilename()).thenReturn("test");
            assertEquals(FileType.UNKNOWN, readService.getFileType(mockFile));
        }

        @Test
        void testGetFileType_validFiles() {
            for (FileType fileType : FileType.values()) {
                when(mockFile.getOriginalFilename()).thenReturn("test." + fileType.extension);
                assertEquals(fileType, readService.getFileType(mockFile));
            }
        }
    }
}

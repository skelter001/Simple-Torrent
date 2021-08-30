package service;

import config.PathConfig;
import exception.TorrentFileException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileLoaderTest {

    static Path filePath;

    /**
     * Эта папка и созданные в ней файлы будут удалены после завершения работы тестов
     */
    @TempDir
    static Path folder;

    @BeforeAll
    static void setUp() {
        filePath = folder.resolve("testfile1.txt");
    }

    @Test
    public void getFile_whenFileDoesNotExist_throwsTorrentException() {

        FileLoader loader = new FileLoader(new PathConfig(folder.toString()));

        assertThrows(TorrentFileException.class, () ->
                loader.getFile("xxx.txt"));
    }

    @Test
    public void getFile_whenContainsFile_ReturnFile() throws TorrentFileException, IOException {

        Files.createFile(filePath);
        FileLoader loader = new FileLoader(new PathConfig(folder.toString()));

        assertEquals(Optional.of(filePath.toFile()),
                loader.getFile(filePath.toString()));
    }
}
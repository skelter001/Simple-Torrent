package logic;

import config.ConfigLoader;
import config.PathConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TorrentServerTest {

    @Test
    public void getFile_whenFileExists_thenReturnCorrect() {
        TorrentServer server = new TorrentServer(new ConfigLoader());

        File file = new File("src/main/resources/db_files/123.txt");

        assertEquals(file, server.getFile("123.txt").get());
    }

    @Test
    public void getAllFilesAsString_whenFilesExist_thenReturnCorrect() throws IOException {
        TorrentServer server = new TorrentServer(new ConfigLoader());

        String expected = Files.list(Path.of("src/main/resources/db_files"))
                .map(Path::toFile)
                .map(File::getName)
                .collect(Collectors.joining("\n"));

        assertEquals(expected, server.getAllFilesAsString());
    }

    @Test
    public void getAllFiles_whenFilesExist_thenReturnCorrect() throws IOException {
        TorrentServer server = new TorrentServer(new ConfigLoader());

        List<File> expected = Files.list(Path.of("src/main/resources/db_files"))
                .map(Path::toFile)
                .collect(Collectors.toList());

        assertEquals(expected, server.getAllFiles());
    }

    @Test void getFile_whenFileDoesNotExist_thenReturnOptionalEmpty() {
        TorrentServer server = new TorrentServer(new ConfigLoader());

        assertEquals(Optional.empty(), server.getFile("xxx.jpg"));
    }
}
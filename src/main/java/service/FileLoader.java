package service;

import config.PathConfig;
import exception.TorrentFileException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileLoader {

    private final Path workingDir;

    public FileLoader(PathConfig config) {
        this.workingDir = Path.of(config.getWorkingPath());
    }

    public List<File> getAllFiles() throws IOException, TorrentFileException {
        if (!Files.exists(workingDir))
            throw new TorrentFileException("Directory " + workingDir + " does not exist");

        return Files
                .list(workingDir)
                .filter(path -> path.toFile().length() > 0 && path.toFile().length() < 137_438_953_472L) // 128 GiB
                .map(Path::toFile)
                .collect(Collectors.toList());
    }

    public Optional<File> getFile(String filename) throws TorrentFileException {
        if (!Files.exists(Paths.get(String.valueOf(workingDir), filename)))
            throw new TorrentFileException("File " + filename + " does not exist in current directory " + workingDir);

        try {
            return Files
                    .list(workingDir)
                    .filter(path -> path.equals(Paths.get(String.valueOf(workingDir), filename)))
                    .findFirst()
                    .map(path -> new File(String.valueOf(path)));
        } catch (Exception io) {
            throw new TorrentFileException("I/O exception occurred during reading working path", io);
        }
    }

}

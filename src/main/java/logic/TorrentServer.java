package logic;

import config.ConfigLoader;
import config.PathConfig;
import config.ServerTorrentConfig;
import exception.TorrentFileException;
import service.FileLoader;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TorrentServer {

    private final FileLoader loader;

    public TorrentServer(ConfigLoader config) {
        ServerTorrentConfig serverConfig = config.readConfig();
        this.loader = new FileLoader(serverConfig.getPathConfig());
    }

    public String getAllFilesAsString() {
        try {
            return loader.getAllFiles()
                    .stream()
                    .map(File::getName)
                    .collect(Collectors.joining("\n"));
        } catch (IOException | TorrentFileException exception) {
            exception.printStackTrace();
        }
        return "I/O exception occurred during loading server files";
    }

    public Optional<File> getFile(String filename) {
        try {
            return loader.getFile(filename);
        } catch (TorrentFileException e) {
            System.err.println(e.getMessage());
            return Optional.empty();
        }
    }

    public List<File> getAllFiles() {
        try {
            return loader.getAllFiles();
        } catch (IOException | TorrentFileException ex) {
            System.err.println("Can't get server files");
            return Collections.emptyList();
        }
    }
}

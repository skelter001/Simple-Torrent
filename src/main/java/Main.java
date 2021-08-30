import config.ConfigLoader;
import config.PathConfig;
import config.ServerTorrentConfig;
import exception.TorrentFileException;
import service.FileLoader;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException, TorrentFileException {
        ConfigLoader configLoader = new ConfigLoader();

        ServerTorrentConfig serverTorrentConfig = configLoader.readConfig();

        FileLoader loader = new FileLoader(new PathConfig());

        System.out.println(loader.getAllFiles()
                .stream()
                .map(File::getName)
                .collect(Collectors.joining("\n")));

        System.out.println(serverTorrentConfig.toString());
    }
}

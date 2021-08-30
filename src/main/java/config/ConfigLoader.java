package config;

import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс, отвечающий за подгрузку данных из конфигурационного файла формата .properties
*/
public class ConfigLoader {

    private final String name;

    /**
     * По умолчанию читает из server.properties
     */
    public ConfigLoader() {
        this.name = "server.properties";
    }

    /**
     *
     * @param name Имя конфигурационного файла, откуда читать
     */
    public ConfigLoader(@NonNull String name) {
        this.name = name;
    }

    /**
     * Считывает конфиг из указанного в конструкторе файла.
     * Если не удалось считать из заданного файла, или какого-то конкретно значения не оказалось,
     * то используют дефолтные значения из {@link PathConfig} и {@link ServerConfig}
     * <br/>
     * Читаются: "torrent.workingPath", "torrent.host", "torrent.port" (но в конфигурационном файле допустимы и другие проперти)
     */
    public ServerTorrentConfig readConfig() {
        Properties properties = new Properties();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(name)) {
            properties.load(is);
        } catch (IOException io) {
            io.printStackTrace();
        }

        return ServerTorrentConfig
                .builder()
                .serverConfig(new ServerConfig(properties.getProperty("torrent.host").isEmpty() ?
                        ServerConfig.DEFAULT_HOST : properties.getProperty("torrent.host"),
                        properties.getProperty("torrent.port").isEmpty() ?
                                ServerConfig.DEFAULT_PORT : Integer.parseInt(properties.getProperty("torrent.port")),
                        properties.getProperty("torrent.clients").isEmpty() ?
                                ServerConfig.MAX_CLIENT_AMOUNT : Integer.parseInt(properties.getProperty("torrent.clients"))))
                .pathConfig(new PathConfig(properties.getProperty("torrent.workingPath").isEmpty() ?
                        PathConfig.DEFAULT_WORKING_PATH : properties.getProperty("torrent.workingPath")))
                .build();
    }
}

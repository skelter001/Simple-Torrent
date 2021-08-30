package config;

import lombok.Getter;
import lombok.NonNull;

/**
 * Какой хост и порт будет слушать наш сервер
 */
@Getter
public class ServerConfig {

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 8080;
    public static final int MAX_CLIENT_AMOUNT = 50;

    private final String host;
    private final int port;
    private final int socketAmount;

    public ServerConfig(@NonNull String host, int port) {
        this.host = host;
        this.port = port;
        this.socketAmount = MAX_CLIENT_AMOUNT;
    }

    public ServerConfig(@NonNull String host, int port, int socketAmount) {
        this.host = host;
        this.port = port;
        this.socketAmount = socketAmount;
    }

    public ServerConfig() {
        this.host = DEFAULT_HOST;
        this.port = DEFAULT_PORT;
        this.socketAmount = MAX_CLIENT_AMOUNT;
    }
}

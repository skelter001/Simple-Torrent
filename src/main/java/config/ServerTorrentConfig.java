package config;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class ServerTorrentConfig {
    private final ServerConfig serverConfig;
    private final PathConfig pathConfig;
}

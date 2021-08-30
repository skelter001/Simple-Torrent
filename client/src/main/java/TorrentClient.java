import config.ConnectionConfig;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public interface TorrentClient extends Runnable {
    Optional<File> downloadFile(String filename) throws IOException;
    void startConnection(ConnectionConfig config) throws IOException;
    void stopConnection() throws IOException;
}

import config.ConnectionConfig;

import java.io.IOException;

public class Client2 {
    public static void main(String[] args) throws IOException {
        TorrentClient client = new TorrentClientImpl();
        client.startConnection(new ConnectionConfig("localhost", 6666));
        client.run();
        client.stopConnection();
    }
}

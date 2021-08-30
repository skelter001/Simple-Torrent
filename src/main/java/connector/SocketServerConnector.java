package connector;

import command.ClientCommand;
import config.ConfigLoader;
import config.ServerConfig;
import exception.ConnectionException;
import logic.TorrentServer;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Класс, который предоставляет доступ к серверу через сокеты
 */
public class SocketServerConnector implements Closeable {

    /**
     * Экзекьютор для выполнения ClientTask
     */
    private final ExecutorService clientIOWorkers;
    private final ExecutorService connectionAcceptorExecutor;
    private final ServerSocket serverSocket;
    private final TorrentServer server;

    /**
     * Конструктор
     *
     * @param serverConfig для инициализации сокета сервера.
     * @param server       сервер со своей логикой
     * @throws ConnectionException если произошла ошибка создания серверного сокета
     */
    public SocketServerConnector(ServerConfig serverConfig, TorrentServer server) throws ConnectionException {
        this.server = server;
        this.clientIOWorkers = Executors.newFixedThreadPool(serverConfig.getSocketAmount());
        this.connectionAcceptorExecutor = Executors.newFixedThreadPool(serverConfig.getSocketAmount());
        try {
            this.serverSocket = new ServerSocket(serverConfig.getPort());
        } catch (IOException io) {
            throw new ConnectionException("Could not create server socket", io);
        }
    }

    public void start() {
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                connectionAcceptorExecutor.submit(() -> {
                    ClientTask clientTask = new ClientTask(clientSocket, server);
                    clientTask.run();
                });
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        ConfigLoader loader = new ConfigLoader();
        SocketServerConnector connector = new SocketServerConnector(
                loader.readConfig().getServerConfig(),
                new TorrentServer(loader)
        );
        connector.start();
    }

    @Override
    public void close() throws IOException {
        System.out.println("Stopping socket connector");
        serverSocket.close();
    }

    /**
     * Runnable, описывающий исполнение клиентской команды.
     */
    static class ClientTask implements Runnable, Closeable {

        private final Socket client;
        private final DataOutputStream out;
        private final DataInputStream in;
        private final TorrentServer server;

        /**
         * @param client клиентский сокет
         */
        ClientTask(Socket client, TorrentServer server) throws ConnectionException {
            this.client = client;
            this.server = server;
            try {
                this.out = new DataOutputStream(client.getOutputStream());
                out.writeUTF("Server files:\n" + server.getAllFilesAsString());
                out.flush();
            } catch (IOException io) {
                throw new ConnectionException("I/O error occurred by trying get output stream from client socket", io);
            }
            try {
                this.in = new DataInputStream(client.getInputStream());
            } catch (IOException io) {
                throw new ConnectionException("I/O error occurred by trying get input stream from client socket", io);
            }
        }

        @Override
        public void run() {

            while (true) {
                if(client.isClosed()) {
                    return;
                }
                try {
                    String command = in.readUTF();
                    if (command.equalsIgnoreCase(ClientCommand.GET.toString()))
                        commonInfoExchange();
                    else if (command.equalsIgnoreCase(ClientCommand.EXIT.toString())) {
                        close();
                        return;
                    } else {
                        System.err.println("Invalid client command");
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }

        public void commonInfoExchange() {
            try {
                String filename = in.readUTF();

                Optional<File> file = server.getFile(filename);

                if (file.isEmpty()) {
                    out.writeLong(0L);
                    out.flush();
                    return;
                } else {
                    out.writeLong(file.get().length());
                    out.flush();
                }
                String ans = in.readUTF();
                boolean validDir = in.readBoolean();
                if (ans.equalsIgnoreCase("Y") && validDir) {
                    sendBytes(new BufferedInputStream(new FileInputStream(file.get())), out);
                }
            } catch (IOException io) {
                throw new ConnectionException("Buffer error occurred", io);
            }
        }

        public void sendBytes(BufferedInputStream in, OutputStream out) {
            byte[] buffer = new byte[8192];
            try {
                while (in.read(buffer, 0, buffer.length) > 0) {
                    out.write(buffer);
                    out.flush();
                }
            } catch (IOException ex) {
                System.err.println("I/O exception occurred during reading/writing to socket stream" + ex.getMessage());
            }
        }

        @Override
        public void close() throws IOException {
            client.close();
        }
    }
}

import command.ClientCommand;
import config.ConnectionConfig;
import exception.ConnectionException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TorrentClientImpl implements TorrentClient {

    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private String fileList;
    private final List<File> downloadedFiles;

    public TorrentClientImpl() {
        this.downloadedFiles = new ArrayList<>();
    }

    public void startConnection(ConnectionConfig config) throws IOException {
        clientSocket = new Socket(config.getHost(), config.getPort());
        out = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
        in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        fileList = in.readUTF();
    }

    public void run() {
        while (!clientSocket.isClosed()) {
            Scanner sc = new Scanner(System.in);

            System.out.println(fileList);

            System.out.println("Write down command[Get/Exit] (case irrelevant)" +
                    "\n\"Get\": to download file with specific name" +
                    "\n\"Exit\": to stop connection");
            String command = sc.nextLine();
            System.out.println("> " + command);
            try {
                out.writeUTF(command);
                out.flush();

                if (command.equalsIgnoreCase(ClientCommand.GET.toString())) {
                    System.out.println("Write down filename which you want to download:");
                    downloadFile(sc.nextLine()).ifPresent(
                            downloadedFiles::add
                    );
                    System.out.println("Downloaded files:\n" + downloadedFiles
                            .stream()
                            .map(File::getName)
                            .collect(Collectors.joining("\n")));
                } else if (command.equalsIgnoreCase(ClientCommand.EXIT.toString())) {
                    try {
                        stopConnection();
                    } catch (IOException io) {
                        throw new ConnectionException("I/O error occurred during closing socket", io);
                    }
                } else {
                    System.out.println("Command not found");
                }
            } catch (IOException exception) {
                System.err.println("An error occurred during reading/writing from socket stream");
            }
        }
    }

    public Optional<File> downloadFile(String filename) throws IOException {

        out.writeUTF(filename);
        out.flush();

        long fileSize = in.readLong();

        if (fileSize <= 0) {
            System.out.println("Wrong filename");
            return Optional.empty();
        } else {
            System.out.println("You are trying to download file: \"" + filename + "\" (" + fileSize + " bytes)" +
                    "\nAre you sure?[Y/N]");

            Scanner sc = new Scanner(System.in);
            String ans = sc.nextLine();

            if (ans.equalsIgnoreCase("Y")) {
                System.out.println("Specify directory for downloading file:");
                String dir = sc.nextLine();

                out.writeUTF(ans);

                if (!Files.exists(Path.of(dir))) {
                    System.out.println("Invalid path: " + dir);
                    out.writeBoolean(false);
                    out.flush();
                    return Optional.empty();
                }
                out.writeBoolean(true); // valid dir
                out.flush();

                File downloadedFile;

                downloadedFile = new File(dir, filename);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(downloadedFile));
                byte[] buffer = new byte[8192];
                long remaining = fileSize;
                int cnt;
                while ((cnt = in.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {
                    remaining -= cnt;
                    bos.write(buffer, 0, cnt);
                    bos.flush();
                }

                bos.close();
                return Optional.of(downloadedFile);
            }
        }
        return Optional.empty();
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException {
        TorrentClient client = new TorrentClientImpl();
        client.startConnection(new ConnectionConfig("localhost", 6666));
        client.run();
        client.stopConnection();
    }
}

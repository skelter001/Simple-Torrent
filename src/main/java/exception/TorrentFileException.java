package exception;

public class TorrentFileException extends Exception {

    public TorrentFileException(String msg, Throwable ex) {
        super(msg, ex);
    }

    public TorrentFileException(String msg) {
        super(msg);
    }

    public TorrentFileException() {
        super("An error occurred while with working directory or its files");
    }
}

package command;

public enum ClientCommand {
    GET("Get"),
    EXIT("Exit");

    private final String commandName;

    ClientCommand(String commandName) {
        this.commandName = commandName;
    }

    @Override
    public String toString() {
        return this.name().charAt(0) + this.name().toLowerCase().substring(1);
    }
}

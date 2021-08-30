package config;

import lombok.Getter;
import lombok.NonNull;

public class PathConfig {

    public static final String DEFAULT_WORKING_PATH = "src/main/resources/";
    @Getter
    private final String workingPath;

    public PathConfig() {
        this.workingPath = DEFAULT_WORKING_PATH;
    }

    public PathConfig(@NonNull String workingPath) {
        this.workingPath = workingPath;
    }
}

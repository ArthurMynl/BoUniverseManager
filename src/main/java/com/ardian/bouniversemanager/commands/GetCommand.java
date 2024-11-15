package main.java.com.ardian.bouniversemanager.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import main.java.com.ardian.bouniversemanager.services.GetService;

public class GetCommand implements Command {
    private final String universeName;
    private final String outputPath;
    private final GetService getService;

    public GetCommand(String universeName, String outputPath) {
        this.universeName = universeName;
        this.outputPath = outputPath != null ? outputPath : ".";
        this.getService = new GetService();
    }

    @Override
    public void execute() throws Exception {
        Path path = Paths.get(outputPath);
        if (Files.isDirectory(path)) {
            // Default filename if only a directory is specified
            path = path.resolve(universeName + ".xlsx");
        }

        if (Files.exists(path) && !Files.isWritable(path)) {
            throw new IOException("Cannot write to the specified file path: " + path.toString());
        }

        Files.createDirectories(path.getParent());

        getService.generateExcelFile(universeName, path); 
    }
}

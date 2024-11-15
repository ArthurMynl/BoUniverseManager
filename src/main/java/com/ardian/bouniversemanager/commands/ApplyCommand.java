package main.java.com.ardian.bouniversemanager.commands;

import java.io.File;

import main.java.com.ardian.bouniversemanager.services.ApplyService;

public class ApplyCommand implements Command {
    private ApplyService applyService;
    private String inputFile;


    public ApplyCommand(String inputFile) {
        this.inputFile = inputFile;
        this.applyService = new ApplyService();
    }

    @Override
    public void execute() throws Exception {
        File file = new File(this.inputFile);
        if (!file.exists()) {
            throw new IllegalArgumentException("Input file does not exist: " + file.getAbsolutePath());
        }
        applyService.applyChanges(file);
    }
}

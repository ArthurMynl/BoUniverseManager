package main.java.com.ardian.bouniversemanager.commands;

import java.io.File;

import main.java.com.ardian.bouniversemanager.services.PlanService;

public class PlanCommand implements Command {
    private PlanService planService;
    private String inputFile;

    public PlanCommand(String inputFile) {
        this.inputFile = inputFile;
        this.planService = new PlanService();
    }

    @Override
    public void execute() throws Exception {
        File file = new File(this.inputFile);
        if (!file.exists()) {
            throw new IllegalArgumentException("Input file does not exist: " + file.getAbsolutePath());
        }
        planService.compareUniverses(file);
    }
}

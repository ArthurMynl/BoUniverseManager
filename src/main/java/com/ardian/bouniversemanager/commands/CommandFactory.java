package main.java.com.ardian.bouniversemanager.commands;

import org.apache.commons.cli.CommandLine;

public class CommandFactory {

    public static Command getCommand(String commandName, CommandLine cmd, String[] args) throws Exception {
        switch (commandName) {
            case "get":
                if (args.length < 2) {
                    throw new IllegalArgumentException("The 'get' command requires a UniverseName argument.");
                }
                String universeName = args[1]; // First argument after 'get'
                String outputPath = cmd.getOptionValue("o", "."); // Default to current directory
                return new GetCommand(universeName, outputPath);

            case "plan":
                if (args.length < 2) {
                    throw new IllegalArgumentException("The 'plan' command requires an INPUT_PATH_FILE argument.");
                }
                String planFilePath = args[1];
                return new PlanCommand(planFilePath);

            case "apply":
                if (args.length < 2) {
                    throw new IllegalArgumentException("The 'apply' command requires an INPUT_PATH_FILE argument.");
                }
                String applyFilePath = args[1];
                return new ApplyCommand(applyFilePath);

            default:
                throw new IllegalArgumentException("Unknown command: " + commandName);
        }
    }
}

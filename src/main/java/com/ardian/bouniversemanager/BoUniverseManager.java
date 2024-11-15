package main.java.com.ardian.bouniversemanager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import main.java.com.ardian.bouniversemanager.commands.Command;
import main.java.com.ardian.bouniversemanager.commands.CommandFactory;

public class BoUniverseManager {
    
    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();

        Option getOption = Option.builder()
            .longOpt("get")
            .desc("Generate an Excel file representing a SAP BO universe")
            .hasArg()
            .argName("Universe Name")
            .required(false) // Main argument, not prefixed, so we won't mark as required here
            .build();

        Option outputOption = Option.builder("o")
            .longOpt("output")
            .desc("Optional target path to save the Excel file (defaults to current directory)")
            .hasArg()
            .argName("Output path")
            .required(false) // Not required, default will be handled in CommandFactory
            .build();

        Option planOption = Option.builder()
            .longOpt("plan")
            .desc("Compare local and server SAP BO universes using the specified file")
            .hasArg()
            .argName("Input file")
            .required(false)
            .build();

        Option applyOption = Option.builder()
            .longOpt("apply")
            .desc("Apply changes to SAP BO server from specified file")
            .hasArg()
            .argName("Input file")
            .required(false)
            .build();

        options.addOption(getOption);
        options.addOption(outputOption);
        options.addOption(planOption);
        options.addOption(applyOption);

        try {
            CommandLine cmd = parser.parse(options, args);
            String[] remainingArgs = cmd.getArgs();

            if (remainingArgs.length < 1) {
                throw new IllegalArgumentException("No command provided. Use 'get', 'plan', or 'apply'.");
            }

            String commandName = remainingArgs[0];
            if ((commandName.equals("get") && remainingArgs.length < 2) || 
                ((commandName.equals("plan") || commandName.equals("apply")) && remainingArgs.length < 2)) {
                throw new IllegalArgumentException("Required argument missing for command: " + commandName);
            }

            Command command = CommandFactory.getCommand(commandName, cmd, remainingArgs);
            command.execute();

        } catch (ParseException e) {
            System.err.println("Error parsing command-line arguments: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar yourapp.jar <command> [options]", options);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar yourapp.jar <command> [options]", options);
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
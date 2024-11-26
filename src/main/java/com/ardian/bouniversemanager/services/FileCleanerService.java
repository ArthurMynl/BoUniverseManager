package main.java.com.ardian.bouniversemanager.services;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Logger;

public class FileCleanerService {

    private static final Logger LOGGER = Logger.getLogger(FileCleanerService.class.getName());

    /**
     * Deletes all folders related to a specific universe within the "universes" directory,
     * except for the most recently modified folder.
     *
     * @param context        The context to retrieve services, if needed.
     * @param serverUniverse The server universe whose folders are to be managed.
     * @throws IllegalStateException If the universes directory does not exist or if deletion fails.
     */
    public void cleanupUniverseFolders() {
        // Step 1: (Optional) Retrieve services if needed
        // LocalResourceService localResourceService = context.getService(LocalResourceService.class);
        // Uncomment the above line if you need to use LocalResourceService for additional operations.

        // Step 2: Define the universes directory path
        File universesDir = getUniversesDirectory();

        // Step 3: Validate the universes directory
        validateUniversesDirectory(universesDir);

        // Step 4: Retrieve all universe folders matching the serverUniverse's name
        File[] universeFolders = getAllUniverseFolders(universesDir);

        if (universeFolders == null || universeFolders.length == 0) {
            return; // Nothing to delete
        }

        if (universeFolders.length == 1) {
            return; // Only one folder exists; nothing to delete
        }

        // Step 5: Identify the latest universe folder to keep
        File latestUniverseFolder = identifyLatestFolder(universeFolders);

        LOGGER.info("Keeping the latest universe folder: " + latestUniverseFolder.getAbsolutePath());

        // Step 6: Delete all other universe folders except the latest one
        for (File folder : universeFolders) {
            if (folder.equals(latestUniverseFolder)) {
                continue; // Skip the latest folder
            }

            boolean deleted = deleteDirectoryRecursively(folder);
            if (!deleted) {
                LOGGER.warning("Failed to delete universe folder: " + folder.getAbsolutePath());
                // Optionally, handle the failure (e.g., throw an exception, retry, etc.)
                // throw new IllegalStateException("Failed to delete folder: " + folder.getAbsolutePath());
            }
        }

    }

    /**
     * Retrieves the base "universes" directory.
     *
     * @return The File object representing the universes directory.
     */
    private File getUniversesDirectory() {
        // Externalize the universes directory path via environment variable or system property
        String universesPath = System.getenv("UNIVERSES_DIR");
        if (universesPath == null || universesPath.isEmpty()) {
            universesPath = "universes"; // Default path
            LOGGER.warning("Environment variable UNIVERSES_DIR not set. Using default path: " + universesPath);
        } 
        return new File(universesPath);
    }

    /**
     * Validates that the universes directory exists and is a directory.
     *
     * @param universesDir The universes directory to validate.
     * @throws IllegalStateException If the directory does not exist or is not a directory.
     */
    private void validateUniversesDirectory(File universesDir) {
        if (!universesDir.exists()) {
            String message = "Universes directory does not exist: " + universesDir.getAbsolutePath();
            LOGGER.warning(message);
            throw new IllegalStateException(message);
        }

        if (!universesDir.isDirectory()) {
            String message = "Universes path is not a directory: " + universesDir.getAbsolutePath();
            LOGGER.warning(message);
            throw new IllegalStateException(message);
        }
    }

    /**
     * Retrieves all universe folders that match the given universe name.
     *
     * @param universesDir The base universes directory.
     * @param universeName The name of the universe to filter folders.
     * @return An array of matching universe folders.
     */
    private File[] getAllUniverseFolders(File universesDir) {
        // Define a FilenameFilter to match directories starting with the universe name
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                File potentialDir = new File(dir, name);
                // Adjust the condition based on your folder naming convention
                return potentialDir.isDirectory();
            }
        };

        File[] matchingFolders = universesDir.listFiles(filter);

        if (matchingFolders != null && matchingFolders.length > 0) {
            // Sort the folders by last modified date in descending order (latest first)
            Arrays.sort(matchingFolders, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    return Long.compare(f2.lastModified(), f1.lastModified());
                }
            });
        }

        return matchingFolders;
    }

    /**
     * Identifies the latest universe folder based on the last modified date.
     *
     * @param universeFolders An array of universe folders.
     * @return The latest modified universe folder.
     */
    private File identifyLatestFolder(File[] universeFolders) {
        // Since the array is already sorted in descending order, the first element is the latest
        return universeFolders[0];
    }

    /**
     * Recursively deletes a directory and all its contents.
     *
     * @param directory The directory to delete.
     * @return true if deletion was successful, false otherwise.
     */
    private boolean deleteDirectoryRecursively(File directory) {
        if (directory.isDirectory()) {
            File[] allContents = directory.listFiles();
            if (allContents != null) {
                for (File file : allContents) {
                    boolean success = deleteDirectoryRecursively(file);
                    if (!success) {
                        LOGGER.warning("Failed to delete file or subdirectory: " + file.getAbsolutePath());
                        return false;
                    }
                }
            }
        }
        boolean deleted = directory.delete();
        if (!deleted) {
            LOGGER.warning("Failed to delete directory or file: " + directory.getAbsolutePath());
        }
        return deleted;
    }
}

package main.java.com.ardian.bouniversemanager.services;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;


public class FileSystemCleaner {

    private static final Logger LOGGER = Logger.getLogger(FileSystemCleaner.class.getName());

    public void deleteRetrievalFolders(Path universesPath) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(universesPath, "retrieval*")) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    deleteDirectoryRecursively(entry);
                }
            }
        } catch (IOException e) {
            LOGGER.warning("Error deleting retrieval folders: " + e.getMessage());
        }
    }

    private void deleteDirectoryRecursively(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void deleteTraceLogFiles(Path projectRootPath) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(projectRootPath, "TraceLog*")) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    Files.delete(entry);
                }
            }
        } catch (IOException e) {
        }
    }
}

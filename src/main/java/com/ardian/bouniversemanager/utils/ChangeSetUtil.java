package main.java.com.ardian.bouniversemanager.utils;

import java.io.IOException;

import main.java.com.ardian.bouniversemanager.comparison.ChangeSet;

public class ChangeSetUtil {
    
    public static void saveChangeSet(ChangeSet changeSet, String filePath) throws IOException {
        // Serialize changeSet to file
    }

    public static ChangeSet loadChangeSet(String filePath) throws IOException, ClassNotFoundException {
        // Deserialize changeSet from file
        return new ChangeSet();
    }
}


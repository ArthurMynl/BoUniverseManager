package main.java.com.ardian.bouniversemanager.comparison;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;
import com.sap.sl.sdk.authoring.businesslayer.Folder;
import com.sap.sl.sdk.authoring.businesslayer.RootFolder;

import main.java.com.ardian.bouniversemanager.comparison.factory.ComparisonHandlerFactory;
import main.java.com.ardian.bouniversemanager.comparison.handlers.ComparisonHandler;

public class FolderComparator {
    private ChangeSet changes;
    private Map<String, String> localItemParentPaths;
    private Map<String, String> serverItemParentPaths;
    private Map<String, String> localItemNames;
    private Map<String, String> serverItemNames;

    private static final Logger LOGGER = Logger.getLogger(FolderComparator.class.getName());

    public FolderComparator() {
        this.changes = new ChangeSet();
        this.localItemParentPaths = new HashMap<>();
        this.serverItemParentPaths = new HashMap<>();
        this.localItemNames = new HashMap<>();
        this.serverItemNames = new HashMap<>();
    }

    public ChangeSet compareRootFolders(RootFolder localRootFolder, RootFolder serverRootFolder) {
        // Build maps of items for local and server structures
        buildItemMaps(localRootFolder, "", localItemParentPaths, localItemNames);
        buildItemMaps(serverRootFolder, "", serverItemParentPaths, serverItemNames);

        // System.out.println(serverItemParentPaths);

        // Compare items
        compareItems(localRootFolder, serverRootFolder);

        return changes;
    }

    private void buildItemMaps(Folder folder, String parentPath, Map<String, String> itemParentPaths,
            Map<String, String> itemNames) {
        String currentPath = parentPath.isEmpty() ? folder.getName() : parentPath + "/" + folder.getName();

        // Traverse all children
        for (BlItem child : folder.getChildren()) {
            if (child instanceof Folder) {
                Folder subFolder = (Folder) child;
                buildItemMaps(subFolder, currentPath, itemParentPaths, itemNames); // Recurse into subfolder
            } else {
                // It's a BlItem that is not a Folder
                itemParentPaths.put(child.getIdentifier(), currentPath); // Store parent path
                itemNames.put(child.getIdentifier(), child.getName()); // Store item name
            }
        }
    }

    private void compareItems(RootFolder localRootFolder, RootFolder serverRootFolder) {
        Set<String> allItemIdentifiers = new HashSet<>();
        allItemIdentifiers.addAll(localItemNames.keySet());
        allItemIdentifiers.addAll(serverItemNames.keySet());

        for (String identifier : allItemIdentifiers) {
            String localParentPath = localItemParentPaths.get(identifier);
            String serverParentPath = serverItemParentPaths.get(identifier);
            String localName = localItemNames.get(identifier);
            String serverName = serverItemNames.get(identifier);

            if (localParentPath != null && serverParentPath != null) {
                // Item exists in both structures
                BlItem localItem = findItemByIdentifier(localRootFolder, identifier);
                BlItem serverItem = findItemByIdentifier(serverRootFolder, identifier);

                if (localItem == null || serverItem == null) {
                    LOGGER.warning("Could not find items for identifier: " + identifier);
                    continue;
                }

                // Get the appropriate ComparisonHandler
                ComparisonHandler handler = ComparisonHandlerFactory.getHandler(localItem);

                // Compare fields and record changes
                boolean hasChange = handler.compareFields(localItem, serverItem, identifier, changes);

                if (hasChange) {
                    LOGGER.info("Change detected for identifier: " + identifier);
                }
            } else if (localParentPath != null) {
                Change change = new Change(identifier, localName);
                BlItem item = findItemByIdentifier(localRootFolder, identifier);
                if (item != null) {
                    change.addChangeDetail(
                            new AddChangeDetail(item, localParentPath, item.getParent().getIdentifier()));
                    changes.addChange(change);
                    LOGGER.info("Added item detected: " + identifier);
                } else {
                    LOGGER.warning("Added item not found for identifier: " + identifier);
                }
            } else if (serverParentPath != null) {
                // Item has been deleted
                Change change = new Change(identifier, serverName);
                BlItem item = findItemByIdentifier(serverRootFolder, identifier);
                if (item != null) {
                    change.addChangeDetail(new DeleteChangeDetail(item, serverParentPath));
                    changes.addChange(change);
                    LOGGER.info("Deleted item detected: " + identifier);
                } else {
                    LOGGER.warning("Deleted item not found for identifier: " + identifier);
                }
            }
        }
    }

    
    private BlItem findItemByIdentifier(Folder folder, String identifier) {
        for (BlItem child : folder.getChildren()) {
            if (child.getIdentifier().equals(identifier)) {
                return child;
            }
            if (child instanceof Folder) {
                BlItem result = findItemByIdentifier((Folder) child, identifier);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}

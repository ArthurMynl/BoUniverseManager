package main.java.com.ardian.bouniversemanager.comparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.sap.sl.sdk.authoring.businesslayer.BlItem;
import com.sap.sl.sdk.authoring.businesslayer.Folder;
import com.sap.sl.sdk.authoring.businesslayer.RootFolder;

import main.java.com.ardian.bouniversemanager.comparison.factory.ComparisonHandlerFactory;
import main.java.com.ardian.bouniversemanager.comparison.handlers.ComparisonHandler;

public class FolderComparator {
    private ChangeSet changes;

    private Map<String, BlItem> localItemMap;
    private Map<String, BlItem> serverItemMap;

    private static final Logger LOGGER = Logger.getLogger(FolderComparator.class.getName());

    public FolderComparator() {
        this.changes = new ChangeSet();
        this.localItemMap = new HashMap<>();
        this.serverItemMap = new HashMap<>();
    }

    public ChangeSet compareRootFolders(RootFolder localRootFolder, RootFolder serverRootFolder) {
        List<ItemEntry> localEntries = new ArrayList<>();
        List<ItemEntry> serverEntries = new ArrayList<>();

        // Flatten folder structures into ordered lists
        flattenFolderStructure(localRootFolder, "", localEntries, localItemMap);
        flattenFolderStructure(serverRootFolder, "", serverEntries, serverItemMap);

        // Extract lists of paths for diffing
        List<String> localPaths = localEntries.stream()
                .map(ItemEntry::getPath)
                .collect(Collectors.toList());
        List<String> serverPaths = serverEntries.stream()
                .map(ItemEntry::getPath)
                .collect(Collectors.toList());

        // Compute diffs
        Patch<String> patch = DiffUtils.diff(serverPaths, localPaths);
        List<AbstractDelta<String>> deltas = patch.getDeltas();

        // Process deltas
        processDeltas(deltas, localEntries, serverEntries);

        return changes;
    }

    private void flattenFolderStructure(Folder folder, String parentPath, List<ItemEntry> entries,
            Map<String, BlItem> itemMap) {
        String currentPath = parentPath.isEmpty() ? folder.getName() : parentPath + "/" + folder.getName();

        List<BlItem> children = folder.getChildren();
        for (int position = 0; position < children.size(); position++) {
            BlItem child = children.get(position);
            String itemPath = currentPath + "/" + child.getName();
            String parentIdentifier = folder.getIdentifier(); // Get the parent folder's identifier
            entries.add(new ItemEntry(child.getIdentifier(), itemPath, position, parentIdentifier));
            itemMap.put(child.getIdentifier(), child);

            if (child instanceof Folder) {
                flattenFolderStructure((Folder) child, currentPath, entries, itemMap);
            }
        }
    }

    private void processDeltas(List<AbstractDelta<String>> deltas, List<ItemEntry> localEntries,
            List<ItemEntry> serverEntries) {
        List<AbstractDelta<String>> deletions = new ArrayList<>();
        List<AbstractDelta<String>> insertions = new ArrayList<>();

        for (AbstractDelta<String> delta : deltas) {
            switch (delta.getType()) {
                case DELETE:
                    deletions.add(delta);
                    break;
                case INSERT:
                    insertions.add(delta);
                    break;
                case CHANGE:
                    // 1. check if the change is a rename (id as changed or not)
                    // 2. if it's a rename, do nothing
                    // 3. else, it must be a creation of a new item, so add it

                    if (!getIdentifierByPath(serverEntries, delta.getSource().getLines().get(0)).equals(
                            getIdentifierByPath(localEntries, delta.getTarget().getLines().get(0)))) {
                        insertions.add(delta);
                    }
                    break;
                default:
                    break;
            }
        }

        Map<String, String> deletedItems = new HashMap<>();
        Map<String, String> insertedItems = new HashMap<>();
        Map<String, Integer> deletedPositions = new HashMap<>();
        Map<String, Integer> insertedPositions = new HashMap<>();

        // Build maps of deleted and inserted items by identifier
        for (AbstractDelta<String> deletion : deletions) {
            List<String> lines = deletion.getSource().getLines();
            for (String line : lines) {
                String identifier = getIdentifierByPath(serverEntries, line);
                if (identifier != null) {
                    deletedItems.put(identifier, line);
                }
            }
        }

        for (AbstractDelta<String> insertion : insertions) {
            List<String> lines = insertion.getTarget().getLines();
            for (String line : lines) {
                String identifier = getIdentifierByPath(localEntries, line);
                if (identifier != null) {
                    insertedItems.put(identifier, line);
                }
            }
        }

        // Match deletions and insertions to detect moves
        List<String> matchedIdentifiers = new ArrayList<>();
        for (Map.Entry<String, String> deletedEntry : deletedItems.entrySet()) {
            String identifier = deletedEntry.getKey();
            String deletedPath = deletedEntry.getValue();

            if (insertedItems.containsKey(identifier)) {
                String insertedPath = insertedItems.get(identifier);
                // It's a move
                BlItem item = localItemMap.get(identifier);
                ItemEntry localEntry = getItemEntryByIdentifier(localEntries, identifier);
                ItemEntry serverEntry = getItemEntryByIdentifier(serverEntries, identifier);

                String newParentIdentifier = localEntry.getParentIdentifier();
                int oldPosition = serverEntry.getPosition();
                int newPosition = localEntry.getPosition();

                Change change = new Change(identifier, item.getName());
                change.addChangeDetail(new MoveChangeDetail(
                        deletedPath, insertedPath, item, newParentIdentifier, oldPosition, newPosition));
                changes.addChange(change);

                LOGGER.info("Moved item detected: " + identifier);

                matchedIdentifiers.add(identifier);
            }
        }

        // Remove matched items
        for (String identifier : matchedIdentifiers) {
            deletedItems.remove(identifier);
            insertedItems.remove(identifier);
            deletedPositions.remove(identifier);
            insertedPositions.remove(identifier);
        }

        // Remaining deletions are actual deletions
        for (Map.Entry<String, String> entry : deletedItems.entrySet()) {
            String identifier = entry.getKey();
            String path = entry.getValue();
            BlItem item = serverItemMap.get(identifier);

            Change change = new Change(identifier, item.getName());
            change.addChangeDetail(new DeleteChangeDetail(item, path));
            changes.addChange(change);

        }

        // Remaining insertions are actual additions
        for (Map.Entry<String, String> entry : insertedItems.entrySet()) {
            String identifier = entry.getKey();
            String path = entry.getValue();
            BlItem item = localItemMap.get(identifier);
            String parentIdentifier = getParentIdentifierByPath(path, localEntries);
            ItemEntry localEntry = getItemEntryByIdentifier(localEntries, identifier);
            int position = localEntry.getPosition();

            Change change = new Change(identifier, item.getName());
            change.addChangeDetail(new AddChangeDetail(item, path, parentIdentifier, position));
            changes.addChange(change);
        }

        // Handle modifications for items that exist in both structures and didn't move
        Set<String> commonIdentifiers = new HashSet<>();
        for (ItemEntry localEntry : localEntries) {
            String identifier = localEntry.getIdentifier();
            if (!matchedIdentifiers.contains(identifier) && serverItemMap.containsKey(identifier)) {
                commonIdentifiers.add(identifier);
            }
        }

        for (String identifier : commonIdentifiers) {
            BlItem localItem = localItemMap.get(identifier);
            BlItem serverItem = serverItemMap.get(identifier);

            if (localItem != null && serverItem != null) {
                ComparisonHandler handler = ComparisonHandlerFactory.getHandler(localItem);
                boolean hasChange = handler.compareFields(localItem, serverItem, identifier, changes);

                if (hasChange) {
                    LOGGER.info("Change detected for identifier: " + identifier);
                }
            }
        }
    }

    private String getIdentifierByPath(List<ItemEntry> entries, String path) {
        for (ItemEntry entry : entries) {
            if (entry.getPath().equals(path)) {
                return entry.getIdentifier();
            }
        }
        return null;
    }

    private String getParentIdentifierByPath(String path, List<ItemEntry> entries) {
        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex > 0) {
            String parentPath = path.substring(0, lastSlashIndex);
            return getIdentifierByPath(entries, parentPath);
        }
        return null;
    }

    private ItemEntry getItemEntryByIdentifier(List<ItemEntry> entries, String identifier) {
        for (ItemEntry entry : entries) {
            if (entry.getIdentifier().equals(identifier)) {
                return entry;
            }
        }
        return null;
    }

    // Helper class to represent items in the flattened list
    public static class ItemEntry {
        private String identifier;
        private String path;
        private int position; // Position within the parent folder
        private String parentIdentifier;

        public ItemEntry(String identifier, String path, int position, String parentIdentifier) {
            this.identifier = identifier;
            this.path = path;
            this.position = position;
            this.parentIdentifier = parentIdentifier;
        }

        public String getIdentifier() {
            return identifier;
        }

        public String getPath() {
            return path;
        }

        public int getPosition() {
            return position;
        }

        public String getParentIdentifier() {
            return parentIdentifier;
        }

        @Override
        public String toString() {
            return identifier + "@" + path;
        }
    }
}

package main.java.com.ardian.bouniversemanager.services;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Logger;

import com.sap.sl.sdk.authoring.businesslayer.BlContainer;
import com.sap.sl.sdk.authoring.businesslayer.BlItem;
import com.sap.sl.sdk.authoring.businesslayer.Folder;
import com.sap.sl.sdk.authoring.businesslayer.RelationalBusinessLayer;
import com.sap.sl.sdk.authoring.cms.CmsResourceService;
import com.sap.sl.sdk.authoring.local.LocalResourceService;
import com.sap.sl.sdk.framework.SlContext;
import com.sap.sl.sdk.framework.SlException;

import main.java.com.ardian.bouniversemanager.comparison.AddChangeDetail;
import main.java.com.ardian.bouniversemanager.comparison.Change;
import main.java.com.ardian.bouniversemanager.comparison.ChangeDetail;
import main.java.com.ardian.bouniversemanager.comparison.ChangeSet;
import main.java.com.ardian.bouniversemanager.comparison.DeleteChangeDetail;
import main.java.com.ardian.bouniversemanager.comparison.MoveChangeDetail;
import main.java.com.ardian.bouniversemanager.comparison.UpdateChangeDetail;
import main.java.com.ardian.bouniversemanager.connection.BoConnectionManager;
import main.java.com.ardian.bouniversemanager.models.Universe;
import main.java.com.ardian.bouniversemanager.utils.ComparisonUtil;
import main.java.com.ardian.bouniversemanager.utils.ExcelUtil;

public class ApplyService {

    private static final Logger LOGGER = Logger.getLogger(ApplyService.class.getName());

    public void applyChanges(File inputFile) throws Exception {
        BoConnectionManager connection = new BoConnectionManager();

        try {
            Universe serverUniverse = fetchServerUniverse(connection.getContext(),
                    inputFile.getName().replace(".xlsx", ""));
            Universe localUniverse = ExcelUtil.readUniverseFromExcel(inputFile, connection.getContext(),
                    serverUniverse);

            ChangeSet changes = ComparisonUtil.compareUniverses(localUniverse, serverUniverse);

            System.out.println(changes.toString());

            applyChanges(changes, connection.getContext(), serverUniverse);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        } finally {
            connection.disconnect();
        }
    }

    private void applyChanges(ChangeSet changes, SlContext context, Universe serverUniverse) {
        for (Change change : changes.getChanges()) {
            // 1. Get the local item corresponding to the change
            String identifier = change.getIdentifier();
            BlItem localItem = findItemByIdentifier(serverUniverse.getBlx().getRootFolder(), identifier);

            // 2. Apply the change to the local item
            for (ChangeDetail detail : change.getChangeDetails()) {
                if (detail instanceof MoveChangeDetail) {
                    MoveChangeDetail moveDetail = (MoveChangeDetail) detail;
                    BlContainer oldParent = (BlContainer) findItemByIdentifier(serverUniverse.getBlx().getRootFolder(), moveDetail.getItem().getParent().getIdentifier());
                    BlContainer newParent = (BlContainer) findItemByIdentifier(serverUniverse.getBlx().getRootFolder(),
                            moveDetail.getNewParentIdentifier());

                    // Delete the item from the old parent
                    oldParent.getChildren().remove(moveDetail.getOldPosition());

                    // Add the item to the new parent
                    newParent.getChildren().add(moveDetail.getNewPosition(), moveDetail.getItem());

                } else if (detail instanceof UpdateChangeDetail) {
                    UpdateChangeDetail updateDetail = (UpdateChangeDetail) detail;
                    String field = updateDetail.getField();
                    BlItem newItem = updateDetail.getNewItem();

                    if (localItem != null && newItem != null) {
                        // Apply the change based on the field
                        switch (field) {
                            case "name":
                                localItem.setName(newItem.getName());
                                break;
                            case "description":
                                localItem.setDescription(newItem.getDescription());
                                break;
                            // case "dataType":
                            // Dimension dimension = (Dimension) localItem;
                            // dimension.setDataType(newItem.getDataType());
                            // break;
                            // case "select":
                            // RelationalBinding binding = (RelationalBinding) localItem.getBinding();
                            // binding.setSelect(newItem.getSelect());
                            // break;
                            // case "where":
                            // binding = (RelationalBinding) localItem.getBinding();
                            // binding.setWhere(newItem.getWhere());
                            // break;
                            default:
                                System.out.println("Unknown field: " + field);
                                break;
                        }
                    } else {
                        LOGGER.severe("localItem or newItem is null for identifier " + identifier);
                    }
                } else if (detail instanceof AddChangeDetail) {
                    AddChangeDetail addDetail = (AddChangeDetail) detail;
                    BlItem newItem = addDetail.getItem();
                    BlContainer newParent = (BlContainer) findItemByIdentifier(serverUniverse.getBlx().getRootFolder(),
                            addDetail.getParentIdentifier());

                    newParent.getChildren().add(addDetail.getPosition(), newItem);
                } else if (detail instanceof DeleteChangeDetail) {
                    DeleteChangeDetail deleteDetail = (DeleteChangeDetail) detail;
                    deleteDetail.getItem().getParent().getChildren().remove(deleteDetail.getItem());
                }
            }
        }

        // 4. Update the local item in the local universe
        LocalResourceService localResourceService = context.getService(LocalResourceService.class);

        File universesDir = new File("universes");

        if (!universesDir.exists() || !universesDir.isDirectory()) {
            throw new IllegalStateException("Universes directory does not exist or is not a directory.");
        }

        // Step 2: Find the latest retrieved universe folder
        File latestUniverseFolder = getLatestUniverseFolder(universesDir);

        if (latestUniverseFolder == null) {
            throw new IllegalStateException("No existing universe folders found");
        }

        // Construct the path for the .blx file within the latest universe folder
        String blxFilePath = latestUniverseFolder.getAbsolutePath() + File.separator + serverUniverse.getName()
                + ".blx";

        // Step 3: if there is a .blx file in the latest universe folder, rename it to
        // _OLD.blx
        File blxFile = new File(blxFilePath);
        if (blxFile.exists()) {
            File oldBlxFile = new File(blxFilePath.replace(".blx", "_OLD.blx"));
            if (!oldBlxFile.exists()) {
                blxFile.renameTo(oldBlxFile);
            }
        }

        // Step 4: Save the updated .blx file
        boolean overwrite = true; // Set to true to overwrite existing file
        localResourceService.save(serverUniverse.getBlx(), blxFilePath, overwrite);

        System.out.println("Successfully updated the local universe at: " + blxFilePath);
    }

    private Universe fetchServerUniverse(SlContext context, String universeName) {
        CmsResourceService resourceService = context.getService(CmsResourceService.class);

        try {
            String retrievedUniverse = resourceService.retrieveUniverse("/Universes/" + universeName + ".unx",
                    "universes", true);

            System.out.println("Retrieved universe: " + retrievedUniverse);

            LocalResourceService localResourceService = context.getService(LocalResourceService.class);

            RelationalBusinessLayer businessLayer = (RelationalBusinessLayer) localResourceService
                    .load(retrievedUniverse);

            Universe universe = new Universe.Builder()
                    .setName(universeName)
                    .setBlx(businessLayer)
                    .build();

            return universe;

        } catch (SlException e) {
            LOGGER.severe("Error retrieving universe: " + e.getMessage());
            return null;
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

    /**
     * Helper method to find the latest universe folder based on timestamp.
     *
     * @param universesDir The base universes directory.
     * @param universeName The name of the universe to filter folders.
     * @return The latest universe folder, or null if none found.
     */
    private File getLatestUniverseFolder(File universesDir) {
        // Filter directories that match the universe name pattern
        File[] universeFolders = universesDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                File potentialDir = new File(dir, name);
                return potentialDir.isDirectory();
            }
        });

        if (universeFolders == null || universeFolders.length == 0) {
            return null;
        }

        // Sort the folders by last modified date in descending order
        Arrays.sort(universeFolders, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });

        // The first element after sorting is the latest folder
        return universeFolders[0];
    }
}

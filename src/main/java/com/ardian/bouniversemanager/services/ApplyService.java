package main.java.com.ardian.bouniversemanager.services;

import java.io.File;
import java.io.IOException;
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
                    BlContainer newParent = (BlContainer) findItemByIdentifier(serverUniverse.getBlx().getRootFolder(),
                            moveDetail.getNewParentIdentifier());
                    
                    // Delete the item from the old parent
                    moveDetail.getItem().getParent().getChildren().remove(moveDetail.getItem());

                    // Add the item to the new parent
                    newParent.getChildren().add(moveDetail.getItem());

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
                            // Add cases for other fields as needed
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
                    newParent.getChildren().add(newItem);
                } else if (detail instanceof DeleteChangeDetail) {
                    DeleteChangeDetail deleteDetail = (DeleteChangeDetail) detail;
                    deleteDetail.getItem().getParent().getChildren().remove(deleteDetail.getItem());
                }
            }

            // 4. Update the local item in the local universe
            LocalResourceService localResourceService = context.getService(LocalResourceService.class);

            localResourceService.save(serverUniverse.getBlx(), "universes" + File.separator + serverUniverse.getName() + ".blx", true);
        }  
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
}

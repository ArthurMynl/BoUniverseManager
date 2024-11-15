package main.java.com.ardian.bouniversemanager.utils;

import java.util.logging.Logger;

import com.sap.sl.sdk.authoring.businesslayer.RelationalBusinessLayer;
import com.sap.sl.sdk.authoring.businesslayer.RootFolder;

import main.java.com.ardian.bouniversemanager.comparison.ChangeSet;
import main.java.com.ardian.bouniversemanager.comparison.FolderComparator;
import main.java.com.ardian.bouniversemanager.models.Universe;

public class ComparisonUtil {

    private static final Logger LOGGER = Logger.getLogger(ComparisonUtil.class.getName());

    public static ChangeSet compareUniverses(Universe local, Universe server) {
        ChangeSet changeSet = new ChangeSet();

        RelationalBusinessLayer localBlx = local.getBlx();
        RelationalBusinessLayer serverBlx = server.getBlx();

        RootFolder localRootFolder = (RootFolder) localBlx.getRootFolder();
        RootFolder serverRootFolder = (RootFolder) serverBlx.getRootFolder();

        LOGGER.info("Comparing universes...");
        // LOGGER.info("Local universe name: " + local.getName());
        // LOGGER.info("Server universe name: " + server.getName());
        // LOGGER.info("Local root folder id: " + localRootFolder.getIdentifier());
        // LOGGER.info("Server root folder id: " + serverRootFolder.getIdentifier());
        // LOGGER.info(
        // (localRootFolder.getChildren().size() ==
        // serverRootFolder.getChildren().size()) ? "Universes are equal"
        // : "Universes are not equal");
        // LOGGER.info(localRootFolder.getChildren().get(0).getIdentifier() + " - "
        // + localRootFolder.getChildren().get(0).getName());
        // LOGGER.info(serverRootFolder.getChildren().get(0).getIdentifier() + " - "
        // + serverRootFolder.getChildren().get(0).getName());

        FolderComparator comparator = new FolderComparator();
        ChangeSet changes = comparator.compareRootFolders(localRootFolder, serverRootFolder);
        changeSet.addAll(changes);

        return changeSet;
    }


}

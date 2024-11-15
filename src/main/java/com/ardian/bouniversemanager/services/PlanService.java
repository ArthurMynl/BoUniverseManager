package main.java.com.ardian.bouniversemanager.services;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.sap.sl.sdk.authoring.businesslayer.RelationalBusinessLayer;
import com.sap.sl.sdk.authoring.cms.CmsResourceService;
import com.sap.sl.sdk.authoring.local.LocalResourceService;
import com.sap.sl.sdk.framework.SlContext;
import com.sap.sl.sdk.framework.SlException;

import main.java.com.ardian.bouniversemanager.comparison.ChangeSet;
import main.java.com.ardian.bouniversemanager.connection.BoConnectionManager;
import main.java.com.ardian.bouniversemanager.models.Universe;
import main.java.com.ardian.bouniversemanager.utils.ComparisonUtil;
import main.java.com.ardian.bouniversemanager.utils.ExcelUtil;

public class PlanService {

    private static final Logger LOGGER = Logger.getLogger(PlanService.class.getName());

    public void compareUniverses(File inputFile) throws Exception {
        BoConnectionManager connection = new BoConnectionManager();

        try {
            Universe serverUniverse = fetchServerUniverse(connection.getContext(),
                    inputFile.getName().replace(".xlsx", ""));
            Universe localUniverse = ExcelUtil.readUniverseFromExcel(inputFile, connection.getContext(),
                    serverUniverse);

            ChangeSet changes = ComparisonUtil.compareUniverses(localUniverse, serverUniverse);

            LOGGER.info(changes.toString());
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        } finally {
            connection.disconnect();
        }
    }

    private Universe fetchServerUniverse(SlContext context, String universeName) {
        CmsResourceService resourceService = context.getService(CmsResourceService.class);

        try {
            String retrievedUniverse = resourceService.retrieveUniverse("/Universes/" + universeName + ".unx",
                    "universes", true);
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
}

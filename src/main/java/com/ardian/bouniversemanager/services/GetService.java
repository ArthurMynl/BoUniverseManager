package main.java.com.ardian.bouniversemanager.services;

import java.nio.file.Path;
import java.util.logging.Logger;

import com.sap.sl.sdk.authoring.businesslayer.RelationalBusinessLayer;
import com.sap.sl.sdk.authoring.cms.CmsResourceService;
import com.sap.sl.sdk.authoring.local.LocalResourceService;
import com.sap.sl.sdk.framework.SlContext;
import com.sap.sl.sdk.framework.SlException;

import main.java.com.ardian.bouniversemanager.connection.BoConnectionManager;
import main.java.com.ardian.bouniversemanager.models.Universe;
import main.java.com.ardian.bouniversemanager.utils.ExcelUtil;

public class GetService {

    private static final Logger LOGGER = Logger.getLogger(GetService.class.getName());

    public void generateExcelFile(String universeName, Path outputPath) {
        BoConnectionManager connection = new BoConnectionManager();
        
        // Fetch universe data from BO server
        Universe universe = fetchUniverseData(universeName, connection.getContext());

        // Write data to Excel file using Apache POI
        ExcelUtil.writeUniverseToExcel(universe, outputPath);
        connection.disconnect();
    }

    private Universe fetchUniverseData(String universeName, SlContext context) {
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

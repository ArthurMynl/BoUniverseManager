package main.java.com.ardian.bouniversemanager.fieldvalueprovider.handlers;

import java.util.logging.Logger;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;

import main.java.com.ardian.bouniversemanager.fieldvalueprovider.FieldValueProvider;

public class DefaultFieldValueProvider implements FieldValueProvider {
    private static final Logger LOGGER = Logger.getLogger(DefaultFieldValueProvider.class.getName());

    @Override
    public String getFieldValue(BlItem item, String fieldName) {
        switch (fieldName) {
            case "name":
                return item.getName();
            case "description":
                return item.getDescription();
            default:
                LOGGER.severe("Unknown field name: " + fieldName);
                return null; 
        }
    }
}

package main.java.com.ardian.bouniversemanager.fieldvalueprovider;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;

public interface FieldValueProvider {
    /**
     * Retrieves the value of the specified field from the given BlItem.
     *
     * @param item      The BlItem instance.
     * @param fieldName The name of the field to retrieve.
     * @return The field value as a String, or a default value if not applicable.
     */
    String getFieldValue(BlItem item, String fieldName);
}


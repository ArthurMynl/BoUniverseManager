package main.java.com.ardian.bouniversemanager.comparison;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;

import main.java.com.ardian.bouniversemanager.fieldvalueprovider.FieldValueProvider;
import main.java.com.ardian.bouniversemanager.fieldvalueprovider.FieldValueProviderFactory;

public class UpdateChangeDetail implements ChangeDetail {
    private ChangeType type;
    private BlItem oldItem;
    private BlItem newItem;
    private String field;

    public UpdateChangeDetail(BlItem oldItem, BlItem newItem, String field) {
        this.type = ChangeType.UPDATE;
        this.field = field;
        this.oldItem = oldItem;
        this.newItem = newItem;
    }

    public BlItem getOldItem() {
        return oldItem;
    }

    public BlItem getNewItem() {
        return newItem;
    }

    public String getField() {
        return field;
    }

    @Override
    public ChangeType getType() {
        return type;
    }

    /**
     * Helper method to retrieve the value of the specified field from a BlItem.
     *
     * @param item      The BlItem from which to retrieve the field value.
     * @param fieldName The name of the field whose value is to be retrieved.
     * @return The value of the field as a String, or "null" if the item is null.
     */
    private String getFieldValue(BlItem item, String fieldName) {
        if (item == null) {
            return "null";
        }
        FieldValueProvider provider = FieldValueProviderFactory.getProvider(item);
        return provider.getFieldValue(item, fieldName);
    }

    @Override
    public String toString() {
        String oldValue = getFieldValue(oldItem, field);
        String newValue = getFieldValue(newItem, field);
        return type + ": Updated item field '" + field + "': '" + oldValue + "' to '" + newValue + "'";
    }
}

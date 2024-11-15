package main.java.com.ardian.bouniversemanager.comparison;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;

public class AddChangeDetail implements ChangeDetail {
    private ChangeType type;
    private BlItem item;
    private String path;
    private String parentIdentifier;

    public AddChangeDetail(BlItem item, String path, String parentIdentifier) {
        this.item = item;
        this.type = ChangeType.ADD;
        this.path = path;
        this.parentIdentifier = parentIdentifier;
    }

    public BlItem getItem() {
        return item;
    }

    public String getPath() {
        return path;
    }

    public String getParentIdentifier() {
        return parentIdentifier;
    }

    @Override
    public ChangeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type + ": Added item: '" + (item != null ? item.getName() : "null") + "' to '" + path + "'";
    }
}
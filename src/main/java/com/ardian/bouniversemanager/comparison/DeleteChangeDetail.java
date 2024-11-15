package main.java.com.ardian.bouniversemanager.comparison;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;

public class DeleteChangeDetail implements ChangeDetail {
    private ChangeType type;
    private BlItem item;
    private String path;

    public DeleteChangeDetail(BlItem item, String path) {
        this.item = item;
        this.type = ChangeType.DELETE;
        this.path = path;
    }

    public BlItem getItem() {
        return item;
    }

    @Override
    public ChangeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type + ": Deleted item: '" + (item != null ? item.getName() : "null") + "' in '" + path + "'";
    }
}
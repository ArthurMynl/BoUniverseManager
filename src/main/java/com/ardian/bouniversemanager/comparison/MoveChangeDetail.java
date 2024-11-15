package main.java.com.ardian.bouniversemanager.comparison;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;

public class MoveChangeDetail implements ChangeDetail {
    private ChangeType type;
    private String oldPath;
    private String newPath;
    private BlItem item;
    private String newParentIdentifier;

    public MoveChangeDetail(String oldPath, String newPath, BlItem item, String newParentIdentifier) {
        this.type = ChangeType.MOVE;
        this.oldPath = oldPath;
        this.newPath = newPath;
        this.item = item;
        this.newParentIdentifier = newParentIdentifier;

        System.out.println("Item created with the following parent : " + this.item.getParent().getName());
    }

    public BlItem getItem() {
        return item;
    }

    public String getNewParentIdentifier() {
        return newParentIdentifier;
    }

    @Override
    public ChangeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type + ": Moved item '" + item.getName() + "' from '" + oldPath + "' to '" + newPath + "'";
    }
}

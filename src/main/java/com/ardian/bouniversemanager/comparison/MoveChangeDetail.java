package main.java.com.ardian.bouniversemanager.comparison;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;

public class MoveChangeDetail implements ChangeDetail {
    private ChangeType type;
    private BlItem item;
    private String oldPath;
    private String newPath;
    private String newParentIdentifier;
    private int oldPosition;
    private int newPosition;

    public MoveChangeDetail(String oldPath, String newPath, BlItem item, String newParentIdentifier, int oldPosition, int newPosition) {
        this.type = ChangeType.MOVE;
        this.item = item;
        this.oldPath = oldPath;
        this.newPath = newPath;
        this.newParentIdentifier = newParentIdentifier;
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }

    public BlItem getItem() {
        return item;
    }

    public String getOldPath() {
        return oldPath;
    }

    public String getNewPath() {
        return newPath;
    }

    public String getNewParentIdentifier() {
        return newParentIdentifier;
    }

    public int getOldPosition() {
        return oldPosition;
    }

    public int getNewPosition() {
        return newPosition;
    }

    @Override
    public ChangeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type + ": Moved item '" + item.getName() + "' from '" + oldPath + "' at position " + (oldPosition + 1) + " to '"
                + newPath + "' at position " + (newPosition + 1);
    }
}

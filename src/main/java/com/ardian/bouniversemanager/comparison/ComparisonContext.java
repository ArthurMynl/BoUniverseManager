package main.java.com.ardian.bouniversemanager.comparison;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;

public class ComparisonContext {
    private final String identifier;
    private final BlItem localItem;
    private final BlItem serverItem;
    private final ChangeSet changes;

    public ComparisonContext(String identifier, BlItem localItem, BlItem serverItem, ChangeSet changes) {
        this.identifier = identifier;
        this.localItem = localItem;
        this.serverItem = serverItem;
        this.changes = changes;
    }

    public String getIdentifier() {
        return identifier;
    }

    public BlItem getLocalItem() {
        return localItem;
    }

    public BlItem getServerItem() {
        return serverItem;
    }

    public ChangeSet getChanges() {
        return changes;
    }
}

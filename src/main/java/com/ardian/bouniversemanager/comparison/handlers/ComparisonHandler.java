package main.java.com.ardian.bouniversemanager.comparison.handlers;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;

import main.java.com.ardian.bouniversemanager.comparison.ChangeSet;

public interface ComparisonHandler {
    /**
     * Compare fields between local and server items.
     *
     * @param localItem   The local BlItem instance.
     * @param serverItem  The server BlItem instance.
     * @param identifier  The unique identifier for the item.
     * @param changes     The collection to record changes.
     * @return true if any change was detected, false otherwise.
     */
    boolean compareFields(BlItem localItem, BlItem serverItem, String identifier, ChangeSet changes);
}

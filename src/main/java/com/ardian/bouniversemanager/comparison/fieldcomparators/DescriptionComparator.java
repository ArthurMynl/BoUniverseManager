package main.java.com.ardian.bouniversemanager.comparison.fieldcomparators;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;

import main.java.com.ardian.bouniversemanager.comparison.Change;
import main.java.com.ardian.bouniversemanager.comparison.ChangeSet;
import main.java.com.ardian.bouniversemanager.comparison.ComparisonContext;
import main.java.com.ardian.bouniversemanager.comparison.FieldComparator;
import main.java.com.ardian.bouniversemanager.comparison.UpdateChangeDetail;

public class DescriptionComparator implements FieldComparator {
    @Override
    public boolean compare(ComparisonContext context) {
        BlItem localItem = context.getLocalItem();
        BlItem serverItem = context.getServerItem();
        String identifier = context.getIdentifier();
        ChangeSet changes = context.getChanges();

        String localDescription = localItem.getDescription() != null ? localItem.getDescription() : "";
        String serverDescription = serverItem.getDescription() != null ? serverItem.getDescription() : "";

        if (!localDescription.equals(serverDescription)) {
            Change change = changes.getChange(identifier);
            if (change == null) {
                change = new Change(identifier, localItem.getName());
                changes.addChange(change);
            }
            change.addChangeDetail(new UpdateChangeDetail(serverItem, localItem, "description"));
            return true;
        }
        return false;
    }
}


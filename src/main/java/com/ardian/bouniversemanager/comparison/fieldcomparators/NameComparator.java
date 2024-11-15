package main.java.com.ardian.bouniversemanager.comparison.fieldcomparators;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;

import main.java.com.ardian.bouniversemanager.comparison.Change;
import main.java.com.ardian.bouniversemanager.comparison.ChangeSet;
import main.java.com.ardian.bouniversemanager.comparison.ComparisonContext;
import main.java.com.ardian.bouniversemanager.comparison.FieldComparator;
import main.java.com.ardian.bouniversemanager.comparison.UpdateChangeDetail;

public class NameComparator implements FieldComparator {
    @Override
    public boolean compare(ComparisonContext context) {
        BlItem localItem = context.getLocalItem();
        BlItem serverItem = context.getServerItem();
        String identifier = context.getIdentifier();
        ChangeSet changes = context.getChanges();

        String localNameValue = localItem.getName();
        String serverNameValue = serverItem.getName();

        if (!localNameValue.equals(serverNameValue)) {
            Change change = changes.getChange(identifier);
            if (change == null) {
                change = new Change(identifier, localNameValue);
                changes.addChange(change);
            }
            change.addChangeDetail(new UpdateChangeDetail(serverItem, localItem, "name"));
            return true;
        }
        return false;
    }
}

package main.java.com.ardian.bouniversemanager.comparison.fieldcomparators;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;
import com.sap.sl.sdk.authoring.businesslayer.BusinessFilter;
import com.sap.sl.sdk.authoring.businesslayer.NativeRelationalFilter;

import main.java.com.ardian.bouniversemanager.comparison.Change;
import main.java.com.ardian.bouniversemanager.comparison.ChangeSet;
import main.java.com.ardian.bouniversemanager.comparison.ComparisonContext;
import main.java.com.ardian.bouniversemanager.comparison.FieldComparator;
import main.java.com.ardian.bouniversemanager.comparison.UpdateChangeDetail;

public class FilterTypeComparator implements FieldComparator {
    @Override
    public boolean compare(ComparisonContext context) {
        BlItem localItem = context.getLocalItem();
        BlItem serverItem = context.getServerItem();
        String identifier = context.getIdentifier();
        ChangeSet changes = context.getChanges();

        String localType = null;
        String serverType = null;

        if (localItem instanceof BusinessFilter) {
            localType = BusinessFilter.class.getSimpleName();
        } else if (localItem instanceof NativeRelationalFilter) {
            localType = NativeRelationalFilter.class.getSimpleName();
        }

        if (serverItem instanceof BusinessFilter) {
            serverType = BusinessFilter.class.getSimpleName();
        } else if (serverItem instanceof NativeRelationalFilter) {
            serverType = NativeRelationalFilter.class.getSimpleName();
        }

        if (localType == null || serverType == null) {
            // Handle nulls as needed (e.g., treat null as a specific value or skip)
            return false;
        }

        if (!localType.equals(serverType)) {
            Change change = changes.getChange(identifier);
            if (change == null) {
                change = new Change(identifier, localItem.getName());
                changes.addChange(change);
            }
            change.addChangeDetail(new UpdateChangeDetail(serverItem, localItem, "filterType"));
            return true;
        }
        return false;
    }
}

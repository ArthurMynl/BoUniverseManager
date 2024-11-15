package main.java.com.ardian.bouniversemanager.comparison.fieldcomparators;


import com.sap.sl.sdk.authoring.businesslayer.BlItem;
import com.sap.sl.sdk.authoring.businesslayer.DataType;
import com.sap.sl.sdk.authoring.businesslayer.Dimension;
import com.sap.sl.sdk.authoring.businesslayer.Measure;

import main.java.com.ardian.bouniversemanager.comparison.Change;
import main.java.com.ardian.bouniversemanager.comparison.ChangeSet;
import main.java.com.ardian.bouniversemanager.comparison.ComparisonContext;
import main.java.com.ardian.bouniversemanager.comparison.FieldComparator;
import main.java.com.ardian.bouniversemanager.comparison.UpdateChangeDetail;

public class DataTypeComparator implements FieldComparator {
    @Override
    public boolean compare(ComparisonContext context) {
        BlItem localItem = context.getLocalItem();
        BlItem serverItem = context.getServerItem();
        String identifier = context.getIdentifier();
        ChangeSet changes = context.getChanges();

        DataType localDataType = null;
        DataType serverDataType = null;

        if (localItem instanceof Dimension && serverItem instanceof Dimension) {
            localDataType = ((Dimension) localItem).getDataType();
            serverDataType = ((Dimension) serverItem).getDataType();
        } else if (localItem instanceof Measure && serverItem instanceof Measure) {
            localDataType = ((Measure) localItem).getDataType();
            serverDataType = ((Measure) serverItem).getDataType();
        }

        if (localDataType == null || serverDataType == null) {
            // Handle nulls as needed (e.g., treat null as a specific value or skip)
            return false;
        }

        if (!localDataType.equals(serverDataType)) {
            Change change = changes.getChange(identifier);
            if (change == null) {
                change = new Change(identifier, localItem.getName());
                changes.addChange(change);
            }
            change.addChangeDetail(new UpdateChangeDetail(serverItem, localItem, "dataType"));
            return true;
        }

        return false;
    }
}


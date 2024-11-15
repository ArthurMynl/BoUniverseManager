package main.java.com.ardian.bouniversemanager.comparison.fieldcomparators;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;
import com.sap.sl.sdk.authoring.businesslayer.Dimension;
import com.sap.sl.sdk.authoring.businesslayer.Measure;

import main.java.com.ardian.bouniversemanager.comparison.Change;
import main.java.com.ardian.bouniversemanager.comparison.ChangeSet;
import main.java.com.ardian.bouniversemanager.comparison.ComparisonContext;
import main.java.com.ardian.bouniversemanager.comparison.FieldComparator;
import main.java.com.ardian.bouniversemanager.comparison.UpdateChangeDetail;
import com.sap.sl.sdk.authoring.businesslayer.RelationalBinding;


public class SelectComparator implements FieldComparator {
    @Override
    public boolean compare(ComparisonContext context) {
        BlItem localItem = context.getLocalItem();
        BlItem serverItem = context.getServerItem();
        String identifier = context.getIdentifier();
        ChangeSet changes = context.getChanges();

        String localSelect = null;
        String serverSelect = null;

        if (localItem instanceof Dimension && serverItem instanceof Dimension) {
            localSelect = ((RelationalBinding) ((Dimension) localItem).getBinding()).getSelect();
            serverSelect = ((RelationalBinding) ((Dimension) serverItem).getBinding()).getSelect();
        } else if (localItem instanceof Measure && serverItem instanceof Measure) {
            localSelect = ((RelationalBinding) ((Measure) localItem).getBinding()).getSelect();
            serverSelect = ((RelationalBinding) ((Measure) serverItem).getBinding()).getSelect();
        }

        if (localSelect == null || serverSelect == null) {
            // Handle nulls as needed (e.g., treat null as a specific value or skip)
            return false;
        }

        if (!localSelect.equals(serverSelect)) {
            Change change = changes.getChange(identifier);
            if (change == null) {
                change = new Change(identifier, localItem.getName());
                changes.addChange(change);
            }
            change.addChangeDetail(new UpdateChangeDetail(serverItem, localItem, "select"));
            return true;
        }

        return false;
    }
}
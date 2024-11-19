package main.java.com.ardian.bouniversemanager.comparison.fieldcomparators;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;
import com.sap.sl.sdk.authoring.businesslayer.BusinessFilter;
import com.sap.sl.sdk.authoring.businesslayer.Dimension;
import com.sap.sl.sdk.authoring.businesslayer.Measure;
import com.sap.sl.sdk.authoring.businesslayer.NativeRelationalFilter;
import com.sap.sl.sdk.authoring.businesslayer.RelationalBinding;

import main.java.com.ardian.bouniversemanager.comparison.Change;
import main.java.com.ardian.bouniversemanager.comparison.ChangeSet;
import main.java.com.ardian.bouniversemanager.comparison.ComparisonContext;
import main.java.com.ardian.bouniversemanager.comparison.FieldComparator;
import main.java.com.ardian.bouniversemanager.comparison.UpdateChangeDetail;

public class WhereComparator implements FieldComparator {
    @Override
    public boolean compare(ComparisonContext context) {
        BlItem localItem = context.getLocalItem();
        BlItem serverItem = context.getServerItem();
        String identifier = context.getIdentifier();
        ChangeSet changes = context.getChanges();

        String localWhere = null;
        String serverWhere = null;

        if (localItem instanceof BusinessFilter) {
            localWhere = BusinessFilter.class.getSimpleName();
        } else if (localItem instanceof NativeRelationalFilter) {
            localWhere = NativeRelationalFilter.class.getSimpleName();
        }

        if (serverItem instanceof BusinessFilter) {
            serverWhere = BusinessFilter.class.getSimpleName();
        } else if (serverItem instanceof NativeRelationalFilter) {
            serverWhere = NativeRelationalFilter.class.getSimpleName();
        }

        if (localItem instanceof Dimension) {
            Dimension dimension = (Dimension) localItem;
            RelationalBinding binding = (RelationalBinding) dimension.getBinding();
            localWhere = binding.getWhere();
        } else if (localItem instanceof Measure) {
            Measure measure = (Measure) localItem;
            RelationalBinding binding = (RelationalBinding) measure.getBinding();
            localWhere = binding.getWhere();
        }

        if (serverItem instanceof Dimension) {
            Dimension dimension = (Dimension) serverItem;
            RelationalBinding binding = (RelationalBinding) dimension.getBinding();
            serverWhere = binding.getWhere();
        } else if (serverItem instanceof Measure) {
            Measure measure = (Measure) serverItem;
            RelationalBinding binding = (RelationalBinding) measure.getBinding();
            serverWhere = binding.getWhere();
        }

        if (localWhere == null || serverWhere == null) {
            // Handle nulls as needed (e.g., treat null as a specific value or skip)
            return false;
        }

        if (!localWhere.equals(serverWhere)) {
            Change change = changes.getChange(identifier);
            if (change == null) {
                change = new Change(identifier, localItem.getName());
                changes.addChange(change);
            }
            change.addChangeDetail(new UpdateChangeDetail(serverItem, localItem, "where"));
            return true;
        }

        return false;
    }
}

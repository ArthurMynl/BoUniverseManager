package main.java.com.ardian.bouniversemanager.comparison.fieldcomparators;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;

import main.java.com.ardian.bouniversemanager.comparison.Change;
import main.java.com.ardian.bouniversemanager.comparison.ChangeSet;
import main.java.com.ardian.bouniversemanager.comparison.ComparisonContext;
import main.java.com.ardian.bouniversemanager.comparison.FieldComparator;
import main.java.com.ardian.bouniversemanager.comparison.MoveChangeDetail;

public class ParentPathComparator implements FieldComparator {
    @Override
    public boolean compare(ComparisonContext context) {
        BlItem localItem = context.getLocalItem();
        BlItem serverItem = context.getServerItem();
        String identifier = context.getIdentifier();
        ChangeSet changes = context.getChanges();

        String localParentPath = localItem.getParent().getIdentifier();
        String serverParentPath = serverItem.getParent().getIdentifier();

        if (!localParentPath.equals(serverParentPath)) {
            Change change = changes.getChange(identifier);
            if (change == null) {
                change = new Change(identifier, localItem.getName());
                changes.addChange(change);
            }
            change.addChangeDetail(new MoveChangeDetail(serverParentPath, localParentPath, serverItem, localItem.getParent().getIdentifier()));
            return true;
        }
        return false;
    }
}

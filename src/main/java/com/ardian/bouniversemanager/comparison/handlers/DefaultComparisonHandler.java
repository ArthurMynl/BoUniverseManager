package main.java.com.ardian.bouniversemanager.comparison.handlers;
import java.util.ArrayList;
import java.util.List;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;

import main.java.com.ardian.bouniversemanager.comparison.ChangeSet;
import main.java.com.ardian.bouniversemanager.comparison.ComparisonContext;
import main.java.com.ardian.bouniversemanager.comparison.FieldComparator;
import main.java.com.ardian.bouniversemanager.comparison.fieldcomparators.DescriptionComparator;
import main.java.com.ardian.bouniversemanager.comparison.fieldcomparators.NameComparator;
import main.java.com.ardian.bouniversemanager.comparison.fieldcomparators.ParentPathComparator;

public class DefaultComparisonHandler implements ComparisonHandler {
    private final List<FieldComparator> fieldComparators;

    public DefaultComparisonHandler() {
        this.fieldComparators = new ArrayList<>();
        // Common field comparators
        this.fieldComparators.add(new ParentPathComparator());
        this.fieldComparators.add(new NameComparator());
        this.fieldComparators.add(new DescriptionComparator());
    }

    @Override
    public boolean compareFields(BlItem localItem, BlItem serverItem, String identifier, ChangeSet changes) {
        System.out.println("Comparing fields for item: " + localItem.getName());
        boolean hasChange = false;
        for (FieldComparator comparator : fieldComparators) {
            boolean changed = comparator.compare(new ComparisonContext(identifier, localItem, serverItem, changes));
            hasChange = hasChange || changed;
        }
        return hasChange;
    }
}

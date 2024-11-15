package main.java.com.ardian.bouniversemanager.comparison.handlers;

import java.util.ArrayList;
import java.util.List;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;

import main.java.com.ardian.bouniversemanager.comparison.ChangeSet;
import main.java.com.ardian.bouniversemanager.comparison.ComparisonContext;
import main.java.com.ardian.bouniversemanager.comparison.FieldComparator;
import main.java.com.ardian.bouniversemanager.comparison.fieldcomparators.DataTypeComparator;
import main.java.com.ardian.bouniversemanager.comparison.fieldcomparators.SelectComparator;

public class MeasureComparisonHandler implements ComparisonHandler {
    private final ComparisonHandler defaultHandler;
    private final List<FieldComparator> fieldComparators;

    public MeasureComparisonHandler() {
        this.defaultHandler = new DefaultComparisonHandler();
        this.fieldComparators = new ArrayList<>();
        // Measure-specific field comparators
        this.fieldComparators.add(new DataTypeComparator());
        this.fieldComparators.add(new SelectComparator());
    }

    @Override
    public boolean compareFields(BlItem localItem, BlItem serverItem, String identifier, ChangeSet changes) {
        boolean hasChange = defaultHandler.compareFields(localItem, serverItem, identifier, changes);
        for (FieldComparator comparator : fieldComparators) {
            boolean changed = comparator.compare(new ComparisonContext(identifier, localItem, serverItem, changes));
            hasChange = hasChange || changed;
        }
        return hasChange;
    }
}

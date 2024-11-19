package main.java.com.ardian.bouniversemanager.fieldvalueprovider;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;
import com.sap.sl.sdk.authoring.businesslayer.Dimension;
import com.sap.sl.sdk.authoring.businesslayer.Filter;
import com.sap.sl.sdk.authoring.businesslayer.Measure;

import main.java.com.ardian.bouniversemanager.fieldvalueprovider.handlers.DefaultFieldValueProvider;
import main.java.com.ardian.bouniversemanager.fieldvalueprovider.handlers.DimensionFieldValueProvider;
import main.java.com.ardian.bouniversemanager.fieldvalueprovider.handlers.FilterFieldValueProvider; 
import main.java.com.ardian.bouniversemanager.fieldvalueprovider.handlers.MeasureFieldValueProvider;

public class FieldValueProviderFactory {
    private static final FieldValueProvider DEFAULT_PROVIDER = new DefaultFieldValueProvider();
    private static final FieldValueProvider DIMENSION_PROVIDER = new DimensionFieldValueProvider();
    private static final FieldValueProvider MEASURE_PROVIDER = new MeasureFieldValueProvider();
    private static final FieldValueProvider FILTER_PROVIDER = new FilterFieldValueProvider();

    /**
     * Returns the appropriate FieldValueProvider based on the type of BlItem.
     *
     * @param item The BlItem instance.
     * @return The corresponding FieldValueProvider.
     */
    public static FieldValueProvider getProvider(BlItem item) {
        if (item instanceof Dimension) {
            return DIMENSION_PROVIDER;
        } else if (item instanceof Measure) {
            return MEASURE_PROVIDER;
        }  else if (item instanceof Filter) {
            return FILTER_PROVIDER;
        } else {
            return DEFAULT_PROVIDER;
        }
    }
}

package main.java.com.ardian.bouniversemanager.comparison.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;
import com.sap.sl.sdk.authoring.businesslayer.Dimension;
import com.sap.sl.sdk.authoring.businesslayer.Filter;
import com.sap.sl.sdk.authoring.businesslayer.Measure;

import main.java.com.ardian.bouniversemanager.comparison.handlers.ComparisonHandler;
import main.java.com.ardian.bouniversemanager.comparison.handlers.DefaultComparisonHandler;
import main.java.com.ardian.bouniversemanager.comparison.handlers.DimensionComparisonHandler;
import main.java.com.ardian.bouniversemanager.comparison.handlers.FilterComparisonHandler;
import main.java.com.ardian.bouniversemanager.comparison.handlers.MeasureComparisonHandler;

public class ComparisonHandlerFactory {
    private static final Map<Class<? extends BlItem>, ComparisonHandler> handlerMap = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(ComparisonHandlerFactory.class.getName());

    static {
        // Register handlers for each BlItem type
        handlerMap.put(Dimension.class, new DimensionComparisonHandler());
        handlerMap.put(Measure.class, new MeasureComparisonHandler());
        handlerMap.put(Filter.class, new FilterComparisonHandler());
    }

    /**
     * Retrieves the appropriate ComparisonHandler based on the BlItem type.
     *
     * @param item The BlItem instance.
     * @return The corresponding ComparisonHandler.
     */
    public static ComparisonHandler getHandler(BlItem item) {
        // Iterate through the handlerMap and find the first matching handler based on type hierarchy
        Optional<Map.Entry<Class<? extends BlItem>, ComparisonHandler>> handlerEntry = handlerMap.entrySet().stream()
            .filter(entry -> entry.getKey().isAssignableFrom(item.getClass()))
            .findFirst();

        if (handlerEntry.isPresent()) {
            return handlerEntry.get().getValue();
        } else {
            LOGGER.info("No specific handler found for type: " + item.getClass().getSimpleName() + ". Using DefaultComparisonHandler.");
            return new DefaultComparisonHandler();
        }
    }
}


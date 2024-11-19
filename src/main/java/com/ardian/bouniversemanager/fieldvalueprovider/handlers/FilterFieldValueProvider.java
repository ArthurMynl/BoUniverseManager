package main.java.com.ardian.bouniversemanager.fieldvalueprovider.handlers;

import com.sap.sl.sdk.authoring.businesslayer.BlItem;
import com.sap.sl.sdk.authoring.businesslayer.BusinessFilter;
import com.sap.sl.sdk.authoring.businesslayer.NativeRelationalFilter;
import com.sap.sl.sdk.authoring.businesslayer.RelationalBinding;

public class FilterFieldValueProvider extends DefaultFieldValueProvider {
    @Override
    public String getFieldValue(BlItem item, String fieldName) {
        if ("filterType".equals(fieldName)) {
            if (item instanceof BusinessFilter) {
                return "BUSINESS";
            } else if (item instanceof NativeRelationalFilter) {
                return "NATIVE";
            }
        } else if ("where".equals(fieldName)) {
            if (item instanceof NativeRelationalFilter) {
                NativeRelationalFilter filter = (NativeRelationalFilter) item;
                RelationalBinding binding = (RelationalBinding) filter.getBinding();
                return binding.getWhere();
            } else if (item instanceof BusinessFilter) {
                BusinessFilter filter = (BusinessFilter) item;
                return filter.getExpression();
            }
        }
        // Delegate to default handler for other fields
        return super.getFieldValue(item, fieldName);
    }
}

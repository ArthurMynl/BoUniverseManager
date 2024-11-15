package main.java.com.ardian.bouniversemanager.fieldvalueprovider.handlers;


import com.sap.sl.sdk.authoring.businesslayer.BlItem;
import com.sap.sl.sdk.authoring.businesslayer.DataType;
import com.sap.sl.sdk.authoring.businesslayer.Measure;
import com.sap.sl.sdk.authoring.businesslayer.RelationalBinding;

public class MeasureFieldValueProvider extends DefaultFieldValueProvider {
    @Override
    public String getFieldValue(BlItem item, String fieldName) {
        if ("dataType".equals(fieldName)) {
            Measure measure = (Measure) item;
            DataType dataType = measure.getDataType();
            return dataType != null ? dataType.toString() : "null";
        } else if ("select".equals(fieldName)) {
            Measure measure = (Measure) item;
            return ((RelationalBinding) measure.getBinding()).getSelect();
        }
        // Delegate to default handler for other fields
        return super.getFieldValue(item, fieldName);
    }
}

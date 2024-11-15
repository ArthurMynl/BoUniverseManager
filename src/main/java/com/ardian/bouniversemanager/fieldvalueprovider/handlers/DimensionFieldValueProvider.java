package main.java.com.ardian.bouniversemanager.fieldvalueprovider.handlers;
import com.sap.sl.sdk.authoring.businesslayer.BlItem;
import com.sap.sl.sdk.authoring.businesslayer.DataType;
import com.sap.sl.sdk.authoring.businesslayer.Dimension;
import com.sap.sl.sdk.authoring.businesslayer.RelationalBinding;

public class DimensionFieldValueProvider extends DefaultFieldValueProvider {
    @Override
    public String getFieldValue(BlItem item, String fieldName) {
        if ("dataType".equals(fieldName)) {
            Dimension dimension = (Dimension) item;
            DataType dataType = dimension.getDataType();
            return dataType != null ? dataType.toString() : "null";
        } else if ("select".equals(fieldName)) {
            Dimension dimension = (Dimension) item;
            return ((RelationalBinding)dimension.getBinding()).getSelect(); 
        }
        // Delegate to default handler for other fields
        return super.getFieldValue(item, fieldName);
    }
}
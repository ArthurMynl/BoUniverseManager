package main.java.com.ardian.bouniversemanager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Stack;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sap.sl.sdk.authoring.businesslayer.BlContainer;
import com.sap.sl.sdk.authoring.businesslayer.BlItem;
import com.sap.sl.sdk.authoring.businesslayer.BusinessFilter;
import com.sap.sl.sdk.authoring.businesslayer.BusinessLayerFactory;
import com.sap.sl.sdk.authoring.businesslayer.DataType;
import com.sap.sl.sdk.authoring.businesslayer.Dimension;
import com.sap.sl.sdk.authoring.businesslayer.Filter;
import com.sap.sl.sdk.authoring.businesslayer.Folder;
import com.sap.sl.sdk.authoring.businesslayer.Measure;
import com.sap.sl.sdk.authoring.businesslayer.NativeRelationalFilter;
import com.sap.sl.sdk.authoring.businesslayer.RelationalBinding;
import com.sap.sl.sdk.authoring.businesslayer.RelationalBusinessLayer;
import com.sap.sl.sdk.authoring.businesslayer.RootFolder;
import com.sap.sl.sdk.framework.IStatus;
import com.sap.sl.sdk.framework.Severity;
import com.sap.sl.sdk.framework.SlContext;

import main.java.com.ardian.bouniversemanager.models.Universe;

public class ExcelUtil {

    private static final Logger LOGGER = Logger.getLogger(ExcelUtil.class.getName());
    private static final String ITEM_TYPE_FOLDER = "FOLDER";
    private static final String ITEM_TYPE_DIMENSION = "DIMENSION";
    private static final String ITEM_TYPE_MEASURE = "MEASURE";
    private static final String ITEM_TYPE_FILTER = "FILTER";

    private static int currentRowNum = 1;

    public static void writeUniverseToExcel(Universe universe, Path filePath) {

        RootFolder rootFolder = universe.getBlx().getRootFolder();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("BLX Export");
        CellStyle darkStyle = workbook.createCellStyle();
        darkStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        darkStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Step 1: Calculate max depth of the BLX structure
        int maxDepth = getMaxDepth(rootFolder, 0);
        System.out.println("Max depth: " + maxDepth);

        // Step 2: Create header row
        Row headerRow = sheet.createRow(0);

        Cell fileTreeHeaderCell = headerRow.createCell(0);
        fileTreeHeaderCell.setCellValue("FILE TREE");

        // Style for the header
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        fileTreeHeaderCell.setCellStyle(headerStyle);

        // Set detail headers
        String[] detailHeaders = { "ID", "DESCRIPTION", "ITEM TYPE", "DATA TYPE", "SELECT", "WHERE" };
        for (int i = 0; i < detailHeaders.length; i++) {
            Cell cell = headerRow.createCell(maxDepth + i);
            cell.setCellValue(detailHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        // Step 3: Write BLX items data
        currentRowNum = 1; // Reset row counter
        for (BlItem child : rootFolder.getChildren()) {
            writeBlxItemsToSheet(child, sheet, 0, maxDepth, darkStyle);
        }

        // Step 4: Add double border to separate sections
        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setBorderRight(BorderStyle.DOUBLE);

        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell borderCell = row.getCell(maxDepth - 1);
                if (borderCell == null) {
                    borderCell = row.createCell(maxDepth - 1);
                }
                borderCell.setCellStyle(borderStyle);
            }
        }

        // Step 5: Adjust column widths
        int totalColumns = maxDepth + 6; // 6 = number of detail headers (ID, DESCRIPTION, etc.)
        for (int i = 0; i < totalColumns; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath.toString())) {
            workbook.write(fileOut);
            LOGGER.info("Workbook saved successfully.");
        } catch (IOException e) {
            LOGGER.severe("Error while saving the workbook: " + e.getMessage());
        } finally {
            try {
                workbook.close();
            } catch (IOException ex) {
                LOGGER.severe("Error while closing the workbook: " + ex.getMessage());
            }
        }

    }

    private static int getMaxDepth(BlItem blItem, int depth) {
        if (blItem instanceof Folder) {
            int maxDepth = depth;
            for (BlItem child : ((Folder) blItem).getChildren()) {
                int childDepth = getMaxDepth(child, depth + 1);
                maxDepth = Math.max(maxDepth, childDepth);
            }
            return maxDepth;
        } else {
            return depth;
        }
    }

    private static void writeBlxItemsToSheet(BlItem blItem, Sheet sheet, int depth, int maxDepth, CellStyle darkStyle) {
        Row row = sheet.createRow(currentRowNum++);

        // Indent by depth: place the name at the correct column for indentation
        Cell nameCell = row.createCell(depth);
        nameCell.setCellValue(blItem.getName());

        // If it's not a folder, add details
        if (!(blItem instanceof Folder)) {
            int colIndex = maxDepth; // Start from column n+1 for details

            // ID
            Cell idCell = row.createCell(colIndex++);
            idCell.setCellValue(blItem.getIdentifier());

            // DESCRIPTION
            Cell descCell = row.createCell(colIndex++);
            descCell.setCellValue(blItem.getDescription());

            // DATA TYPE, SELECT, WHERE (if applicable)
            if (blItem instanceof Dimension) {
                Dimension dimension = (Dimension) blItem;

                // ITEM TYPE
                Cell itemTypeCell = row.createCell(colIndex++);
                itemTypeCell.setCellValue("DIMENSION");

                // DATA TYPE
                Cell dataTypeCell = row.createCell(colIndex++);
                dataTypeCell.setCellValue(dimension.getDataType().toString());

                // SELECT
                RelationalBinding binding = (RelationalBinding) dimension.getBinding();
                Cell selectCell = row.createCell(colIndex++);
                selectCell.setCellValue(binding.getSelect());

                // WHERE
                Cell whereCell = row.createCell(colIndex++);
                whereCell.setCellValue(binding.getWhere());

            } else if (blItem instanceof Measure) {
                Measure measure = (Measure) blItem;

                // ITEM TYPE
                Cell itemTypeCell = row.createCell(colIndex++);
                itemTypeCell.setCellValue("MEASURE");

                // DATA TYPE
                Cell dataTypeCell = row.createCell(colIndex++);
                dataTypeCell.setCellValue(measure.getDataType().toString());

                // SELECT
                RelationalBinding binding = (RelationalBinding) measure.getBinding();
                Cell selectCell = row.createCell(colIndex++);
                selectCell.setCellValue(binding.getSelect());

                // WHERE
                Cell whereCell = row.createCell(colIndex++);
                whereCell.setCellValue(binding.getWhere());
            } else if (blItem instanceof Filter) {
                Filter filter = (Filter) blItem;

                // ITEM TYPE
                Cell itemTypeCell = row.createCell(colIndex++);
                itemTypeCell.setCellValue("FILTER");

                // FILTER TYPE
                Cell filterTypeCell = row.createCell(colIndex++);
                
                // SELECT
                Cell selectCell = row.createCell(colIndex++);
                selectCell.setCellStyle(darkStyle);

                if (filter instanceof NativeRelationalFilter) {
                    filterTypeCell.setCellValue("NATIVE");

                    // WHERE
                    NativeRelationalFilter nativeFilter = (NativeRelationalFilter) filter;
                    RelationalBinding binding = (RelationalBinding) nativeFilter.getBinding();
                    Cell whereCell = row.createCell(colIndex++);
                    whereCell.setCellValue(binding.getWhere());

                } else if (filter instanceof BusinessFilter) {
                    filterTypeCell.setCellValue("BUSINESS");

                    // WHERE
                    BusinessFilter businessFilter = (BusinessFilter) filter;
                    Cell whereCell = row.createCell(colIndex++);
                    whereCell.setCellValue(businessFilter.getExpression());
                }
            }
        } else {
            int colIndex = maxDepth; // Start from column n+1 for details

            // ID
            Cell idCell = row.createCell(colIndex++);
            idCell.setCellValue(blItem.getIdentifier());

            // DESCRIPTION
            Cell descCell = row.createCell(colIndex++);
            descCell.setCellValue(blItem.getDescription());

            // ITEM TYPE
            Cell itemTypeCell = row.createCell(colIndex++);
            itemTypeCell.setCellValue("FOLDER");

            for (int i = colIndex; i <= maxDepth + 5; i++) { // 5 = number of detail headers (ID, DESCRIPTION, etc.) - 1
                Cell cell = row.createCell(i);
                cell.setCellStyle(darkStyle);
            }
        }
        // Recursively process children if it's a folder
        if (blItem instanceof Folder) {
            Folder folder = (Folder) blItem;
            if (folder.getChildren() != null) {
                for (BlItem child : folder.getChildren()) {
                    writeBlxItemsToSheet(child, sheet, depth + 1, maxDepth, darkStyle);
                }
            }
        }
    }

    public static Universe readUniverseFromExcel(File inputFile, SlContext context, Universe serverUniverse)
            throws IOException {

        ExcelErrorSet errorSet = new ExcelErrorSet();

        RelationalBusinessLayer serverBusinessLayer = serverUniverse.getBlx();
        String dfxPath = serverBusinessLayer.getDataFoundationPath();

        // Create a new Business Layer instance
        BusinessLayerFactory blxFactory = context.getService(BusinessLayerFactory.class);
        RelationalBusinessLayer blx = blxFactory.createRelationalBusinessLayer("BLX", dfxPath);

        // BLX -> Root folder -> BLItems
        RootFolder rootFolder = blx.getRootFolder();

        try (FileInputStream fis = new FileInputStream(inputFile); XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);

            int separatorIndex = findSeparatorIndex(sheet);

            Stack<BlContainer> parentStack = new Stack<>();
            parentStack.push(rootFolder);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null)
                    continue; // Skip empty rows

                // Find the current hierarchy level based on the first non-empty cell in the
                // left part
                int currentLevel = 0;
                for (int i = 0; i < separatorIndex; i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null && !getCellValue(cell).isEmpty()) {
                        currentLevel = i;
                        break;
                    }
                }

                // Adjust the stack to match the current level
                while (parentStack.size() > currentLevel + 1) {
                    parentStack.pop(); // Pop to the correct level
                }
                BlContainer currentParent = parentStack.peek();

                // Process the item in the current row

                String itemName = getCellValue(row.getCell(currentLevel));
                if (itemName.isEmpty()) {
                    continue; // Nothing to process
                }

                String itemType = getCellValue(row.getCell(separatorIndex + 2));
                String itemID = getCellValue(row.getCell(separatorIndex));

                try {
                    switch (itemType) {
                        case ITEM_TYPE_FOLDER:
                            Folder newFolder;
                            if (!itemID.isEmpty()) {
                                newFolder = blxFactory.createBlItem(Folder.class, itemName, currentParent, itemID);
                            } else {
                                newFolder = blxFactory.createBlItem(Folder.class, itemName, currentParent);
                            }

                            newFolder.setDescription(getCellValue(row.getCell(separatorIndex + 1)));

                            parentStack.push(newFolder); // Push this folder onto the stack as the new parent
                            break;

                        case ITEM_TYPE_DIMENSION:
                            Dimension newDimension;
                            if (!itemID.isEmpty()) {
                                newDimension = blxFactory.createBlItem(Dimension.class, itemName, currentParent,
                                        itemID);
                            } else {
                                newDimension = blxFactory.createBlItem(Dimension.class, itemName, currentParent);
                            }
                            newDimension.setDescription(getCellValue(row.getCell(separatorIndex + 1)));

                            Optional<DataType> dataTypeOpt = parseDataType(
                                    getCellValue(row.getCell(separatorIndex + 3)));
                            if (dataTypeOpt.isPresent()) {
                                newDimension.setDataType(dataTypeOpt.get());
                            } else {
                                errorSet.addError(
                                        new ExcelError("Invalid data type", rowIndex + 1, separatorIndex + 3));
                            }

                            RelationalBinding binding = (RelationalBinding) newDimension.getBinding();
                            binding.setSelect(getCellValue(row.getCell(separatorIndex + 4)));
                            binding.setWhere(getCellValue(row.getCell(separatorIndex + 5)));
                            break;

                        case ITEM_TYPE_MEASURE:
                            Measure newMeasure;
                            if (!itemID.isEmpty()) {
                                newMeasure = blxFactory.createBlItem(Measure.class, itemName, currentParent, itemID);
                            } else {
                                newMeasure = blxFactory.createBlItem(Measure.class, itemName, currentParent);
                            }
                            newMeasure.setDescription(getCellValue(row.getCell(separatorIndex + 1)));

                            dataTypeOpt = parseDataType(
                                    getCellValue(row.getCell(separatorIndex + 3)));
                            if (dataTypeOpt.isPresent()) {
                                newMeasure.setDataType(dataTypeOpt.get());
                            } else {
                                errorSet.addError(
                                        new ExcelError("Invalid data type", rowIndex + 1, separatorIndex + 3));
                            }

                            binding = (RelationalBinding) newMeasure.getBinding();
                            binding.setSelect(getCellValue(row.getCell(separatorIndex + 4)));
                            binding.setWhere(getCellValue(row.getCell(separatorIndex + 5)));
                            break;

                        case ITEM_TYPE_FILTER:
                            String filterType = getCellValue(row.getCell(separatorIndex + 3));

                            if (filterType.equals("NATIVE")) {
                                NativeRelationalFilter newFilter;
                                if (!itemID.isEmpty()) {
                                    newFilter = blxFactory.createBlItem(NativeRelationalFilter.class, itemName,
                                            currentParent, itemID);
                                } else {
                                    newFilter = blxFactory.createBlItem(NativeRelationalFilter.class, itemName,
                                            currentParent);
                                }
                                newFilter.setDescription(getCellValue(row.getCell(separatorIndex + 1)));

                                binding = (RelationalBinding) newFilter.getBinding();
                                binding.setWhere(getCellValue(row.getCell(separatorIndex + 5)));
                            } else if (filterType.equals("BUSINESS")) {
                                BusinessFilter newFilter;
                                if (!itemID.isEmpty()) {
                                    newFilter = blxFactory.createBlItem(BusinessFilter.class, itemName, currentParent,
                                            itemID);
                                } else {
                                    newFilter = blxFactory.createBlItem(BusinessFilter.class, itemName, currentParent);
                                }
                                newFilter.setDescription(getCellValue(row.getCell(separatorIndex + 1)));
                                newFilter.setExpression(getCellValue(row.getCell(separatorIndex + 5)));
                                IStatus status = newFilter.validateExpression();

                                if (status.getSeverity() != Severity.OK) {
                                    errorSet.addError(new ExcelError("Invalid filter expression", rowIndex + 1,
                                            separatorIndex + 5));
                                }
                            } else {
                                errorSet.addError(
                                        new ExcelError("Invalid filter type", rowIndex + 1, separatorIndex + 3));
                            }
                            break;

                        default:
                            errorSet.addError(
                                    new ExcelError("Unknown item type: " + itemType, rowIndex + 1, separatorIndex + 2));
                    }
                } catch (Exception e) {
                    LOGGER.severe("Failed to process item: " + itemName + ". Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        if (errorSet.getErrors().size() > 0) {
            throw new IOException("Errors found in Excel file: \n" + errorSet.toString());
        }

        // Use Apache POI to read data from Excel
        return new Universe.Builder()
                .setName(serverUniverse.getName())
                .setBlx(blx)
                .build();
    }

    private static int findSeparatorIndex(Sheet sheet) {
        Row headerRow = sheet.getRow(1); // Assuming the first row is the header row
        for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
            Cell cell = headerRow.getCell(cellIndex);
            if (cell != null && cell.getCellStyle().getBorderRight() == BorderStyle.DOUBLE) {
                return cellIndex + 1; // Separator is the cell after the double border
            }
        }
        return -1; // Return -1 if no separator is found
    }

    private static String getCellValue(Cell cell) {
        if (cell == null)
            return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private static Optional<DataType> parseDataType(String dataTypeStr) {
        if (dataTypeStr == null || dataTypeStr.isEmpty()) {
            LOGGER.severe("DataType string is null.");
            return Optional.empty();
        }

        String normalizedStr = dataTypeStr.trim().toUpperCase();
        try {
            DataType dataType = DataType.valueOf(normalizedStr);
            return Optional.of(dataType);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}

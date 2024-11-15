package main.java.com.ardian.bouniversemanager.utils;

public class ExcelError {
    private String message;
    private int row;
    private int column;

    public ExcelError(String message, int row, int column) {
        this.message = message;
        this.row = row;
        this.column = column;
    }

    public String toString() {
        return "Error at row " + row + ", column " + toExcelColumn(column) + ": " + message;
    }

    private static String toExcelColumn(int column) {
        StringBuilder columnLetter = new StringBuilder();
        while (column >= 0) {
            columnLetter.insert(0, (char) ('A' + (column % 26)));
            column = (column / 26) - 1;
        }
        return columnLetter.toString();
    }
}

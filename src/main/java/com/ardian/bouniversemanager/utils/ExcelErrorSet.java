package main.java.com.ardian.bouniversemanager.utils;

import java.util.ArrayList;
import java.util.List;

public class ExcelErrorSet {
    private List<ExcelError> errors;

    public ExcelErrorSet() {
        this.errors = new ArrayList<>();
    }

    public List<ExcelError> getErrors() {
        return errors;
    }

    public void addError(ExcelError error) {
        errors.add(error);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (ExcelError error : errors) {
            builder.append(error.toString() + "\n");
        }
        return builder.toString();
    }
}

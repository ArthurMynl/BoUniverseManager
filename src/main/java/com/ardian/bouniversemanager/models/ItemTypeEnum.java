package main.java.com.ardian.bouniversemanager.models;

public enum ItemTypeEnum {
    FOLDER,
    DIMENSION,
    MEASURE;

    public static ItemTypeEnum fromString(String type) {
        try {
            return ItemTypeEnum.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

package main.java.com.ardian.bouniversemanager.utils;

import java.util.Optional;
import java.util.logging.Logger;

public class EnumParser {
    private static final Logger LOGGER = Logger.getLogger(EnumParser.class.getName());

    public static <E extends Enum<E>> Optional<E> parseEnum(Class<E> enumClass, String value) {
        if (value == null) {
            LOGGER.severe("Enum value string is null.");
            return Optional.empty();
        }

        String normalizedStr = value.trim().toUpperCase();
        LOGGER.fine("Attempting to parse " + enumClass.getSimpleName() + ": '" + normalizedStr + "'");

        try {
            E enumValue = Enum.valueOf(enumClass, normalizedStr);
            LOGGER.fine("Parsed " + enumClass.getSimpleName() + " successfully: " + enumValue);
            return Optional.of(enumValue);
        } catch (IllegalArgumentException e) {
            LOGGER.severe("Invalid " + enumClass.getSimpleName() + ": '" + value + "'");
            return Optional.empty();
        }
    }
}
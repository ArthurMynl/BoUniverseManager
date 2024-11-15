package main.java.com.ardian.bouniversemanager.comparison;


@FunctionalInterface
public interface FieldComparator {
    /**
     * Compare a specific field between two BlItems and record changes if necessary.
     *
     * @param context The context containing all necessary comparison data.
     * @return true if a change was detected and recorded, false otherwise.
     */
    boolean compare(ComparisonContext context);
}

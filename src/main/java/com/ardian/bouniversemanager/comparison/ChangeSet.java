package main.java.com.ardian.bouniversemanager.comparison;

import java.util.ArrayList;
import java.util.List;

public class ChangeSet {
    private List<Change> changes;

    public ChangeSet() {
        this.changes = new ArrayList<>();
    }

    public void addChange(Change change) {
        changes.add(change);
    }

    public void addAll(ChangeSet changes) {
        this.changes.addAll(changes.getChanges());
    }

    public List<Change> getChanges() {
        return changes;
    }

    public Change getChange(String identifier) {
        for (Change change : changes) {
            if (change.getIdentifier().equals(identifier)) {
                return change;
            }
        }
        return null;
    }

    public Change getChangeByIdentifier(String identifier) {
        for (Change change : changes) {
            if (change.getIdentifier().equals(identifier)) {
                return change;
            }
        }
        return null;
    }    

    public boolean containsChangeForIdentifier(String identifier) {
        for (Change change : changes) {
            if (change.getIdentifier().equals(identifier)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        if (changes.size() == 0) {
            return "No changes found.";   
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("---------------------------------\n");
            for (Change change : changes) {
                sb.append(change.toString());
                sb.append("\n---------------------------------\n");
            }
            return sb.toString();
        }
    }
}
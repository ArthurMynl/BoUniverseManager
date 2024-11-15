package main.java.com.ardian.bouniversemanager.comparison;

import java.util.ArrayList;
import java.util.List;

public class Change {
    private String identifier;
    private String name;
    private List<ChangeDetail> changeDetails;

    public Change(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
        this.changeDetails = new ArrayList<>();
    }

    public void addChangeDetail(ChangeDetail detail) {
        this.changeDetails.add(detail);
    }

    public List<ChangeDetail> getChangeDetails() {
        return changeDetails;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (changeDetails.size() == 1)
            sb.append("Change for item: ").append(name).append(" (ID: ").append(identifier);
        else
            sb.append("Changes for item: ").append(name).append(" (ID: ").append(identifier);
        for (ChangeDetail detail : changeDetails) {
            sb.append(")\n").append(detail.toString());
        }
        return sb.toString();
    }
}


package main.java.com.ardian.bouniversemanager.models;

import com.sap.sl.sdk.authoring.businesslayer.RelationalBusinessLayer;

public class Universe {
    private String name;
    private RelationalBusinessLayer blx;

    private Universe(Builder builder) {
        this.name = builder.name;
        this.blx = builder.blx;
    }

    public String getName() {
        return name;
    }

    public RelationalBusinessLayer getBlx() {
        return blx;
    }

    public static class Builder {
        private String name;
        private RelationalBusinessLayer blx;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setBlx(RelationalBusinessLayer blx) {
            this.blx = blx;
            return this;
        }

        public Universe build() {
            return new Universe(this);
        }
    }
}

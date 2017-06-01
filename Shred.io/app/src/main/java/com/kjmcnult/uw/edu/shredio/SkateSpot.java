package com.kjmcnult.uw.edu.shredio;

import java.util.ArrayList;

/**
 * Created by Seung on 5/31/2017.
 */

public class SkateSpot {
    public String name;
    public String description;
    public String imageResource;
    public LatLng location;
    public ArrayList<Boolean> ids;
    private String key;

    public SkateSpot() {}

    public SkateSpot(String spotName, String description, String imageResource, LatLng location, ArrayList<Boolean> ids){
        this.name = spotName;
        this.description = description;
        this.imageResource = "spots/" + imageResource;
        this.location = location;
        this.ids = ids;
        this.key = "";
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageResource() {
        return imageResource;
    }

    public LatLng getLocation() { return location; }

    public ArrayList<Boolean> getIds() { return ids; }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() { return key; }

    public String toString() {
        return name + ": " + description;
    }
}

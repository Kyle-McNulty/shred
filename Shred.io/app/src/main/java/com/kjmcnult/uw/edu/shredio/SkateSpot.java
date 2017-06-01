package com.kjmcnult.uw.edu.shredio;

import java.util.ArrayList;

/**
 * Created by Seung on 5/31/2017.
 */

public class SkateSpot {
    public String name;
    public String description;
    public String imageResource;
    public String tags;
    public LatLng location;
    public ArrayList<Boolean> ids;

    public SkateSpot() {}

    public SkateSpot(String spotName, String description, String imageResource, String tags, LatLng location, ArrayList<Boolean> ids){
        this.name = spotName;
        this.description = description;
        this.imageResource = "spots/" + imageResource;
        this.tags = tags;
        this.location = location;
        this.ids = ids;
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

    public String getTags() {
        return tags;
    }

    public LatLng getLocation() { return location; }

    public ArrayList<Boolean> getIds() { return ids; }

    public String toString() {
        return name + ": " + description;
    }
}

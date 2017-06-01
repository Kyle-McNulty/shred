package com.kjmcnult.uw.edu.shredio;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Seung on 5/31/2017.
 */

public class SkateSpot {
    private String name;
    private String description;
    private String imageResource;
    private LatLng location;
    private ArrayList<Boolean> ids;
    private HashMap<String, Double> userRatings;


    public SkateSpot() {}

    public SkateSpot(String spotName, String description, String imageResource, LatLng location, ArrayList<Boolean> ids, HashMap<String, Double> userRatings){
        this.name = spotName;
        this.description = description;
        this.imageResource = "spots/" + imageResource;
        this.location = location;
        this.ids = ids;
        this.userRatings = userRatings;
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

    public String toString() {
        return name + ": " + description;
    }

    public HashMap<String, Double> getUserRatings() {
        return userRatings;
    }

    public Double getRating() {
        Double ratingTotal = 0.00;
        for(Double rating : userRatings.values()){
            ratingTotal += rating;
        }
        return ratingTotal/userRatings.keySet().size();
    }
}

package com.kjmcnult.uw.edu.shredio;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by troybarnard on 5/30/17.
 */

public class Spot {

    private String name;
    private String description;
    private Bitmap picture;
    private LatLng location;
    private ArrayList<String> tags;

    public Spot() {

    }

    public Spot(String name, LatLng location, String description, Bitmap picture) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.picture = picture;
        tags = new ArrayList<>();
    }

    public Spot(String name, LatLng location, String description) {
        this.name = name;
        this.location = location;
        this.description = description;
    }

    public Spot(String name, LatLng location) {
        this.name = name;
        this.location = location;
    }

    public String toString() {
        return name + "\n" + location.toString() + "\n" + description;
    }

    public String getName() {
        return  this.name;
    }

    public String getDescription() {
        return  this.description;
    }

    public Bitmap getPicture() {
        return  this.picture;
    }

    public LatLng getLocation() {
        return this.location;
    }

    public ArrayList<String> getTags() {
        return this.tags;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public void addTags(String[] tags) {
        for (String tag: tags) {
            this.tags.add(tag);
        }
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }
}

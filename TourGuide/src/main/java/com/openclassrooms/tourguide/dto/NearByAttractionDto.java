package com.openclassrooms.tourguide.dto;

import gpsUtil.location.Location;

public class NearByAttractionDto {
    public String attractionName;
    public Location attractionLocation;
    public Location userLocation;
    public double distance;
    public int rewardPoints;

    public NearByAttractionDto(String attractionName, Location attractionLocation,
                               Location userLocation, double distance, int rewardPoints) {
        this.attractionName = attractionName;
        this.attractionLocation = attractionLocation;
        this.userLocation = userLocation;
        this.distance = distance;
        this.rewardPoints = rewardPoints;
    }
}
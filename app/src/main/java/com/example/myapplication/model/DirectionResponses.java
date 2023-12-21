package com.example.myapplication.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DirectionResponses{

    @SerializedName("routes")
    private List<RoutesItem> routes;

    @SerializedName("geocoded_waypoints")
    private List<GeocodedWaypointsItem> geocodedWaypoints;

    @SerializedName("status")
    private String status;

    public void setRoutes(List<RoutesItem> routes){
        this.routes = routes;
    }

    public List<RoutesItem> getRoutes(){
        return routes;
    }

    public void setGeocodedWaypoints(List<GeocodedWaypointsItem> geocodedWaypoints){
        this.geocodedWaypoints = geocodedWaypoints;
    }

    public List<GeocodedWaypointsItem> getGeocodedWaypoints(){
        return geocodedWaypoints;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
    public String getTravelMode() {
        if (routes != null && !routes.isEmpty()) {
            List<StepsItem> steps = routes.get(0).getLegs().get(0).getSteps();
            if (steps != null && !steps.isEmpty()) {
                return steps.get(0).getTravelMode();
            }
        }
        return null;
    }
}
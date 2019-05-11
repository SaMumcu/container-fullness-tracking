package com.example.containerfullnesstracker.model;

public class Container {

    private String occupancyRate;
    private String temperature;
    private String date;
    private String sensorId;
    private String containerId;
    private double latitude;
    private double longitude;

    public Container() {
    }

    public Container(String occupancyRate, String temperature, String date, String sensorId, String containerId,double latitude,double longitude) {
        this.occupancyRate = occupancyRate;
        this.temperature = temperature;
        this.date = date;
        this.sensorId = sensorId;
        this.containerId = containerId;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public String getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(String occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }
}

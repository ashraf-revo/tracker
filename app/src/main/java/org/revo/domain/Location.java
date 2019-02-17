package org.revo.domain;

import java.util.Date;

public class Location {
    private String id;
    private String trackerId;
    private Date date;
    private Double lat;
    private Double lng;

    public Location() {
    }

    public Location(String id, String trackerId, Date date, Double lat, Double lng) {
        this.id = id;
        this.trackerId = trackerId;
        this.date = date;
        this.lat = lat;
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}

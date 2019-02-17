package org.revo.domain;

import java.util.Date;

public class Call {
    private String id;
    private String trackerId;
    private Date date;
    private CallType callType;
    private String number;
    private int duration;

    public Call() {
    }

    public Call(String id, String trackerId, Date date, CallType callType, String number, int duration) {
        this.id = id;
        this.trackerId = trackerId;
        this.date = date;
        this.callType = callType;
        this.number = number;
        this.duration = duration;
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

    public CallType getCallType() {
        return callType;
    }

    public void setCallType(CallType callType) {
        this.callType = callType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

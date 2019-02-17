package org.revo.domain;

import java.util.Date;

public class Tracker {
    private String id;
    private String account[];

    private Date createdDate;
    private Date lastUpdateCall;
    private Date lastUpdateLocation;

    public Tracker() {
    }

    public Tracker(String id, String[] account, Date createdDate, Date lastUpdateCall, Date lastUpdateLocation) {
        this.id = id;
        this.account = account;
        this.createdDate = createdDate;
        this.lastUpdateCall = lastUpdateCall;
        this.lastUpdateLocation = lastUpdateLocation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getAccount() {
        return account;
    }

    public void setAccount(String[] account) {
        this.account = account;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdateCall() {
        return lastUpdateCall;
    }

    public void setLastUpdateCall(Date lastUpdateCall) {
        this.lastUpdateCall = lastUpdateCall;
    }

    public Date getLastUpdateLocation() {
        return lastUpdateLocation;
    }

    public void setLastUpdateLocation(Date lastUpdateLocation) {
        this.lastUpdateLocation = lastUpdateLocation;
    }
}

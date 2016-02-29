/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver.telemetry;

import java.util.Calendar;

/**
 *
 * @author osyniaev
 */
public class ServerStateModel {

    private long responseTime;
    private int aliveConnectionsNumber;
    private Calendar currentDate;

    public ServerStateModel(long responseTime, int aliveConnectionsNumber) {
        this.responseTime = responseTime;
        this.aliveConnectionsNumber = aliveConnectionsNumber;
        currentDate = Calendar.getInstance();
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public int getAliveConnectionsNumber() {
        return aliveConnectionsNumber;
    }

    public void setAliveConnectionsNumber(int aliveConnectionsNumber) {
        this.aliveConnectionsNumber = aliveConnectionsNumber;
    }

    public Calendar getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Calendar currentDate) {
        this.currentDate = currentDate;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) (this.responseTime ^ (this.responseTime >>> 32));
        hash = 97 * hash + this.aliveConnectionsNumber;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ServerStateModel other = (ServerStateModel) obj;
        if (this.responseTime != other.responseTime) {
            return false;
        }
        if (this.aliveConnectionsNumber != other.aliveConnectionsNumber) {
            return false;
        }
        return true;
    }
}

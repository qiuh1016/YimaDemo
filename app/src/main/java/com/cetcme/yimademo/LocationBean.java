package com.cetcme.yimademo;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

@Table(name = "t_location")
public class LocationBean {
    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "longitude")
    private int longitude;
    @Column(name = "latitude")
    private int latitude;
    @Column(name = "speed")
    private Double speed;
    @Column(name = "heading")
    private float heading;
    @Column(name = "acqtime")
    private Date acqtime;
    @Column(name = "navtime")
    private Date navtime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public float getHeading() {
        return heading;
    }

    public void setHeading(float heading) {
        this.heading = heading;
    }

    public Date getAcqtime() {
        return acqtime;
    }

    public void setAcqtime(Date acqtime) {
        this.acqtime = acqtime;
    }

    public Date getNavtime() {
        return navtime;
    }

    public void setNavtime(Date navtime) {
        this.navtime = navtime;
    }
}

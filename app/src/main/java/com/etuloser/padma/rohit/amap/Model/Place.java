package com.etuloser.padma.rohit.amap.Model;

import java.io.Serializable;

/**
 * Created by Rohit on 11/11/2017.
 */

public class Place implements Serializable {

    private  String placename;
    private String latitude;
    private String longitude;

    public String getPlacename() {
        return placename;
    }

    public void setPlacename(String placename) {
        this.placename = placename;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}

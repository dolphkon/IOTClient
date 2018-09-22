package com.dolphkon.me.bean;

/**
 * Created by dolphkon on 2018/8/28.
 */

public class GPSEventMsp {
    private  int gid;
    private String gdevice;
    private String gtime;
    private String gtemperature;
    private String  gip;
    private String gfrag;
    private float glongitud;
    private  float dimension;

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public String getGdevice() {
        return gdevice;
    }

    public void setGdevice(String gdevice) {
        this.gdevice = gdevice;
    }

    public String getGtime() {
        return gtime;
    }

    public void setGtime(String gtime) {
        this.gtime = gtime;
    }

    public String getGtemperature() {
        return gtemperature;
    }

    public void setGtemperature(String gtemperature) {
        this.gtemperature = gtemperature;
    }

    public String getGip() {
        return gip;
    }

    public void setGip(String gip) {
        this.gip = gip;
    }

    public String getGfrag() {
        return gfrag;
    }

    public void setGfrag(String gfrag) {
        this.gfrag = gfrag;
    }

    public float getGlongitud() {
        return glongitud;
    }

    public void setGlongitud(float glongitud) {
        this.glongitud = glongitud;
    }

    public float getDimension() {
        return dimension;
    }

    public void setDimension(float dimension) {
        this.dimension = dimension;
    }
}

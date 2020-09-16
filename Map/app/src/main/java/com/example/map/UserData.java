package com.example.map;

import java.io.Serializable;

public class UserData implements Serializable { // User 데이터를 저장하기 위한 class 입니다.
    private String name;
    private String addr;
    private String hp;
    private String sx;
    private String h2;
    private double lat = -1e9;
    private double log = -1e9;

    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLog() {
        return log;
    }
    public void setLog(double log) {
        this.log = log;
    }
    public String getSx() {
        return sx;
    }
    public void setSx(String sx) {
        this.sx = sx;
    }
    public String getH2() {
        return h2;
    }
    public void setH2(String h2) {
        this.h2 = h2;
    }
    public String getHp3() {
        return hp3;
    }
    public void setHp3(String hp3) {
        this.hp3 = hp3;
    }
    public String getBr() {
        return br;
    }
    public void setBr(String br) {
        this.br = br;
    }
    private String hp3;
    private String br;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddr() {
        return addr;
    }
    public void setAddr(String addr) {
        this.addr = addr;
    }
    public String getHp() {
        return hp;
    }
    public void setHp(String hp) {
        this.hp = hp;
    }
}

package com.example.tastemap;

import java.io.Serializable;

public class ListData implements Serializable, Comparable<ListData> {
    private String name; //가게 이름
    private String address; // 주소
    private double latitude; // 위도
    private double longitude; // 경도
    private int rate; //평가한 별점
    private double distance;
    private String memo;//메모


    public ListData(String address, double latitude, double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ListData(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ListData(String name, double latitude, double longitude, String address) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public ListData(String name, double latitude, double longitude, String address, int rate) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rate = rate;
    }

    public ListData(String name, double latitude, double longitude, String address, int rate, String memo) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rate = rate;
        this.memo = memo;
    }

    public static void add(ListData item) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return this.getName() + "\t"
                + this.getLatitude() + "\t"
                + this.getLongitude() + "\t"
                + this.getAddress() + "\t"
                + this.getRate() + "\t"
                + this.getMemo() + "\n";
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(ListData o) {
        if (this.distance > o.distance) {
            return 1;
        } else if (this.distance == o.distance) {
            return 0;
        } else {
            return -1;
        }
    }
}
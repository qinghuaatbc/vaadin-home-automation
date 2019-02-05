package com.example.test.html;

public class Camera {
    private String IP;
    private String locationChinese;
    private String locationEnglish;


    public String getIP() {
        return IP;
    }

    public String getLocationChinese() {
        return locationChinese;
    }

    public String getLocationEnglish() {
        return locationEnglish;
    }

    public Camera(String IP, String locationChinese, String locationEnglish) {
        this.IP = IP;
        this.locationChinese = locationChinese;
        this.locationEnglish = locationEnglish;
    }
}

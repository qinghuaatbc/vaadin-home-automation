package com.example.test.html;

public class Movie {
    private String streamId;
    private String Title;
    private String Year;
    private String country;
    private String image;

    public String getStreamId() {
        return streamId;
    }

    public String getTitle() {
        return Title;
    }

    public String getYear() {
        return Year;
    }

    public String getCountry() {
        return country;
    }

    public String getImage() {
        return image;
    }

    public Movie(String streamId, String title, String year, String country) {
        this.streamId = streamId;
        Title = title;
        Year = year;
        this.country = country;
    }

    public Movie(String streamId, String title, String year, String country, String image) {
        this.streamId = streamId;
        Title = title;
        Year = year;
        this.country = country;
        this.image = image;
    }


}

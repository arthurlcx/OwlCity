package com.example.arthur.owlcity.Class;

public class Club {
    private String clubID;
    private String name;
    private String location;
    private String desc;
    private String clubAdd;
    private String contact;
    private String opHour;
    private double longitude;
    private double lat;
    private String clubLogo;
    private String clubImg;

    public Club() {
    }

    public Club(String clubID, String name, String location, String desc, String clubAdd, String contact, String opHour, double longitude, double lat, String clubLogo, String clubImg) {
        this.clubID = clubID;
        this.name = name;
        this.location = location;
        this.desc = desc;
        this.clubAdd = clubAdd;
        this.contact = contact;
        this.opHour = opHour;
        this.longitude = longitude;
        this.lat = lat;
        this.clubLogo = clubLogo;
        this.clubImg = clubImg;
    }

    public String getClubID() {
        return clubID;
    }

    public void setClubID(String clubID) {
        this.clubID = clubID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getClubAdd() {
        return clubAdd;
    }

    public void setClubAdd(String clubAdd) {
        this.clubAdd = clubAdd;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getOpHour() {
        return opHour;
    }

    public void setOpHour(String opHour) {
        this.opHour = opHour;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getClubLogo() {
        return clubLogo;
    }

    public void setClubLogo(String clubLogo) {
        this.clubLogo = clubLogo;
    }

    public String getClubImg() {
        return clubImg;
    }

    public void setClubImg(String clubImg) {
        this.clubImg = clubImg;
    }
}
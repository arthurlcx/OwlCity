package com.example.arthur.owlcity.Class;

public class City {
    private String cityCode;
    private String cityName;
    private String imageFileName;

    public City() {
    }

    public City(String cityCode, String cityName, String imageFileName) {
        this.cityCode = cityCode;
        this.cityName = cityName;
        this.imageFileName = imageFileName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
}

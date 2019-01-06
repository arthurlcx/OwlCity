package com.example.arthur.owlcity.Class;

public class ClubPackage {
    private String packID;
    private String packDesc;
    private String packName;
    private String packPrice;
    private String packSeat;
    private String clubId;
    private String packageImg;
    private String clubName;

    public ClubPackage() {
    }

    public ClubPackage(String packID, String packDesc, String packName, String packPrice, String packSeat, String clubId, String packageImg, String clubName) {
        this.packID = packID;
        this.packDesc = packDesc;
        this.packName = packName;
        this.packPrice = packPrice;
        this.packSeat = packSeat;
        this.clubId = clubId;
        this.packageImg = packageImg;
        this.clubName = clubName;
    }

    public String getPackID() {
        return packID;
    }

    public void setPackID(String packID) {
        this.packID = packID;
    }

    public String getPackDesc() {
        return packDesc;
    }

    public void setPackDesc(String packDesc) {
        this.packDesc = packDesc;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getPackPrice() {
        return packPrice;
    }

    public void setPackPrice(String packPrice) {
        this.packPrice = packPrice;
    }

    public String getPackSeat() {
        return packSeat;
    }

    public void setPackSeat(String packSeat) {
        this.packSeat = packSeat;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getPackageImg() {
        return packageImg;
    }

    public void setPackageImg(String packageImg) {
        this.packageImg = packageImg;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }
}

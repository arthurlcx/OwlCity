package com.example.arthur.owlcity.Class;

public class ReservationInfo {
    private String reservationID;
    private String reservationOwnerId;
    private String reservationOwner;
    private String clubName;
    private String reservationDetails;
    private String date;

    public ReservationInfo() {
    }

    public ReservationInfo(String reservationID, String reservationOwnerId, String reservationOwner, String clubName, String reservationDetails, String date) {
        this.reservationID = reservationID;
        this.reservationOwnerId = reservationOwnerId;
        this.reservationOwner = reservationOwner;
        this.clubName = clubName;
        this.reservationDetails = reservationDetails;
        this.date = date;
    }

    public String getReservationID() {
        return reservationID;
    }

    public void setReservationID(String reservationID) {
        this.reservationID = reservationID;
    }

    public String getReservationOwnerId() {
        return reservationOwnerId;
    }

    public void setReservationOwnerId(String reservationOwnerId) {
        this.reservationOwnerId = reservationOwnerId;
    }

    public String getReservationOwner() {
        return reservationOwner;
    }

    public void setReservationOwner(String reservationOwner) {
        this.reservationOwner = reservationOwner;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getReservationDetails() {
        return reservationDetails;
    }

    public void setReservationDetails(String reservationDetails) {
        this.reservationDetails = reservationDetails;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

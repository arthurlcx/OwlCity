package com.example.arthur.owlcity.Class;

public class CardInfo {
    private String cardId;
    private String cardNo;
    private String cardName;
    private String ccv;
    private String cardOwnerId;

    public CardInfo() {
    }

    public CardInfo(String cardId, String cardNo, String cardName, String ccv, String cardOwnerId) {
        this.cardId = cardId;
        this.cardNo = cardNo;
        this.cardName = cardName;
        this.ccv = ccv;
        this.cardOwnerId = cardOwnerId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCcv() {
        return ccv;
    }

    public void setCcv(String ccv) {
        this.ccv = ccv;
    }

    public String getCardOwnerId() {
        return cardOwnerId;
    }

    public void setCardOwnerId(String cardOwnerId) {
        this.cardOwnerId = cardOwnerId;
    }

    @Override
    public String toString() {
        return "CardInfo{" +
                "cardNo=" + cardNo +
                ", cardName='" + cardName + '\'' +
                ", ccv=" + ccv +
                ", cardOwnerId='" + cardOwnerId + '\'' +
                '}';
    }
}

package com.example.arthur.owlcity.Class;

public class CardInfo {
    private String cardId;
    private Long cardNo;
    private String cardName;
    private Long ccv;
    private String cardOwnerId;

    public CardInfo() {
    }

    public CardInfo(String cardId, Long cardNo, String cardName, Long ccv, String cardOwnerId) {
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

    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public Long getCcv() {
        return ccv;
    }

    public void setCcv(Long ccv) {
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

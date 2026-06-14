package com.eventmanager.app.models;

import java.io.Serializable;

public class Booking implements Serializable {

    private String id;
    private String eventId;
    private String userId;
    private int quantity;
    private double totalPrice;
    private String status;
    private long purchaseDate;

    // Snapshot des infos event (pour affichage rapide sans rejoin)
    private String eventTitle;
    private long eventDate;
    private String eventLocation;
    private String eventImage;

    public Booking() {}

    public Booking(String id, String eventId, String userId, int quantity, double totalPrice,
                   String status, long purchaseDate, String eventTitle, long eventDate,
                   String eventLocation, String eventImage) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.purchaseDate = purchaseDate;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.eventImage = eventImage;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(long purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public long getEventDate() { return eventDate; }
    public void setEventDate(long eventDate) { this.eventDate = eventDate; }

    public String getEventLocation() { return eventLocation; }
    public void setEventLocation(String eventLocation) { this.eventLocation = eventLocation; }

    public String getEventImage() { return eventImage; }
    public void setEventImage(String eventImage) { this.eventImage = eventImage; }
}
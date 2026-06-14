package com.eventmanager.app.models;

import java.io.Serializable;

public class Ticket implements Serializable {

    private String id;
    private String bookingId;
    private String eventId;
    private String userId;
    private String qrCode;
    private int seatNumber;
    private String status;
    private long purchaseDate;

    // Snapshot pour affichage
    private String eventTitle;
    private long eventDate;
    private String eventLocation;
    private String eventImage;

    public Ticket() {}

    public Ticket(String id, String bookingId, String eventId, String userId, String qrCode,
                  int seatNumber, String status, long purchaseDate,
                  String eventTitle, long eventDate, String eventLocation, String eventImage) {
        this.id = id;
        this.bookingId = bookingId;
        this.eventId = eventId;
        this.userId = userId;
        this.qrCode = qrCode;
        this.seatNumber = seatNumber;
        this.status = status;
        this.purchaseDate = purchaseDate;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.eventImage = eventImage;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

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
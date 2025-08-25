package com.example.map_toysocialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class Message extends Entity<Long> {
    private User from;
    private User to;
    private String message;
    private LocalDateTime date;
    private Long idReply; // Va fi -1 pentru mesajele noi

    //constructor pentru primul mesaj dintr o conversatie
    public Message(User from, User to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = LocalDateTime.now();
        this.idReply = (long) -1; // Implicit este -1
    }

    // Constructor pentru mesaj de răspuns
    public Message(User from, User to, String message, Long idReply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = LocalDateTime.now();
        this.idReply = idReply;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getIdReply() {
        return idReply;
    }

    public void setReply(Long idReply) {
        this.idReply = idReply;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd:MM:yyyy");
        return date.format(formatter);
    }
}

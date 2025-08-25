package com.example.map_toysocialnetwork.domain;

import java.time.LocalDateTime;

public class FriendshipRequest extends Entity<Tuple<Long, Long>>{

    String status;
    LocalDateTime date;

    public FriendshipRequest() {
        this.date = LocalDateTime.now();
    }

    public FriendshipRequest(Tuple<Long, Long> id) {
        super.setId(id);
        this.date = LocalDateTime.now();
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
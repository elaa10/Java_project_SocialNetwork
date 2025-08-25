package com.example.map_toysocialnetwork.domain;

import java.time.LocalDateTime;


public class Friendship extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;

    public Friendship() {
        this.date = LocalDateTime.now();
    }

    public Friendship(Tuple<Long, Long> id) {
        super.setId(id);
        this.date = LocalDateTime.now();
    }

    /**
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) { this.date = date; }

    @Override
    public String toString() {
        return "Friendship { " +"id = " + getId() +", date = " + date + "}";
    }
}

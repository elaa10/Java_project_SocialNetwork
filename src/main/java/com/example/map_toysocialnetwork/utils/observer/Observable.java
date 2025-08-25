package com.example.map_toysocialnetwork.utils.observer;

import java.sql.SQLException;

public interface Observable {
    public void addObserver(Observer o);
    public void removeObserver(Observer o);
    public void notifyAllObservers() throws SQLException;
}

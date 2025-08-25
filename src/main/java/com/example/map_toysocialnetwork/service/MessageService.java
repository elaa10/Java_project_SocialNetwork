package com.example.map_toysocialnetwork.service;

import com.example.map_toysocialnetwork.domain.Message;
import com.example.map_toysocialnetwork.domain.User;
import com.example.map_toysocialnetwork.repository.dataBase.MessageDataBaseRepository;
import com.example.map_toysocialnetwork.repository.dataBase.UserDataBaseRepository;
import com.example.map_toysocialnetwork.utils.observer.Observable;
import com.example.map_toysocialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageService implements Observable {

    private final MessageDataBaseRepository messageRepository;
    private final UserDataBaseRepository userRepository;
    List<Observer> observers = new ArrayList<>();

    public MessageService(MessageDataBaseRepository messageRepository, UserDataBaseRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }


    public List<Message> getMessagesBetween(User user1, User user2) {
        return StreamSupport.stream(messageRepository.findAll().spliterator(), false)
                .filter(m -> (m.getFrom().getId().equals(user1.getId()) && m.getTo().getId().equals(user2.getId())) ||
                        (m.getFrom().getId().equals(user2.getId()) && m.getTo().getId().equals(user1.getId())))
                .collect(Collectors.toList());
    }

    public void replyMessage(User from, User to, String messageText) {
        sendMessage(from, to, messageText);
    }

    public void sendMessageToUsername(User from, String username, String text) {
        Optional<User> optionalTo = userRepository.findOneUsername(username);
        if (optionalTo.isEmpty()) {
            throw new RuntimeException("User not found: " + username);
        }
        User to = optionalTo.get();
        sendMessage(from, to, text);
    }

    public List<User> getConversationUsers(User currentUser) {
        return StreamSupport.stream(messageRepository.findAll().spliterator(), false) // Transformă Iterable în Stream
                .filter(message -> message.getFrom().equals(currentUser) || message.getTo().equals(currentUser)) // Filtrare
                .map(message -> message.getFrom().equals(currentUser) ? message.getTo() : message.getFrom()) // Selectăm celălalt utilizator
                .distinct() // Eliminăm duplicatele
                .collect(Collectors.toList()); // Colectăm rezultatele într-o listă
    }


    public void sendMessage(User from, User to, String text) {
        Message message = new Message(from, to, text);
        messageRepository.save(message);
        notifyAllObservers();
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyAllObservers() {
        for (Observer observer : observers) {
            try {
                observer.update();
            } catch (Exception e) {
                throw new ServiceException("Failed to notify observers: " + e.getMessage(), e);
            }
        }
    }
}

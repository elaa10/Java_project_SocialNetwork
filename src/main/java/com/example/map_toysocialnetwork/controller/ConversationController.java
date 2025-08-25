package com.example.map_toysocialnetwork.controller;

import com.example.map_toysocialnetwork.domain.Message;
import com.example.map_toysocialnetwork.domain.User;
import com.example.map_toysocialnetwork.service.MessageService;
import com.example.map_toysocialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConversationController implements Observer {

    @FXML
    private TableView<Message> tableViewMessages;
    @FXML
    private TableColumn<Message, String> fromColumn;
    @FXML
    private TableColumn<Message, String> messageColumn;
    @FXML
    private TableColumn<Message, String> dateColumn;
    @FXML
    private TextField messageTextField;

    private final ObservableList<Message> modelMessages = FXCollections.observableArrayList();

    private MessageService messageService;
    private User currentUser;
    private User otherUser;

    public void setServices(MessageService messageService, User currentUser, User otherUser) {
        this.messageService = messageService;
        this.currentUser = currentUser;
        this.otherUser = otherUser;

        messageService.addObserver(this);

        initModelMessages();
    }

    private void initModelMessages() {
        List<Message> messages = messageService.getMessagesBetween(currentUser, otherUser);
        modelMessages.setAll(messages);
    }

    @FXML
    public void initialize() {
        fromColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFrom().getUsername()));

        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));


        dateColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFormattedDate()));

        tableViewMessages.setItems(modelMessages);
    }

    @Override
    public void update() throws SQLException {
        initModelMessages();
    }

    @FXML
    public void handleSendMessage() {
        String messageText = messageTextField.getText();
        if (messageText.isBlank()) {
            showError("Introduceți un mesaj!");
            return;
        }

        messageService.sendMessage(currentUser, otherUser, messageText);
        initModelMessages();
        messageTextField.clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
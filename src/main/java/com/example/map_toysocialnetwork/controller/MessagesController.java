package com.example.map_toysocialnetwork.controller;

import com.example.map_toysocialnetwork.domain.User;
import com.example.map_toysocialnetwork.service.MessageService;
import com.example.map_toysocialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MessagesController implements Observer {

    @FXML
    private TableView<User> tableViewUsers;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TextField recipientsTextField;
    @FXML
    private TextField messageTextField;

    private final ObservableList<User> modelUsers = FXCollections.observableArrayList();

    private MessageService messageService;
    private User currentUser;
    private Stage stage;

    public void setServices(MessageService messageService, User currentUser, Stage stage) {
        this.messageService = messageService;
        this.currentUser = currentUser;
        this.stage = stage;

        messageService.addObserver(this);

        initModelUsers();
    }

    private void initModelUsers() {
        List<User> users = messageService.getConversationUsers(currentUser);
        modelUsers.setAll(users);
    }

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableViewUsers.setItems(modelUsers);
    }

    @Override
    public void update() throws SQLException {
        initModelUsers();
    }


    private void openMessageWindow(User user) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../conversation-view.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            ConversationController messageController = loader.getController();
            messageController.setServices(messageService, currentUser, user);

            stage.setTitle("Mesaje cu " + user.getUsername());
            stage.show();
        } catch (IOException e) {
            showError("Eroare la deschiderea ferestrei de mesaje: " + e.getMessage());
        }
    }

    @FXML
    public void handleSendMessageToMultipleUsers() {
        String recipients = recipientsTextField.getText();
        String messageText = messageTextField.getText();

        if (recipients.isBlank() || messageText.isBlank()) {
            showError("Introduceți destinatarii și mesajul!");
            return;
        }

        String[] recipientArray = recipients.split(" ");
        for (String recipient : recipientArray) {
            System.out.println(recipient + " ");
            messageService.sendMessageToUsername(currentUser, recipient, messageText);
        }

        recipientsTextField.clear();
        messageTextField.clear();
        initModelUsers();
    }

    @FXML
    public void handleOpenConversation() {
        User selectedUser = tableViewUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            MessageAlert.showErrorMessage(null, "Nu ați selectat niciun prieten pentru a șterge!");
            return;
        }
        openMessageWindow(selectedUser);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

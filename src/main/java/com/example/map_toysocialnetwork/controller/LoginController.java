package com.example.map_toysocialnetwork.controller;

import com.example.map_toysocialnetwork.HelloApplication;
import com.example.map_toysocialnetwork.Main;
import com.example.map_toysocialnetwork.domain.User;
import com.example.map_toysocialnetwork.service.Service;
import com.example.map_toysocialnetwork.service.ServiceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.awt.*;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LoginController {

    private Service service;
    private Stage stage;
    private HelloApplication main;

    @FXML
    private TableView<User> tableViewFriends;
    private final ObservableList<User> modelFriends = FXCollections.observableArrayList();

    @FXML
    private TextField textUsername;
    @FXML
    private TextField textPassword;

    @FXML
    private TableColumn<User, String> UsernameFriendColumn;
    @FXML
    private TableColumn<User, String> FirstNameFriendColumn;
    @FXML
    private TableColumn<User, String> LastNameFriendColumn;

    public void setService(Service service, Stage stage) {
        this.service = service;
        this.stage = stage;

        initializeUsers();
        initModelUsers();
    }

    public void setMain(HelloApplication main) {
        this.main = main;
    }

    public void handleLogin() throws SQLException {
        if (textUsername.getText().isEmpty()) {
            MessageAlert.showErrorMessage(null, "Introdu un username");
        } else if (textPassword.getText().isEmpty()) {
            MessageAlert.showErrorMessage(null, "Introdu parola");
        }
        else {
            try {
                if(service.verifyLogin(textUsername.getText(), textPassword.getText())) {
                    User user = service.findUser(textUsername.getText());
                    if (user != null)
                        main.userStage(user);
                }
                else
                    MessageAlert.showErrorMessage(null, "Datele de logare sunt incorecte");
            }
            catch(ServiceException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        }
    }


    @FXML
    public void initializeUsers() {
        UsernameFriendColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        FirstNameFriendColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        LastNameFriendColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableViewFriends.setItems(modelFriends);
    }

    private void initModelUsers() {
        Iterable<User> messages = service.getAllUsers();
        List<User> users = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        modelFriends.setAll(users);
    }



}

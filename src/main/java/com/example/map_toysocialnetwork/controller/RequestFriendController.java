package com.example.map_toysocialnetwork.controller;

import com.example.map_toysocialnetwork.domain.User;
import com.example.map_toysocialnetwork.service.FriendshipRequestService;
import com.example.map_toysocialnetwork.service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RequestFriendController {

    @FXML
    private TextField searchBar;
    @FXML
    private TableView<User> tableViewUsers;
    @FXML
    private TableColumn<User, String> UsernameColumn;
    @FXML
    private TableColumn<User, String> FirstNameColumn;
    @FXML
    private TableColumn<User, String> LastNameColumn;

    private ObservableList<User> modelUsers = FXCollections.observableArrayList();
    private Service service;
    private FriendshipRequestService requestService;
    private User currentUser;
    private Stage stage;

    public void setServices(Service service, FriendshipRequestService requestService, User currentUser, Stage stage) {
        this.service = service;
        this.requestService = requestService;
        this.currentUser = currentUser;
        this.stage = stage;

        initializeTable();
    }

    private void initializeTable() {
        UsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        FirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        LastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableViewUsers.setItems(modelUsers);
    }

    @FXML
    public void handleSearch() {
        String query = searchBar.getText();
        if (query == null || query.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Introduceți un nume pentru căutare.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Iterable<User> users = service.searchUsers(query);
        List<User> userList = StreamSupport.stream(users.spliterator(), false)
                .filter(user -> !user.getUsername().equals(currentUser.getUsername()))
                .collect(Collectors.toList());
        modelUsers.setAll(userList);
    }

    @FXML
    public void handleSendRequest() {
        User selectedUser = tableViewUsers.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Selectați un utilizator pentru a trimite cererea de prietenie.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {
            requestService.addRequest(currentUser.getUsername(), selectedUser.getUsername());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cererea de prietenie a fost trimisă.", ButtonType.OK);
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Eroare: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }
}

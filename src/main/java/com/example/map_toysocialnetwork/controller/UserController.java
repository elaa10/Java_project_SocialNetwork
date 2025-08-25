package com.example.map_toysocialnetwork.controller;

import com.example.map_toysocialnetwork.domain.FriendshipRequest;
import com.example.map_toysocialnetwork.domain.RequestTableModel;
import com.example.map_toysocialnetwork.domain.Tuple;
import com.example.map_toysocialnetwork.service.FriendshipRequestService;
import com.example.map_toysocialnetwork.service.MessageService;
import com.example.map_toysocialnetwork.service.Service;
import com.example.map_toysocialnetwork.domain.User;
import com.example.map_toysocialnetwork.utils.observer.Observer;

import com.example.map_toysocialnetwork.utils.paging.Page;
import com.example.map_toysocialnetwork.utils.paging.Pageable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class UserController implements Observer {

    @FXML
    private TableView<User> tableViewFriends;
    @FXML
    private TableView<RequestTableModel> tableViewRequests;

    Service service;
    FriendshipRequestService requestService;
    User user;
    MessageService messageService;
    private final ObservableList<User> modelFriends = FXCollections.observableArrayList();
    private final ObservableList<RequestTableModel> modelRequests = FXCollections.observableArrayList();

    private Stage stage;
    @FXML
    private TableColumn<RequestTableModel, String> UsernameRequestColumn;
    @FXML
    private TableColumn<RequestTableModel, String> FirstNameRequestColumn;
    @FXML
    private TableColumn<RequestTableModel, String> LastNameRequestColumn;
    @FXML
    private TableColumn<RequestTableModel, String> StatusRequestColumn;
    @FXML
    private TableColumn<RequestTableModel, String> DateRequestColumn;
    @FXML
    private TableColumn<User, String> UsernameFriendColumn;
    @FXML
    private TableColumn<User, String> FirstNameFriendColumn;
    @FXML
    private TableColumn<User, String> LastNameFriendColumn;
    @FXML
    private Button buttonNext;
    @FXML
    private Button buttonPrevious;
    @FXML
    private Label labelPage;


    private int pageSize = 2;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;

    public void setService(User user, Service service, FriendshipRequestService requestService, MessageService messageService, Stage stage) {
        this.user = user;
        this.service = service;
        this.requestService = requestService;
        this.messageService = messageService;
        this.stage = stage;

        service.addObserver(this);
        requestService.addObserver(this);


        initializeFriends();
        initializeRequests();

        initModelFriends();
        initModelRequests();

    }

    @FXML
    public void initializeFriends() {
        UsernameFriendColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        FirstNameFriendColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        LastNameFriendColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableViewFriends.setItems(modelFriends);
//        labelPage.textProperty().addListener(o ->
//            {currentPage = 0;
//            initModelFriends();
//        });
    }

    private void initModelFriends() {
        Page<User> pageUsers = service.findAllOnPage(new Pageable(currentPage, pageSize), this.user.getId());
        int maxPageUser = (int) Math.ceil((double) pageUsers.getTotalNumberOfElements() / pageSize) - 1;

        if (maxPageUser == -1) {
            maxPageUser = 0;
        }

        if (currentPage > maxPageUser) {
            currentPage = maxPageUser;

            pageUsers = service.findAllOnPage(new Pageable(currentPage, pageSize), this.user.getId());
        }


        totalNumberOfElements = pageUsers.getTotalNumberOfElements();
        buttonPrevious.setDisable(currentPage == 0);
        buttonNext.setDisable((currentPage + 1) * pageSize >= totalNumberOfElements);
        modelFriends.setAll(StreamSupport.stream(pageUsers.getElementsOnPage().spliterator(), false).collect(Collectors.toList()));
        labelPage.setText("Page " + (currentPage + 1) + " of " + (maxPageUser + 1));
    }

    public void onNextPage(ActionEvent actionEvent) {
        currentPage ++;
        initModelFriends();
    }

    public void onPreviousPage(ActionEvent actionEvent) {
        currentPage --;
        initModelFriends();
    }

    @FXML
    public void initializeRequests() {
        UsernameRequestColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        FirstNameRequestColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        LastNameRequestColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        StatusRequestColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        DateRequestColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        tableViewRequests.setItems(modelRequests);
    }


    private void initModelRequests() {
        Iterable<Tuple<User, FriendshipRequest>> requests = requestService.getAllRequests(this.user);
        List<RequestTableModel> tableData = StreamSupport.stream(requests.spliterator(), false)
                .map(request -> new RequestTableModel(
                        request.getLeft().getUsername(),
                        request.getLeft().getFirstName(),
                        request.getLeft().getLastName(),
                        request.getRight().getStatus(),
                        request.getRight().getDate().toString()
                ))
                .collect(Collectors.toList());

        modelRequests.setAll(tableData);
    }



    @Override
    public void update() throws SQLException {
        currentPage = 0;
        initModelRequests();
        initModelFriends();

        List<RequestTableModel> currentRequests = modelRequests.stream()
                .filter(request -> request.getStatus().equals("new"))
                .toList();


        for (RequestTableModel request : currentRequests) {
            showNewRequestNotification(request.getUsername());
            updateRequestStatus(request.getUsername());
        }
    }

    private void showNewRequestNotification(String username) {
        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,
                "Cerere nouă de prietenie", "Cerere nouă de prietenie de la @" + username);
    }


    private void updateRequestStatus(String username) {
        try {
            requestService.updateRequestStatus(username, this.user.getUsername(), "Pending");

            initModelRequests();
        } catch (SQLException e) {
            MessageAlert.showErrorMessage(null, "Eroare la actualizarea statusului cererii: " + e.getMessage());
        }
    }


    public void handleDelete() {
        try {
            User selectedUser = tableViewFriends.getSelectionModel().getSelectedItem();
            if (selectedUser == null) {
                MessageAlert.showErrorMessage(null, "Nu ați selectat niciun prieten pentru a șterge!");
                return;
            }
            service.removeFriendship(user.getUsername(), selectedUser.getUsername());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Elimina userul din lista de prieteni", "User eliminat cu succes!");
            initModelFriends();
        } catch (Exception e){
            MessageAlert.showErrorMessage(null, "Eroare: " + e.getMessage());
        }
    }


    public void handleAddFriend() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../request_friend-view.fxml"));

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Caută și adaugă prieteni");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setResizable(false);

            RequestFriendController controller = loader.getController();
            controller.setServices(service, requestService, user, dialogStage);

            Scene scene = new Scene(root, 500, 450);
            dialogStage.setScene(scene);
            dialogStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            MessageAlert.showErrorMessage(null, "Eroare: " + e.getMessage());
        }
    }

    public void handleOpenMessages() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../messages-view.fxml"));

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Trimite un mesaj sau incepe o noua conversatie");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setResizable(false);

            MessagesController controller = loader.getController();
            controller.setServices(messageService , user, dialogStage);

            Scene scene = new Scene(root, 500, 450);
            dialogStage.setScene(scene);
            dialogStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            MessageAlert.showErrorMessage(null, "Eroare: " + e.getMessage());
        }
    }


    public void handleRejectRequest() {
        String name = tableViewRequests.getSelectionModel().getSelectedItem().getUsername();
        if (user != null) {
            try {
                requestService.removeRequest(this.user.getUsername(), name);
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Refuza cererea", "Cererea a fost refuzata!");
            } catch(Exception e){
                MessageAlert.showErrorMessage(null, "Eroare la refuzarea cererii: " + e.getMessage());
            }
        } else {
            MessageAlert.showErrorMessage(null, "Niciun utilizator selectat.");
        }
    }

    public void handleAcceptRequest() {
        String name = tableViewRequests.getSelectionModel().getSelectedItem().getUsername();
        if (user != null) {
            try {
                requestService.acceptRequest(user.getUsername(), name);
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Accepta cererea", "Cererea a fost acceptata!");
            }
            catch(Exception e){
                MessageAlert.showErrorMessage(null, "Eroare la refuzarea cererii: " + e.getMessage());
            }
        } else {
            MessageAlert.showErrorMessage(null, "Niciun utilizator selectat.");
        }
    }


}

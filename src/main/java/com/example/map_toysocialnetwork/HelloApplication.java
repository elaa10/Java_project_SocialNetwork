package com.example.map_toysocialnetwork;

import com.example.map_toysocialnetwork.controller.LoginController;
import com.example.map_toysocialnetwork.controller.UserController;
import com.example.map_toysocialnetwork.domain.User;
import com.example.map_toysocialnetwork.domain.validators.FriendshipRequestValidator;
import com.example.map_toysocialnetwork.domain.validators.FriendshipValidator;
import com.example.map_toysocialnetwork.domain.validators.MessageValidator;
import com.example.map_toysocialnetwork.domain.validators.UserValidator;
import com.example.map_toysocialnetwork.repository.dataBase.FriendshipDataBaseRepository;
import com.example.map_toysocialnetwork.repository.dataBase.MessageDataBaseRepository;
import com.example.map_toysocialnetwork.repository.dataBase.RequestDataBaseRepository;
import com.example.map_toysocialnetwork.repository.dataBase.UserDataBaseRepository;
import com.example.map_toysocialnetwork.service.FriendshipRequestService;
import com.example.map_toysocialnetwork.service.MessageService;
import com.example.map_toysocialnetwork.service.Service;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    final String url = "jdbc:postgresql://localhost:5432/postgres";
    final String user = "postgres";
    final String password = "1005";

    UserDataBaseRepository userDataBaseRepo = new UserDataBaseRepository(url,user,password, new UserValidator());
    FriendshipDataBaseRepository friendshipDataBaseRepository = new FriendshipDataBaseRepository(url, user, password, new FriendshipValidator());
    RequestDataBaseRepository requestDataBaseRepository = new RequestDataBaseRepository(url, user, password, new FriendshipRequestValidator());
    MessageDataBaseRepository messageDataBaseRepository = new MessageDataBaseRepository(url, user, password, new MessageValidator(), userDataBaseRepo);

    Service service = new Service(userDataBaseRepo, friendshipDataBaseRepository);
    FriendshipRequestService requestService = new FriendshipRequestService(userDataBaseRepo, friendshipDataBaseRepository, requestDataBaseRepository);
    MessageService messageService = new MessageService(messageDataBaseRepository, userDataBaseRepo);


    @Override
    public void start(Stage primaryStage) throws IOException {
        loginStage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void loginStage(Stage primaryStage) throws IOException {
        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("login-view.fxml"));

        VBox loginVBox = loginLoader.load();
        LoginController loginController = loginLoader.getController();
        loginController.setMain(this);

        Scene loginScene = new Scene(loginVBox);

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Login - Social Network");

        loginController.setService(service, primaryStage);

        primaryStage.show();
    }

    public void userStage(User user) {
        try {
            FXMLLoader userLoader = new FXMLLoader();
            userLoader.setLocation(getClass().getResource("user-view.fxml"));

            Stage userStage = new Stage();
            Scene userScene = null;
            userScene = new Scene(userLoader.load());

            userStage.setTitle("ACASA - User");
            userStage.setScene(userScene);

            UserController userController = userLoader.getController();
            userController.setService(user, service,requestService, messageService, userStage);

            userStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
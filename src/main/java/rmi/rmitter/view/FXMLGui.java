package rmi.rmitter.view;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import rmi.rmitter.control.RemoteController;
import rmi.rmitter.model.*;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


/**
 * Created by affo on 04/04/18.
 */
public class FXMLGui extends Application
        implements FeedObserver, UserObserver, RemoteBaseView {
    @FXML
    private TextField username;
    @FXML
    private TextField follow;
    @FXML
    private TextField hashtag;
    @FXML
    private TextField post;
    @FXML
    private TextArea feed;


    private RemoteController controller;
    private String currentToken;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Registry registry = LocateRegistry.getRegistry();

        // gets a reference for the remote controller
        RemoteController controller = (RemoteController) registry.lookup("controller");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/frontA.fxml"));

        Parent rootComponent = loader.load(); // load the interface to load the controller and get it
        FXMLGui gui = loader.getController();
        gui.controller = controller; // `gui` is the correct instance of FXMLGui managed by FXML
        UnicastRemoteObject.exportObject(gui, 0); // it is also the one that should be exported

        primaryStage.setTitle("TwitterJ");
        primaryStage.setScene(new Scene(rootComponent));
        primaryStage.show();
        primaryStage.setOnCloseRequest((event -> {
            try {
                UnicastRemoteObject.unexportObject(this, true);
            } catch (NoSuchObjectException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }));
    }

    private void writeToFeed(String message) {
        feed.appendText(message + "\n");
    }

    public void loginPressed(ActionEvent actionEvent) {
        try {
            String username = this.username.getText();
            String token = controller.login(username, this);
            controller.observeUser(token, username, this, this);
            writeToFeed(">>> [OK]: Logged in as @" + username);
            this.currentToken = token;
        } catch (RemoteException e) {
            writeToFeed(">>> [ERROR]: " + e.getCause().getMessage());
        }
    }

    public void followPressed(ActionEvent actionEvent) {
        try {
            String username = this.follow.getText();
            controller.followUser(currentToken, username);
            writeToFeed(">>> [OK]: Following @" + username);
        } catch (RemoteException e) {
            writeToFeed(">>> [ERROR]: " + e.getCause().getMessage());
        }
    }

    public void hashtagPressed(ActionEvent actionEvent) {
        try {
            String ht = this.hashtag.getText();
            controller.followHashtag(currentToken, ht, this);
            writeToFeed(">>> [OK]: Following #" + ht);
        } catch (RemoteException e) {
            writeToFeed(">>> [ERROR]: " + e.getCause().getMessage());
        }
    }

    public void enterPressed(ActionEvent actionEvent) {
        try {
            if (currentToken != null) {
                controller.post(currentToken, post.getText());
                writeToFeed(">>> [OK]: Posted");
            }
            post.setText("");
        } catch (RemoteException e) {
            writeToFeed(">>> [ERROR]: " + e.getCause().getMessage());
        }
    }

    // ---------------------------------- Remote events

    @Override
    public void onFolloweePost(Post post) throws RemoteException {
        writeToFeed("@" + post.getPoster().getUsername() + " -> " + post.getContent());
    }

    @Override
    public void onHashtagUse(Hashtag hashtag, Post post) throws RemoteException {
        writeToFeed("Hashtag " + hashtag + " used:");
        writeToFeed(post.getContent());
    }

    @Override
    public void onNewPost(Post post) throws RemoteException {
        writeToFeed(post.getContent());
    }

    @Override
    public void onMention(Post post) throws RemoteException {
        writeToFeed("You were mentioned in:");
        writeToFeed(post.getContent());
    }

    @Override
    public void onFollower(User user) throws RemoteException {
        writeToFeed("New follower: " + user.getUsername());
    }

    @Override
    public void error(String message) throws RemoteException {
        writeToFeed(">>> " + message);
    }

    @Override
    public void ack(String message) throws RemoteException {
        writeToFeed(">>> " + message);
    }
}

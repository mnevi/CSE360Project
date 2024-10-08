package edu.asu.ASUHelloWorldJavaFXMaven;



import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.*;
import java.io.*;
import java.sql.SQLException;


public class HelloFX extends Application {
	StartCSE360 a=new StartCSE360();
	String email,password;

    public void start(Stage primaryStage) {
        primaryStage.setTitle("HelpSystem for CSE360");
        Label l1=new Label("Enter admin email");
        TextField t1=new TextField("");
        t1.setPromptText("email");
        t1.setMaxWidth(300);
       
        Label l2=new Label("Enter Password");
        TextField t2=new TextField("");
        t2.setPromptText("password");
        t2.setMaxWidth(300);
        Button btn = new Button();
        btn.setText("Submit");
        btn.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	email = t1.getText();
                password = t2.getText();

					try {
						a.setupAdministrator(email,password);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						System.out.println("Error there");
						e.printStackTrace();
					}
				
            }
        });
        
        
        
        VBox root = new VBox();
        root.setSpacing(10);
        root.getChildren().add(l1);
        root.getChildren().add(t1);
        root.getChildren().add(l2);
        root.getChildren().add(t2);
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 600, 350));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
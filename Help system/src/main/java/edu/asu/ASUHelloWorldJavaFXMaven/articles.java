package edu.asu.ASUHelloWorldJavaFXMaven;

import java.sql.SQLException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*******
 * <p> articles Class </p>
 * 
 * <p> Description: This class acts as a menu to print all operations that can be performed 
 * on an article and various methods to manipulate it </p>
 * 
 * 
 * @author Tushar Sachan, Max Neville, Taj Yoshimura, Alan Lintemuth, William McLean
 * 
 * @version 1.0		Development Phase 1 (Articles initial menu)
 * 
 */

public class articles extends Application {
	articlehelper help=new articlehelper();
    VBox v;

    /**********
	 * This is the start method that is called once the application has been and it sets up the stage
	 * to view all GUI components
	 */
    public void start(Stage primaryStage) throws SQLException {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

        System.out.println("It started!");
        printmenu(primaryStage); // Initialize the buttons in VBox v

        root.getChildren().add(v); // Add VBox with buttons to the root VBox

        StackPane stackPane = new StackPane(root); // StackPane to center root VBox in the scene
        Scene scene = new Scene(stackPane, 400, 250);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Articles");
        primaryStage.show();
    }
    
    /**********
	 * Sets up the buttons so that necessary actions can be implemented
	 */
    public void printmenu(Stage stage) {
        v = new VBox(10); // Add spacing between buttons
        v.setAlignment(Pos.CENTER); // Center-align buttons in the VBox

        Button b1 = new Button("Create new article");
        Button b12 = new Button("View all article");
        Button b4 = new Button("Search article");
        Button b2 = new Button("Remove article");
        Button b3 = new Button("Update article");
        Button b5 = new Button("Backup");
        Button b6 = new Button("Restore");
        v.getChildren().addAll(b1, b12, b4, b2, b3, b5, b6); // Add all buttons to VBox v
        
        //creating a new article
        b1.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	help.work=1;
            	try {
					help.start(new Stage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        
        //viewing all articles in database
        b12.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	help.work=3;
            	try {
					help.start(new Stage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        
        //remove article
        b2.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	help.work=2;
            	try {
					help.start(new Stage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        
        //update article
        b3.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	help.work=4;
            	try {
					help.start(new Stage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        
        //search article by groups or id
        b4.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	help.work=5;
            	try {
					help.start(new Stage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        
        //backup
        b5.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	help.work=6;
            	try {
					help.start(new Stage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        
        //restore
        b6.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	help.work=7;
            	try {
					help.start(new Stage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package edu.asu.ASUHelloWorldJavaFXMaven;

import java.sql.SQLException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class roles extends Application{
	BorderPane cp=new BorderPane();
	String roleuser="";
	StartCSE360 helper = new StartCSE360();
	Admin adminhelper=new Admin();
	
	
	public void start(Stage primaryStage) {
		BorderPane root=new BorderPane();
		
		switch (this.roleuser) {
        case "111":
           all(primaryStage);  // Admin + Instructor + Student
            break;
        case "110":
        	admininstructor(primaryStage);  // Admin + Instructor
            break;
        case "101":
        	adminstudent(primaryStage);  // Admin + Student
            break;
        case "100":
        	admin(primaryStage);  // Only Admin
            break;
        case "011":
        	instructorstudent(primaryStage);  // Instructor + Student
            break;
        case "010":
        	instructor(primaryStage);  // Only Instructor
            break;
        case "001":
        	student(primaryStage);  // Only Student
            break;
        
    }
		root.setCenter(cp);
		primaryStage.setScene(new Scene(root,400,250));
		primaryStage.show();
	}
	public void setrole(String role) {
		this.roleuser=role;
	}
	
	
	public void all(Stage primaryStage) {
		VBox v=new VBox();
		Label lb=new Label("Please select a role to continue");
		Button b1=new Button("Admin");
		Button b2=new Button("Instructor");
		Button b3=new Button("Student");
		v.getChildren().add(lb);
		v.getChildren().add(b1);
		v.getChildren().add(b2);
		v.getChildren().add(b3);
		Button b=new Button("Log out");
		cp.setCenter(v);
		
		cp.setRight(b);
		b1.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	admin(primaryStage);
            }
        });
		b2.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	instructor(primaryStage);
            }
        });
		b3.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	student(primaryStage);
            }
        });
		
	}
	
	public void admininstructor(Stage primaryStage) {
		VBox v=new VBox();
		Label lb=new Label("Please select a role to continue");
		Button b1=new Button("Admin");
		Button b2=new Button("Instructor");
		Button b=new Button("Log out");
		v.getChildren().add(lb);
		v.getChildren().add(b1);
		v.getChildren().add(b2);
		cp.setCenter(v);
		cp.setRight(b);
		b1.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	admin(primaryStage);
            }
        });
		b2.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	instructor(primaryStage);
            }
        });
	}
	
	public void adminstudent(Stage primaryStage) {
		VBox v=new VBox();
		Label lb=new Label("Please select a role to continue");
		Button b=new Button("Log out");
		Button b1=new Button("Admin");
		Button b3=new Button("Student");
		v.getChildren().add(lb);
		v.getChildren().add(b1);
		v.getChildren().add(b3);
		cp.setCenter(v);
		cp.setRight(b);
		b3.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	student(primaryStage);
            }
        });
		b1.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	admin(primaryStage);
            }
        });
		
	}
	
	public void admin(Stage primaryStage) {
		VBox v = new VBox();
		Label ll=new Label("");
		ScrollPane s=new ScrollPane(ll);
		s.setPrefSize(300, 150);
		s.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);  
        s.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		Label lb=new Label("Welcome to admin role");
		TextField t1=new TextField();
		Button delete=new Button("Delete A User");
		Button list=new Button("List all Users");
		Button removeroles=new Button("Remove roles of users");
		Button b=new Button("Log out");
		
		v.getChildren().add(lb);
		
		v.getChildren().add(delete);
		v.getChildren().add(t1);
		v.getChildren().add(list);
		v.getChildren().add(removeroles);
		
		cp.setCenter(v);
		cp.setRight(b);
		delete.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	
        		
            	if(helper.checkuser(t1.getText()))
            	{
            		
            	try {
					helper.delete(t1.getText());
					ll.setText("User has been deleted");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            	else
            		ll.setText("Username does not exist");}
        });
		list.setOnAction(new EventHandler<>() { 
            public void handle(ActionEvent event) {
            	try {
					String users=helper.show();
					//ll.setText(users);
					//v.getChildren().add(s);
					adminhelper.setUsers(users);
					adminhelper.showusers();
					
			        
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
		b.setOnAction(new EventHandler<>() { 
            public void handle(ActionEvent event) {
            	logout(primaryStage);
            }
        });
		removeroles.setOnAction(new EventHandler<>() { 
            public void handle(ActionEvent event) {
            	adminhelper.removeroles();
            }
        });
	}
	
	public void logout(Stage primaryStage)
	{
		primaryStage.close();
	}
	
	public void instructorstudent(Stage primaryStage) {
		Label lb=new Label("Please select a role to continue");
		cp.getChildren().clear();
		VBox vb=new VBox();
		Button b1=new Button("Instructor");
		Button b2=new Button("Student");
		vb.getChildren().add(lb);
		vb.getChildren().add(b1);
		vb.getChildren().add(b2);
		cp.setCenter(vb);
		b1.setOnAction(new EventHandler<>() { 
            public void handle(ActionEvent event) {
            	cp.getChildren().clear();
            	instructor(primaryStage);
            	
            }
        });
		b2.setOnAction(new EventHandler<>() { 
            public void handle(ActionEvent event) {
            	cp.getChildren().clear();
            	student(primaryStage);
            }
        });
		System.out.println("was here");
		

	}
	public void instructor(Stage primaryStage) {
		Label lb=new Label("Welcome to Instructor role");
		Button b=new Button("Log out");
		cp.setCenter(lb);
		cp.setRight(b);
		b.setOnAction(new EventHandler<>() { 
            public void handle(ActionEvent event) {
            	primaryStage.close();
            	
            }
        });
		
		
	}
	
	public void student(Stage primaryStage) {
		Label lb=new Label("Welcome to Student role");
		Button b=new Button("Log out");
		cp.setCenter(lb);
		cp.setRight(b);
		b.setOnAction(new EventHandler<>() { 
            public void handle(ActionEvent event) {
            	primaryStage.close();
            	
            }
        });
		
	}
	public static void main(String[] args) {
		launch(args);
	}
	
}
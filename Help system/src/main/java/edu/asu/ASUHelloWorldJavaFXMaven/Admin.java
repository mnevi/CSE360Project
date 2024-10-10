package edu.asu.ASUHelloWorldJavaFXMaven;

import java.sql.SQLException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Admin extends Application{
	StartCSE360 helper=new StartCSE360();
	BorderPane root;
	private String useradmin="";
	
	public void start(Stage primaryStage) {
		root.setPadding(new Insets(10, 10, 10, 10));
		primaryStage.setScene(new Scene(root,400,250));
		primaryStage.show();
	}
	
	public void showusers() {
		root=new BorderPane();
		root.getChildren().clear();
		start(new Stage());
		Label l=new Label(useradmin);
		ScrollPane scrollPane = new ScrollPane(l);
	    scrollPane.setPrefSize(300, 150);  
	    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);  
	    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	    root.setCenter(scrollPane);
	}
	public void setUsers(String user) {
		useradmin=user;
	}
	public void removeroles() {
		VBox v=new VBox();
		root=new BorderPane();
		root.getChildren().clear();
		start(new Stage());
		Label l1=new Label("Enter username of user to change roles");
		TextField t1=new TextField();
		Button submit=new Button("Submit");
		t1.setPrefWidth(100);
		v.getChildren().add(l1);
		v.getChildren().add(t1);
		v.getChildren().add(submit);
		root.setCenter(v);
		submit.setOnAction(new EventHandler<>() { 
            public void handle(ActionEvent event) {
            	root.getChildren().clear();
            	try {
					editroles(t1.getText(),root);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
		
	}
	
	public void editroles(String username,BorderPane root) throws SQLException {
		VBox v=new VBox();
		String name=helper.prefname(username);
		Label l1=new Label("Add/Remove roles for "+name);
		String role="";
		try {
			role=helper.findrole(username);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CheckBox c1=new CheckBox();
		CheckBox c2=new CheckBox();
		CheckBox c3=new CheckBox();
		Button submit=new Button("Submit");
		switch (role) {
        case "111":
        	c1.setSelected(true); 
        	c2.setSelected(true); 
        	c3.setSelected(true); 
            break;
        case "110":
        	c1.setSelected(true); 
        	c2.setSelected(true); 
        	c3.setSelected(false); 
            break;
        case "101":
        	c1.setSelected(true); 
        	c2.setSelected(false); 
        	c3.setSelected(true); 
            break;
        case "100":
        	c1.setSelected(true); 
        	c2.setSelected(false); 
        	c3.setSelected(false); 
            break;
        case "011":
        	c1.setSelected(false); 
        	c2.setSelected(true); 
        	c3.setSelected(true); 
            break;
        case "010":
        	c1.setSelected(false); 
        	c2.setSelected(true); 
        	c3.setSelected(false); 
            break;
        case "001":
        	c1.setSelected(false); 
        	c2.setSelected(false); 
        	c3.setSelected(true); 
            break;
            
        
    }
		v.getChildren().add(l1);
		v.getChildren().add(c1);
		v.getChildren().add(c2);
		v.getChildren().add(c3);
		v.getChildren().add(submit);
		root.setCenter(v);
		submit.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	String newrole="";
            	newrole+= c1.isSelected()?"1":"0";
            	newrole+= c2.isSelected()?"1":"0";
            	newrole+= c3.isSelected()?"1":"0";
            		try {
						helper.setrole(newrole, username);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            }
        });
	}
	
	public static void main(String[] args) {
		launch(args);}
}
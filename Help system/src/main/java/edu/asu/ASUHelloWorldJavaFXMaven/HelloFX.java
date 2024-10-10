package edu.asu.ASUHelloWorldJavaFXMaven;

import java.sql.SQLException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class HelloFX extends Application {
	roles roles=new roles();
	StartCSE360 helper=new StartCSE360();
	Admin adminhelper=new Admin();
	
    public void start(Stage primaryStage) throws SQLException {
    	VBox root = new VBox();
    	System.out.println("It started!");
        primaryStage.setTitle("AuthManager");
        if(helper.check()) {
        	firstAdminLogin(root,helper);
        }
        else
        	userLogin(root,helper);
        primaryStage.setScene(new Scene(root, 400, 250));
        primaryStage.show();
    }
    
    public void firstAdminLogin(VBox root,StartCSE360 helper) {
    	Label l1=new Label("Enter Username");
    	TextField t1=new TextField();
    	Label l2=new Label("Enter Password");
    	PasswordField t2=new PasswordField();
    	Label l3=new Label("Confirm Password");
    	Label l4=new Label("");
    	PasswordField t3=new PasswordField();
    	CheckBox c1=new CheckBox("Admin");
    	CheckBox c2=new CheckBox("Instructor");
    	CheckBox c3=new CheckBox("Student");
    	Button btn = new Button("Admin Signup");
    	root.getChildren().add(l1);
    	root.getChildren().add(t1);
    	root.getChildren().add(l2);
    	root.getChildren().add(t2);
    	root.getChildren().add(l3);
    	root.getChildren().add(t3);
    	root.getChildren().add(c1);
    	root.getChildren().add(c2);
    	root.getChildren().add(c3);
    	root.getChildren().add(btn);
    	root.getChildren().add(l4);
    	
    	btn.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	String roles = rolehelper(c1.isSelected(),c2.isSelected(),c3.isSelected());
            	if(t2.getText().equals(t3.getText()))
            	{
            	try {
					helper.setupAdministrator(t1.getText(),t2.getText(),roles);
					userLogin(root,helper);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	}
            	else
            		l4.setText("Passwords do not match");
            		
            }
        });
    	
    }
    
    public void userLogin(VBox root, StartCSE360 helper) {
    	root.getChildren().clear();
    	Label l1=new Label("Enter Username");
    	TextField t1=new TextField();
    	Label l2=new Label("Enter Password");
    	
    	TextField t2=new TextField();
    	Button btn = new Button("Log In");
    	Button btn2 = new Button("Sign Up");
    	Label l3=new Label("");
    	root.getChildren().add(l1);
    	root.getChildren().add(t1);
    	root.getChildren().add(l2);
    	root.getChildren().add(t2);
    	root.getChildren().add(btn);
    	
    	root.getChildren().add(btn2);
    	root.getChildren().add(l3);
    	btn.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	String role="";
            	if(helper.login(t1.getText(),t2.getText())) {
            		try {
						if(helper.findaccess(t1.getText()))
						{
							role=helper.findrole(t1.getText());
							loginuserroles(root,helper,role);
						}
						else
						userdetail(root,t1.getText(),helper);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	} else
					try {
						if(helper.checktemp(t2.getText()))
						{
							
							{
								helper.updateuser(t1.getText(), t2.getText());
								adminhelper.permpassword(t1.getText());
							}
						}
						else
						l3.setText("Invalid username or password");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            }
        });
    	
    	btn2.setOnAction(new EventHandler<>() { 
            public void handle(ActionEvent event) {
            	createUser(root,helper);
            }
        });
    }
    
    public void userdetail(VBox root,String username,StartCSE360 helper) {
    	root.getChildren().clear();
    	Label ln=new Label("Finish Setting up your account");
    	Label l1=new Label("Enter Email");
    	TextField t1=new TextField();
    	Label l2=new Label("Enter First Name");
    	TextField t2=new TextField();
    	Label l3=new Label("Enter Middle Name");
    	TextField t3=new TextField();
    	Label l4=new Label("Enter Last Name");
    	TextField t4=new TextField();
    	Label l5=new Label("Enter Preferred Name");
    	TextField t5=new TextField();
    	Button btn = new Button("Submit");
    	root.getChildren().add(ln);
    	root.getChildren().add(l1);
    	root.getChildren().add(t1);
    	root.getChildren().add(l2);
    	root.getChildren().add(t2);
    	root.getChildren().add(l3);
    	root.getChildren().add(t3);
    	root.getChildren().add(l4);
    	root.getChildren().add(t4);
    	root.getChildren().add(l5);
    	root.getChildren().add(t5);

    	root.getChildren().add(btn);
    	btn.setOnAction(new EventHandler<>() { 
            public void handle(ActionEvent event) {
            	helper.execute(t1.getText(),t2.getText(),t3.getText(),t4.getText(),t5.getText(),username);
            	userLogin(root,helper);
            }
        });

    	
    }
    
    public void createUser(VBox root,StartCSE360 helper) {
    	root.getChildren().clear();
    	Label l1=new Label("Enter Username");
    	TextField t1=new TextField();
    	Label l2=new Label("Enter Password");
    	TextField t2=new TextField();
    	Label l3=new Label("Confirm Password");
    	Label l4=new Label("");
    	TextField t3=new TextField();
   
    	
    	root.getChildren().add(l1);
    	root.getChildren().add(t1);
    	root.getChildren().add(l2);
    	root.getChildren().add(t2);
    	root.getChildren().add(l3);
    	root.getChildren().add(t3);
  
    	Button btn = new Button();
    	Button btn2 = new Button();
    	Button btn3 = new Button("Back");
    	root.getChildren().add(btn);
    	root.getChildren().add(btn2);
    	root.getChildren().add(btn3);
    	
        btn.setText("SUBMIT");
        btn2.setText("DELETE");
        root.getChildren().add(l4);
        btn.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	if(t2.getText().equals(t3.getText())) 
            	{
            	try {
					if(helper.checktemp(t2.getText()))
					{
						if(helper.checkuser(t1.getText()))
						{
							l4.setText("User already in the system");
						}
						else
						{
							helper.updateuser(t1.getText(), t2.getText());
							adminhelper.permpassword(t1.getText());
						}
					}
					else
						l4.setText("Invalid Password");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            }
            	else
            		l4.setText("Passwords do not match");}
            
        });
        btn2.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	try {
					helper.deleteall();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
        btn3.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	userLogin(root,helper);
            }
        });
    }
    
    public void loginuserroles(VBox root,StartCSE360 helper,String role) {
    	roles.setrole(role);
    	roles.start(new Stage());		
    }
    
    public String rolehelper(Boolean a,Boolean b, Boolean c) {
    	String s="";
    	s+= a==true?"1":"0";
    	s+= b==true?"1":"0";
    	s+= c==true?"1":"0";
    	return s;		
    }
    
    

    public static void main(String[] args) {
        launch(args);
    }

}
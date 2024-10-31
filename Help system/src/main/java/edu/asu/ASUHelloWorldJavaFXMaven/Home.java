package edu.asu.ASUHelloWorldJavaFXMaven;

import java.sql.SQLException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/*******
 * <p> Home Class </p>
 * 
 * <p> Description: This JavaFX class is the entry point to our GUI and calls methods
 * to login, sign up, and handle admin sign in </p>
 * 
 * 
 * @author Tushar Sachan, Max Neville, Taj Yoshimura, Alan Lintemuth, William McLean
 * 
 * @version 1.0		Development Phase 1 (User authrorization)
 * 
 */

public class Home extends Application {
	roles roles=new roles();
	StartCSE360 helper=new StartCSE360();
	Admin adminhelper=new Admin();
	
	/**********
	 * This is the start method that is called once the application has been loaded into memory and
	 * is ready to get to work.
	 */
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
    
    /**********
	 * This method checks if database is empty and prompts 
	 * to enter username and password for initial admin
	 */
    public void firstAdminLogin(VBox root,StartCSE360 helper) {
    	
    	//setting the GUI 
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
    	
    	//adding all components to the stage
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
    	c1.setSelected(true);
    	c1.setDisable(true);
    	
    	//sends the primary admin information to database and save it
    	btn.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	char[] passwordChars = t2.getText().toCharArray();
            	String pass="";
            	for(char c:passwordChars)
            		pass+=c;
            	String roles = rolehelper(true,c2.isSelected(),c3.isSelected());
            	if(t2.getText().equals(t3.getText()))
            	{
            	try {
					helper.setupAdministrator(t1.getText(),pass,roles);
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
    
    /**********
	 * This method is the login page and checks if the user is in the
	 * database and authorizes the login
	 */
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
    	
    	//this handler authorizes the login by checking if username and password is in database
    	btn.setOnAction(new EventHandler<>() {
    		
            public void handle(ActionEvent event) {
            	//gets password input as chr[] array for safety eason
            	char[] passwordChars = t2.getText().toCharArray();
            	String pass="";
            	for(char c:passwordChars)
            		pass+=c;
            	String role="";
            	//check if the credentials are in database
            	if(helper.login(t1.getText(),pass)) {
            		try {
            			//checks if user has never logged in before and ask for their information
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
						//executes if the username exists, but the password is reset by admin
						if(helper.checktemp(pass))
						{
							
							{
								helper.updateuser(t1.getText(), pass);
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
    	
    	//This button takes the user to a signup page to register new user
    	btn2.setOnAction(new EventHandler<>() { 
            public void handle(ActionEvent event) {
            	createUser(root,helper);
            }
        });
    }
    
    /**********
	 * This function asks for primary details of the user after the username 
	 * and password are already set up
	 */
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
    
    /**********
	 * This method creates as user by signup button
	 */
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
  
    	Button btn3 = new Button("Back");
    	root.getChildren().add(btn);

    	root.getChildren().add(btn3);
    	
        btn.setText("SUBMIT");
     
        root.getChildren().add(l4);
        //submit handler to create and push a new user in the database
        btn.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	String s="";
            	char[] passwordChars = t2.getText().toCharArray();
            	for(char c:passwordChars)
            		s+=c;
            	String s1="";
            	char[] passwordChar = t3.getText().toCharArray();
            	for(char c:passwordChar)
            		s1+=c;
        		System.out.println(s);
        		System.out.println(s1);
            	if(s.equals(s1)) 
            	{
            		
            		
            	try {
					if(helper.checktemp(s))
					{
						if(helper.checkuser(t1.getText()))
						{
							l4.setText("User already in the system");
						}
						else
						{
							helper.updateuser(t1.getText(), s);
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
        //This button takes the user back to the login page
        btn3.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	userLogin(root,helper);
            }
        });
    }
    
    /**********
	 * This function is a helper and sets the role entered by admin
	 */
    public void loginuserroles(VBox root,StartCSE360 helper,String role) {
    	roles.setrole(role);
    	roles.start(new Stage());		
    }
    
    /**********
	 * This function determines what all roles the user has by terminary if conditions
	 */
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
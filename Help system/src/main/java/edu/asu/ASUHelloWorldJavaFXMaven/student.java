package edu.asu.ASUHelloWorldJavaFXMaven;

import java.sql.SQLException;

/*******
* <p> Student Class. </p>
*
* <p> Description: A Java Class for dealing with students </p>
*
* @author Tushar Sachan, Max Neville, Taj Yoshimura, Alan Lintemuth, William McLean
*
* @version 1.00 handles student behaviors
* 
*/
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class student extends Application {
	
    VBox v;
    public int work;
    ArticleDatabaseHelper helparticle=new ArticleDatabaseHelper();
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		VBox root = new VBox();
        root.setAlignment(Pos.CENTER);

      
        switch(work) {
        case 1:
        	sendmessage(primaryStage);
        	break;
        case 2:
        	viewall(primaryStage);
        	break;
        case 3:
        	list(primaryStage);
        	break;
        case 4:
        	
        	break;
        case 5:
        	
        	break;
        default:
        	break;
        }

        root.getChildren().add(v); // Add VBox with buttons to the root VBox
        
        StackPane stackPane = new StackPane(root); // StackPane to center root VBox in the scene
        Scene scene = new Scene(stackPane, 500, 350);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Send message");
        primaryStage.show();
	}
	
	/**
     * This functions gives options to select if we want
     * to list everything or list by filters
     */
	private void list(Stage primaryStage) throws SQLException {
		helparticle.connectToDatabase();
		v = new VBox(10);
		Label l=new Label("Select relevant listing option");
		Button b=new Button("List all articles");
		Button b2=new Button("Custom filters on articles");
		v.getChildren().addAll(l,b,b2);
		b.setOnAction(new EventHandler<>() {
	            public void handle(ActionEvent event) {
	            	try {
	            		v.getChildren().clear();
						listall(v);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        });
		b2.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	v.getChildren().clear();
            	try {
					customfilter(v);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
	}
	
	/**
     * This function displays articles based on user input
     */
	private void customfilter(VBox v) throws Exception {
		ComboBox<String> comboBox = new ComboBox<>();
	    comboBox.getItems().addAll("View All", "beginner", "intermediate", "advanced", "expert");
	    comboBox.setValue("View All");
		TextField t1=new TextField();
		TextField t2=new TextField();
		TextField t3=new TextField();
		TextField t4=new TextField();
		t1.setPromptText("Filter by Title");
	    t2.setPromptText("Filter by Author");
	    t3.setPromptText("Filter By Abstract");
	    t4.setPromptText("Filter By ID");
		Button b=new Button("Filter");
		ScrollPane sc=new ScrollPane();
		String s= helparticle.listArticle();
		Label l=new Label(s);
		Label l2=new Label();
		sc.setContent(l);
		
		v.getChildren().addAll(comboBox,t1,t2,t3,b,sc);
		v.setAlignment(Pos.CENTER);
		b.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	if(!(t1.getText().equals(""))) {
            		try {
						String s=helparticle.filterbyTitle(t1.getText());
						Label l=new Label(s);
						sc.setContent(l);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            	else if(!(t2.getText().equals(""))) {
            		try {
						String s=helparticle.filterbyauthor(t2.getText());
						Label l=new Label(s);
						sc.setContent(l);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            	else if(!(t3.getText().equals(""))) {
            		try {
						String s=helparticle.filterbyabstract(t3.getText());
						Label l=new Label(s);
						sc.setContent(l);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            	else if(!(t4.getText().equals(""))) {
            		try {
						String s=helparticle.filterbyid(t4.getText());
						Label l=new Label(s);
						sc.setContent(l);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            	else if(comboBox.getValue().equals("View All")) {
            		try {
            			String s= helparticle.listArticle();
            			Label l=new Label(s);
	            		sc.setContent(l);
					}
                	catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
            	}
            	else if(comboBox.getValue().equals("beginner")) {
            		int lastCharAsInt;
            		try {
						String s=helparticle.filterbybeginner("beginner");
						if(s.equals("")) {
							s="No results found ";
							lastCharAsInt=0;}else {
						char lastChar = s.charAt(s.length() - 1); // Get the last character
						lastCharAsInt = Integer.parseInt(String.valueOf(lastChar));}
						
						
						Label l=new Label("Group: Beginner\n"+"Number of records found: "+lastCharAsInt+"\n"+s.substring(0,s.length()-2));
	            		sc.setContent(l);
					}
                	catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
            	}
            	else if(comboBox.getValue().equals("advanced")) {
            		int lastCharAsInt;
            		try {
						String s=helparticle.filterbybeginner("advanced");
						if(s.equals("")) {
							s="No results found ";
							lastCharAsInt=0;}else {
						char lastChar = s.charAt(s.length() - 1); // Get the last character
						lastCharAsInt = Integer.parseInt(String.valueOf(lastChar));}
						
						
						Label l=new Label("Group: Advanced\n"+"Number of records found: "+lastCharAsInt+"\n"+s.substring(0,s.length()-2));
	            		sc.setContent(l);
					}
                	catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
            	}
            	else if(comboBox.getValue().equals("intermediate")) {
            		try {
						String s=helparticle.filterbybeginner("intermediate");
						char lastChar = s.charAt(s.length() - 1); // Get the last character
						int lastCharAsInt = Integer.parseInt(String.valueOf(lastChar));
						if(s.equals(""))
							s="No results found";
						Label l=new Label("Group: Intermediate\n"+"Number of records found: "+lastCharAsInt+"\n"+s.substring(0,s.length()-2));
	            		sc.setContent(l);
					}
                	catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
            	}
            	else if(comboBox.getValue().equals("expert")) {
            		try {
						String s=helparticle.filterbybeginner("expert");
						char lastChar = s.charAt(s.length() - 1); // Get the last character
						int lastCharAsInt = Integer.parseInt(String.valueOf(lastChar));
						if(s.equals(""))
							s="No results found";
						Label l=new Label("Group: Expert\n"+"Number of records found: "+lastCharAsInt+"\n"+s.substring(0,s.length()-2));
	            		sc.setContent(l);
					}
                	catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
            	}
            }
        });
	}
	
	/**
     * This function prints out all the articles in the database
     */
	public void listall(VBox v) throws Exception {
		String s=helparticle.listArticles();
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setPrefHeight(250); 
		Label l=new Label(s);
		scrollPane.setContent(l);
		scrollPane.setPannable(true);
		v.getChildren().add(scrollPane);
	}
	
	/**
     * This function helps print all the messages given by students
     */
	private void viewall(Stage primaryStage) throws Exception {
		helparticle.connectToDatabase();
		v = new VBox(10);
		
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setPrefHeight(250); 
        Label l = null;
		try {
			l = new Label(helparticle.viewallmessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        scrollPane.setContent(l);
        scrollPane.setPannable(true);
        v.getChildren().add(scrollPane);
        Scene scene = new Scene(v, 400, 250);
        primaryStage.setScene(scene);
	}
	
	/**
     * This function helps student draft and send the message to instructor
     */
	private void sendmessage(Stage primary) throws SQLException {
		helparticle.connectToDatabase();
		v = new VBox(10);
        v.setAlignment(Pos.CENTER);
        Label ln=new Label("Choose the type of message:");
        Label l2=new Label();
        RadioButton option1 = new RadioButton("Send generic message");
        RadioButton option2 = new RadioButton("Send specific message");
        ToggleGroup group = new ToggleGroup();
        option1.setToggleGroup(group);
        option2.setToggleGroup(group);
    	Button b=new Button("Submit");
    	TextField t1=new TextField();
    	v.getChildren().addAll(ln,option1,option2,t1,b,l2);
    	b.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	int c=0;
            	if (group.getSelectedToggle() != null) {
                    RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
                     
                     if(selectedRadioButton.getText().equalsIgnoreCase("Send generic message")) { c=1;}
                     else
                    	 c=2;
                     
                } else {
                     System.out.println("No option selected.");
                }
            	
            		try {
						helparticle.savemessage(c,t1.getText());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
            }
        });
	}
	
	
}
    
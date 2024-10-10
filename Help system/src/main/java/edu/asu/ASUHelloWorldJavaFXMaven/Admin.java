package edu.asu.ASUHelloWorldJavaFXMaven;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Random;

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
		Label l11=new Label("Admin");
		CheckBox c1=new CheckBox();
		Label l2=new Label("Instructor");
		Label l3=new Label("Student");
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
		v.getChildren().add(l11);
		v.getChildren().add(c1);
		v.getChildren().add(l2);
		v.getChildren().add(c2);
		v.getChildren().add(l3);
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
	
	public void inviteuser() {
		VBox v=new VBox();
		root=new BorderPane();
		root.getChildren().clear();
		start(new Stage());
		Label l=new Label("Choose role for the new user");
		Label l1=new Label("Admin");
		CheckBox c1=new CheckBox();
		Label l2=new Label("Instructor");
		CheckBox c2=new CheckBox();
		Label l3=new Label("Student");
		CheckBox c3=new CheckBox();
		Button b=new Button("Generate OTP");
		Label l4=new Label("");
		Label l5=new Label("");
		v.getChildren().add(l);
		v.getChildren().add(l1);
		v.getChildren().add(c1);
		v.getChildren().add(l2);
		v.getChildren().add(c2);
		v.getChildren().add(l3);
		v.getChildren().add(c3);
		v.getChildren().add(b);
		v.getChildren().add(l4);
		v.getChildren().add(l5);
		root.setCenter(v);
		b.setOnAction(new EventHandler<>() { 
            public void handle(ActionEvent event) {
      
            	System.out.println("HERE");
            	char[] a=generateRandomCharArray();
            	String s="";
            	for (char c : a) {
                    s+=c;
                }
            	l4.setText(s);
            	l5.setText(getExpirationDate());
            	System.out.println(s);
            	String newrole="";
            	newrole+= c1.isSelected()?"1":"0";
            	newrole+= c2.isSelected()?"1":"0";
            	newrole+= c3.isSelected()?"1":"0";
            	try {
					helper.inviteroles(newrole,s,getExpirationDate());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
		
		
	}
	public void resetuser() {
		VBox v=new VBox();
		root=new BorderPane();
		root.getChildren().clear();
		start(new Stage());
		Label l=new Label("Enter the username to reset");
		TextField tf=new TextField();
		Label l5=new Label("");
		Label lf=new Label("");
		Button submit =new Button("Reset");
		v.getChildren().add(l);
		v.getChildren().add(tf);
		v.getChildren().add(submit);
		v.getChildren().add(lf);
		v.getChildren().add(l5);
		root.setCenter(v);
		submit.setOnAction(new EventHandler<>() { 
            public void handle(ActionEvent event) {
            	try {
            		String s="";
            		l5.setText(getExpirationDate());
            		char[] a=generateRandomCharArray();
            		for (char c : a) {
                        s+=c;
                    }
            		System.out.println(s);
            		lf.setText(s);
					helper.resetuser(tf.getText(),s,getExpirationDate());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
	}
	public static String getExpirationDate() {
        Calendar expirationDate = Calendar.getInstance();                 //get expiration to the current date
        expirationDate.add(Calendar.DATE, 30);                             //add 30 days to the expiration date
        int Year = expirationDate.get(Calendar.YEAR);
        int Month = expirationDate.get(Calendar.MONTH)+1;
        int Date = expirationDate.get(Calendar.DATE);
        String dateStr = "%d-%d-%d";
        String formattedDate = String.format(dateStr, Year, Month, Date);
        return formattedDate;
    }
	
	public void permpassword(String username) {
		VBox v=new VBox();
		root=new BorderPane();
		root.getChildren().clear();
		start(new Stage());
		Label l=new Label("Enter the permanent password for the account.");
		TextField tf=new TextField();
		Label l2=new Label("Confirm password");
		TextField tf2=new TextField();
		Button submit = new Button("Submit");
		Label l3=new Label("");
		v.getChildren().add(l);
		v.getChildren().add(tf);
		v.getChildren().add(l2);
		v.getChildren().add(tf2);
		v.getChildren().add(submit);
		v.getChildren().add(l3);
		root.setCenter(v);
		
		
		submit.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	if(tf.getText().equals(tf2.getText())) {
            	try {
            		
					helper.updatepass(tf.getText(),username);
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}}
            	else
            		l3.setText("Passwords are not the same");
            		
            }
        });
	}
	public static char[] generateRandomCharArray() {
        Random random = new Random();
        char[] randomChars = new char[10];
        
        for (int i = 0; i < randomChars.length; i++) {
            randomChars[i] = (char) (random.nextInt(26) + 'a'); // generates a random lowercase letter
        }
        
        return randomChars;}
	
	public static void main(String[] args) {
		launch(args);}
}
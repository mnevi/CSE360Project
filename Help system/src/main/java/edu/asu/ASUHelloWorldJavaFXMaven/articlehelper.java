package edu.asu.ASUHelloWorldJavaFXMaven;

import java.sql.SQLException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*******
 * <p> articlehelper Class </p>
 * 
 * <p> Description: This class handles operations with respect to articles
 * It does everything ranging from adding things to database to storing them in a backup file </p>
 * 
 * 
 * @author Tushar Sachan, Max Neville, Taj Yoshimura, Alan Lintemuth, William McLean
 * 
 * @version 1.0		Development Phase 1 (Article management)
 * 
 */

public class articlehelper extends Application {
		
    VBox v;
    public int work;
    ArticleDatabaseHelper helparticle=new ArticleDatabaseHelper();
    String q;
    
    /**********
	 * This is the start method that is called once the application has been and it sets up the stage
	 * to view all GUI components
	 */
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox();
        helparticle.ArticleDatabase();
        root.setAlignment(Pos.CENTER);
        //Decides what operation is going to be performed on article
        switch(work) {
        case 1:
        	addarticle(primaryStage);
        	break;
        case 2:
        	remove(primaryStage);
        	break;
        case 3:
        	list(primaryStage);
        	break;
        case 4:
        	updatetemp(primaryStage);
        	break;
        case 5:
        	searching(primaryStage);
        	break;
        case 6:
        	backup(primaryStage);
        	break;
        case 7:
        	restore(primaryStage);
        	break;
        default:
        	break;
        }

        //gets and adds the root to stage
        root.getChildren().add(v); // Add VBox with buttons to the root VBox
        StackPane stackPane = new StackPane(root); // StackPane to center root VBox in the scene
        Scene scene = new Scene(stackPane, 400, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Articles");
        primaryStage.show();
    }
    
    /**********
	 * This function helps by prompting for article details and adding them to 
	 * the database by database helper object
	 */
    public void addarticle(Stage primaryStage) {
        v = new VBox(10); // Add spacing between buttons
        v.setAlignment(Pos.CENTER); // Center-align buttons in the VBox
        Label ln=new Label("Enter details for the article:");
    	Label l1=new Label("Enter the title of the article:");
    	TextField t1=new TextField();
    	Label l2=new Label("Enter Author(s) for the article:");
    	TextField t2=new TextField();
    	Label l21=new Label("Enter Abstract: ");
    	TextField t21=new TextField();
    	Label l3=new Label("Enter set of keywords: ");
    	TextField t3=new TextField();
    	Label l4=new Label("Enter body of the article: ");
    	TextField t4=new TextField();
    	Label l5=new Label("Enter set of references: ");
    	TextField t5=new TextField();
    	Label l6=new Label("Enter the level (beginner, intermediate, advanced, expert): ");
    	TextField t6=new TextField();
    	Label l7=new Label("Enter the groups for the article (separated by a comma): ");
    	TextField t7=new TextField();
    	Button btn = new Button("Submit");
    	v.getChildren().addAll(ln, l1, t1, l2, t2, l21, t21, l3, t3, l4, t4, l5, t5, l6, t6, l7, t7, btn);
    	
    	//calls the back end database to add article to memory
    	btn.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	try {
					helparticle.addArticle(t1.getText(),t2.getText(),t21.getText(),t3.getText(),t4.getText(), t5.getText(),t6.getText(),t7.getText());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	System.out.println("Article added successfully");
            	primaryStage.close();
            }
        });
        
    }
    
    /**********
	 * This function helps remove an article from the database by prompting for ID
	 */
    public void remove(Stage primary) throws SQLException {
    	v = new VBox(10); // Add spacing between buttons
        v.setAlignment(Pos.CENTER); // Center-align buttons in the VBox
        Label l1=new Label("Enter the ID of the article to be deleted:");
    	TextField t1=new TextField();
    	Button btn = new Button("Submit");
    	v.getChildren().addAll( l1, t1, btn);
    	
    	
    	//Communicates with the backend and removes article from memory
    	btn.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	try {
					helparticle.deleteArticle(Long.parseLong(t1.getText()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	primary.close();
            }
        });
    }
    
    /**********
	 * This function lists all articles in memory of primary database 
	 */
    public void list(Stage primary) throws Exception {
    	v = new VBox(10); // Add spacing between buttons
        v.setAlignment(Pos.CENTER); // Center-align buttons in the VBox
        String s=helparticle.listArticles();
        Label label = new Label(s);
        ScrollPane scrollPane = new ScrollPane(label);
	    scrollPane.setPrefSize(300, 450);  
	    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);  
	    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	    v.getChildren().add(scrollPane);
    }
    
    /**********
	 * This function helps update the article
	 */
    public void update(Stage primary,String s,VBox v,long id) throws Exception {
        v.getChildren().clear();
        Label ln=new Label("Update details for the article:");
    	Label l1=new Label("Update the title of the article:");
    	TextField t1=new TextField();
    	Label l2=new Label("Update Author(s) for the article:");
    	TextField t2=new TextField();
    	Label l21=new Label("Update Abstract: ");
    	TextField t21=new TextField();
    	Label l3=new Label("Update set of keywords: ");
    	TextField t3=new TextField();
    	Label l4=new Label("Update body of the article: ");
    	TextField t4=new TextField();
    	Label l5=new Label("Update set of references: ");
    	TextField t5=new TextField();
    	Label l6=new Label("Update the level (beginner, intermediate, advanced, expert): ");
    	TextField t6=new TextField();
    	Label l7=new Label("Update the groups for the article (separated by a comma): ");
    	TextField t7=new TextField();
    	Button btn = new Button("Submit");
    	
    	v.getChildren().addAll(ln, l1, t1, l2, t2, l21, t21, l3, t3, l4, t4, l5, t5, l6, t6, l7, t7, btn);
    	System.out.println(s);
        String[] parts = s.split("#");
        
        t1.setText(parts[0]);
        t21.setText(parts[1]);
        t2.setText(parts[2]);
        t3.setText(parts[3]);
        t4.setText(parts[4]);
        t5.setText(parts[5]);
        t6.setText(parts[6]);
        t7.setText(parts[7]);
        btn.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	try {
					helparticle.addArticle(id,t1.getText(),t21.getText(),t2.getText(),t3.getText(),t4.getText(),t5.getText(),t6.getText(),t7.getText());
					System.out.println("Update Successful");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            }
        });
    }
    
    
     /**********
   	 * This function helps update the articles by prompoting for an ID
   	 */
       
       public void updatetemp(Stage primary) throws Exception {
       	
       	v = new VBox(10); // Add spacing between buttons
           v.setAlignment(Pos.CENTER); // Center-align buttons in the VBox
           Label l1=new Label("Enter the ID of article to update");
       	TextField t1=new TextField();
       	Button b = new Button ("Submit");
       	v.getChildren().addAll( l1, t1, b);
       	
       	//Applies necessary changes in the backend database
       	b.setOnAction(new EventHandler<>() {
               public void handle(ActionEvent event) {
               	try {
   					String s=helparticle.getdata(Long.parseLong(t1.getText()));
   					
   					update(primary,s,v,Long.parseLong(t1.getText()));
   				} catch (Exception e) {
   					// TODO Auto-generated catch block
   					e.printStackTrace();
   				}
               	
               }
           });
       }
       
         /**********
      	 * This function prompts to select if user wants to back up
      	 * everything or just backup by groups
      	 */
       
       public void backup(Stage primary) {
    	v = new VBox(10); // Add spacing between buttons
        v.setAlignment(Pos.CENTER); // Center-align buttons in the VBox
        Button b1=new Button("Backup Everything");
        Button b2=new Button("Backup by groups");
        v.getChildren().addAll( b1,b2);
        
        //button backs everything up
        b1.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	backupall(primary,v);
            	
            }
        });
        
        //button backs up everything filtered by groups
        b2.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	backupgroups(primary,v);
            	
            }
        });
    	
       }
       
         /**********
     	 * This function creates a backup of the current database
     	 */
       public void backupall(Stage primary,VBox v) {
    	v.getChildren().clear();
    	Label l1=new Label("Enter the name of the file:");
    	TextField t1=new TextField();
    	Button b = new Button ("Submit");
    	v.getChildren().addAll( l1,t1,b);
    	b.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	helparticle.backupArticles(t1.getText());
            	primary.close();
            }
        });
       }
       
       /**********
     	 * This function creates a backup filtered by groups 
     	 * in the file of user's choice
     	 */
       public void backupgroups(Stage primary,VBox v) {
    	v.getChildren().clear();
    	Label l1=new Label("Enter the name of the file:");
    	TextField t1=new TextField();
    	Label l2=new Label("Enter the groups to be back up (seperated by comma):");
    	TextField t2=new TextField();
    	Button b = new Button ("Submit");
    	v.getChildren().addAll( l1,t1,l2,t2,b);
    	b.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	helparticle.backupGroupedArticles(t1.getText(),t2.getText());
            	primary.close();
            }
        });
       }
       
       /**********
     	 * This function creates a GUI to ask if user wants to clear and restore
     	 * or just merge the current database with a backup
     	 */
       public void restore(Stage primary) {
    	v = new VBox(10); // Add spacing between buttons
        v.setAlignment(Pos.CENTER); // Center-align buttons in the VBox
        Button b1=new Button("Clear and restore");
        Button b2=new Button("Merge and restore");
        v.getChildren().addAll( b1,b2);
        //clear and restore
        b1.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	restoreall(primary,v);
            	
            }
        });
        //merge and restore
        b2.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	restoremerge(primary,v);
            	
            }
        });
       }
       
       
       	/**********
     	 * makes necessary database calls to clear the database and restore everything 
     	 * from a backup file
     	 */
       public void restoreall(Stage primary,VBox v) {
    	v.getChildren().clear();
    	Label l1=new Label("Enter the name of the file:");
    	TextField t1=new TextField();
    	Button b = new Button ("Submit");
    	v.getChildren().addAll( l1,t1,b);
    	b.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	try {
					helparticle.deleteRestoreArticles(t1.getText());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	primary.close();
            }
        });
       }
       
       
         /**********
    	 * checks for same id and merges from backup to avoid any duplicates in the file
    	 */
       public void restoremerge(Stage primary,VBox v) {
    	v.getChildren().clear();
    	Label l1=new Label("Enter the name of the file:");
    	TextField t1=new TextField();
    	Button b = new Button ("Submit");
    	v.getChildren().addAll( l1,t1,b);
    	b.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	try {
					helparticle.mergeArticles(t1.getText());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	primary.close();
            }
        });
       }
       
       /**********
   	   * prompts two options to search by ID or group
   	   */
       public void searching(Stage primary) {
    	v = new VBox(10); // Add spacing between buttons
        v.setAlignment(Pos.CENTER); // Center-align buttons in the VBox
        Button b1=new Button("Search article by ID");
        Button b2=new Button("Search article by groups");
        v.getChildren().addAll( b1,b2);
        b1.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	try {
					searchbyid(primary,v);
				} catch (NumberFormatException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            }
        });
        b2.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	searchbygroups(primary,v);
            	
            }
        });
       }
       
       /**********
   	   * searches an article by ID and print the article details
   	   */
   		public void searchbyid(Stage primary,VBox v) throws NumberFormatException, SQLException {
    	v.getChildren().clear();
    	Label l1=new Label("Enter the ID to view article:");
    	TextField t1=new TextField();
    	Button b=new Button("Submit");
    	v.getChildren().addAll(l1,t1,b);
    	b.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	String s="";
				try {
					s = helparticle.viewArticle(Long.parseLong(t1.getText()));
				} catch (NumberFormatException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	printsearch(primary,v,s);
            }
        });
   		}
   		
   		/**********
    	 * just a helper function to help print the required article
    	 * in form a scroll pane
    	 */
   		public void printsearch(Stage primary, VBox v,String s) {
    	v.getChildren().clear();
    	Label l2=new Label(s);
    	ScrollPane scrollPane = new ScrollPane(l2);
	    scrollPane.setPrefSize(300, 250);
	    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);  
	    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	    v.getChildren().add(scrollPane);
   		}
   		
   		/**********
    	 * this enables searching by groups and prints every article that is 
    	 * linked to the user
    	 */
   		public void searchbygroups(Stage primary,VBox v) {
    	v.getChildren().clear();
    	Label l1=new Label("Enter the groups (seperated by commas )");
    	TextField t1=new TextField();
    	Button b=new Button("Submit");
    	v.getChildren().addAll(l1,t1,b);
    	b.setOnAction(new EventHandler<>() {
            public void handle(ActionEvent event) {
            	String s="";
				
					try {
						s = helparticle.listGroupedArticles(t1.getText());
						printgroups(primary,v,s);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
            	printsearch(primary,v,s);
            }
        });
   		}
    
   		/**********
    	 * Helper function to print the groups in form of a scrollpane
    	 */
   		public void printgroups(Stage primary, VBox v,String s) {
    	v.getChildren().clear();
    	Label l2=new Label(s);
    	ScrollPane scrollPane = new ScrollPane(l2);
	    scrollPane.setPrefSize(300, 450);
	    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);  
	    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
	    v.getChildren().add(scrollPane);
   		}
    

    public static void main(String[] args) {
        launch(args);
    }
}

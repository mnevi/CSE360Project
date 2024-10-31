package edu.asu.ASUHelloWorldJavaFXMaven;

import java.util.Random;
import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

/*******
* <p> ArticleDatabaseHelper Class. </p>
*
* <p> Description: A Java Class for dealing with articles saved on a database </p>
*
* @author Taj Yoshimura
*
* @version 1.00 2024-10-20 Connects to a database and can add/remove/list/back-up/restore articles
* @version 2.00 2024-10-30 Adjusted to work for Phase 2 with groups/levels/new ID format
*/

class ArticleDatabaseHelper {
	
    // JDBC driver name and database URL 
    static final String JDBC_DRIVER = "org.h2.Driver";   
    static final String DB_URL = "jdbc:h2:~/articlesDatabase";  
    
    // Database credentials 
    static final String USER = "sa"; 
    static final String PASS = ""; 
    
    private Scanner scanner;
    private Connection connection = null;
    private Statement statement = null; 
    //	PreparedStatement pstmt
    
    /** Default constructor for the class 
     * @throws SQLException */
    public void ArticleDatabase() throws SQLException {
    	connectToDatabase();
    	scanner = new Scanner(System.in);
    }
    
    /**
     * This function connects to the database
     * @throws SQLException
     */
    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER); // Load the JDBC driver
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement(); 
            createTables();  // Create the necessary tables if they don't exist
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    /**
     * This function creates the tables of articles
     * @throws SQLException
     */
    private void createTables() throws SQLException {
    	
        String articleTable = "CREATE TABLE IF NOT EXISTS articles ("
                + "id BIGINT PRIMARY KEY, "
                + "title VARCHAR(255), "
                + "authors VARCHAR(255), "
                + "abstract TEXT, "
                + "keywords VARCHAR(255), "
                + "body TEXT, "
                + "references TEXT, "
                + "level VARCHAR(255), "
                + "groups TEXT)";
        statement.execute(articleTable);
    }
    
    /**********
	 * Generate a long unique ID to avoid duplicate articles
	 */
    private long generateUniqueId() throws SQLException {
        Random random = new Random();
        long randomId;

        do {
            randomId = Math.abs(random.nextLong()); // Generate a positive random long
        } while (idExists(randomId)); // Check for uniqueness

        return randomId;
    }
    
    /**********
	 * checks if the article already exists
	 */
    private boolean idExists(long id) throws SQLException {
        String checkId = "SELECT COUNT(*) FROM articles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(checkId)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0; // return true if the ID exists
        }
    }


    /**
     * This function inserts a new into the table and encrypts the body 
     * @param title
     * @param authors
     * @param abstractText
     * @param keywords
     * @param body
     * @param references
     * @throws Exception
     */
    public void addArticle(String title, String authors, String abstractText, 
                           String keywords, String body, String references, String level, String groups) throws Exception {
    	String encryptedBody = Base64.getEncoder().encodeToString(body.getBytes());
    	
    	long uniqueId = generateUniqueId(); // Generate a unique ID
    	
    	System.out.println("Generated Unique ID: " + uniqueId); // Debugging output
    	
        String insertArticle = "INSERT INTO articles (id, title, authors, abstract, keywords, body, references, level, groups) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
        	pstmt.setLong(1, uniqueId); // Set the unique ID
            pstmt.setString(2, title);
            pstmt.setString(3, authors);
            pstmt.setString(4, abstractText);
            pstmt.setString(5, keywords);
            pstmt.setString(6, encryptedBody); 	//send encrypted body
            pstmt.setString(7, references);
            pstmt.setString(8, level);
            pstmt.setString(9, groups);
            pstmt.executeUpdate();
            System.out.println("Article added successfully.");
        }
    }
    
    /**
     * This function is for inserting restored articles into the table, where the body is already encrypted
     * @param title
     * @param authors
     * @param abstractText
     * @param keywords
     * @param body
     * @param references
     * @throws Exception
     */
    public void addRestoredArticle(Long id, String title, String authors, String abstractText, 
            String keywords, String body, String references, String level, String groups) throws Exception {
    	String insertArticle = "INSERT INTO articles (id, title, authors, abstract, keywords, body, references, level, groups) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
        	pstmt.setLong(1, id);
            pstmt.setString(2, title);
            pstmt.setString(3, authors);
            pstmt.setString(4, abstractText);
            pstmt.setString(5, keywords);
            pstmt.setString(6, body);			//body already encrypted so send body
            pstmt.setString(7, references);
            pstmt.setString(8, level);
            pstmt.setString(9, groups);
            pstmt.executeUpdate();
            System.out.println("Article added successfully.");
        }
    }
    
    
    public void addArticle(Long id, String title, String authors, String abstractText, 
            String keywords, String body, String references, String level, String groups) throws Exception {
    	String encryptedBody = Base64.getEncoder().encodeToString(body.getBytes());
    	String insertArticle = "UPDATE articles SET title = ?, authors = ?, abstract = ?, "
                + "keywords = ?, body = ?, references = ?, level = ?, groups = ? "
                + "WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
            pstmt.setString(1, title);
            pstmt.setString(2, authors);
            pstmt.setString(3, abstractText);
            pstmt.setString(4, keywords);
            pstmt.setString(5, encryptedBody);			//body already encrypted so send body
            pstmt.setString(6, references);
            pstmt.setString(7, level);
            pstmt.setString(8, groups);
            pstmt.setLong(9, id);
            pstmt.executeUpdate();
            System.out.println("Article added successfully.");
        }
    }
    
    /**
     * This function deletes an article saved in the database
     * @throws SQLException
     */
    public void deleteArticle(long id) throws SQLException {
        

        String deleteArticle = "DELETE FROM articles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteArticle)) {
            pstmt.setLong(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Article deleted successfully.");
            } else {
                System.out.println("No article found with title: " + id);
            }
        }
    }
    
    /**
     * This function lists each article saved in the database
     * @throws Exception
     */
    public String listArticles() throws Exception {
    	String s="";
        String sql = "SELECT * FROM articles"; 
        ResultSet rs = statement.executeQuery(sql); 

        while (rs.next()) { 
            long id = rs.getLong("id"); 
            String title = rs.getString("title"); 
            String authors = rs.getString("authors");  
            String abstractText = rs.getString("abstract");
            String keywords = rs.getString("keywords");
            String encryptedBody = rs.getString("body");
            byte[] bodyByte= Base64.getDecoder().decode(encryptedBody); 
            String bodyString= new String(bodyByte); 
            String references = rs.getString("references");
            String level = rs.getString("level");
            String groups = rs.getString("groups");

           System.out.println(id);
            s+= 	"--------------------------------------"+"\n"+
            		"ID: "+ id+"\n"+
            		"Title: " + title+"\n"+
            		"Authors: " + authors+"\n"+
            		"Abstract: " + abstractText+"\n"+
            		"Keywords: " + keywords+"\n"+
            		"Body: "+ bodyString +"\n"+
            		"Level: " + level+"\n"+
            		"groups: " + groups+"\n\n";
        }
		return s;
    }
    
    /**********
	 * This function returns all the data of an article depending on the ID
	 */
    public String getdata(long id) throws Exception {
    	String s="";
        String sql = "SELECT * FROM articles WHERE id = ?"; 
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setLong(1, id); // Set the title parameter

        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) { 
		            String title = rs.getString("title"); 
		            String authors = rs.getString("authors");  
		            String abstractText = rs.getString("abstract");
		            String keywords = rs.getString("keywords");
		            String encryptedBody = rs.getString("body");
		            byte[] bodyByte= Base64.getDecoder().decode(encryptedBody); 
		            String bodyString= new String(bodyByte); 
		            String references = rs.getString("references");
		            String level = rs.getString("level");
		            String groups = rs.getString("groups");

		           
		            s+= title+"#"+authors+"#"+abstractText+"#"+keywords+"#"+bodyString+"#"+references+"#"+level+"#"+groups;
			}
		return s;
    }
    
    /**
     * This function lists each article saved in the database by group
     * @throws Exception
     */
    public String listGroupedArticles(String backupGroups) throws Exception {
    	String s="";
        						//collect user input for groups
        String[] groupList = backupGroups.split("\\s*,\\s*"); 				//create an array of each group listed by user
    	
        String sql = "SELECT * FROM articles"; 
        ResultSet rs = statement.executeQuery(sql); 

        while (rs.next()) {

        	boolean inGroup = false;
        	String articleGroups = rs.getString("groups");
        	String[] articleGroupList = articleGroups.split("\\s*,\\s*"); 	//create an array of current articles groups
        	
        	for (String element1 : groupList) {								//check if any groups match in the articles
                for (String element2 : articleGroupList) {
                    if (element1.equals(element2)) {
                        inGroup = true;
                        break;
                    }
                }
                if (inGroup) {
                    break;
                }
            }
        	
        	if(inGroup == true) {					//add article if any groups match
        		long id = rs.getLong("id"); 
                String title = rs.getString("title"); 
                String authors = rs.getString("authors");  
                String abstractText = rs.getString("abstract");
                String keywords = rs.getString("keywords");
                String encryptedBody = rs.getString("body");
                byte[] bodyByte= Base64.getDecoder().decode(encryptedBody); 
                String bodyString= new String(bodyByte); 
                String references = rs.getString("references");
                String level = rs.getString("level");
                String groups = rs.getString("groups");

                s+="------------------------\n"+
                "ID: " + id+"\n"+
                "Title: " + title+"\n"+
                "Authors: " + authors+"\n"+
                "Abstract: " + abstractText+"\n"+
                "Keywords: " + keywords+"\n"+
                "Body: "+ bodyString +"\n"+
                "References: " + references+"\n"+
                "Level: " + level+"\n"+
                "groups: " + groups+"\n\n";
        	}
        }
        return s;
    }
    
    /**
     * This function shows an article based on an ID given by the user
     * @param idToView
     * @throws SQLException
     */
    public String viewArticle(long idToView) throws SQLException{
        String s="";
        String sql = "SELECT * FROM articles WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setLong(1, idToView); // Set the title parameter

        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) {
            String title = rs.getString("title");
            String authors = rs.getString("authors");
            String abstractText = rs.getString("abstract");
            String keywords = rs.getString("keywords");
            String encryptedBody = rs.getString("body");
            byte[] bodyByte = Base64.getDecoder().decode(encryptedBody);
            String bodyString = new String(bodyByte);
            String references = rs.getString("references");
            String level = rs.getString("level");
            String groups = rs.getString("groups");

            s+="ID: " + idToView+"\n"+
            "Title: " + title+"\n"+
            "Authors: " + authors+"\n"+
            "Abstract: " + abstractText+"\n"+
            "Keywords: " + keywords+"\n"+
            "Body: " + bodyString+"\n"+
            "References: " + references+"\n"+
            "Level: " + level+"\n"+
            "Groups: " + groups+"\n\n";
        }
        rs.close();
        
        preparedStatement.close();
        return s;
    }

  
    
    /**
     * This function backs up the saved articles to a file of the user's choosing by group
     */
    public void backupGroupedArticles(String filename, String backupGroups) {
    	filename=filename+".txt";
        String[] groupList = backupGroups.split("\\s*,\\s*");//create array from given groups

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            String sql = "SELECT * FROM articles";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
            
            boolean inGroup = false;
            String articleGroups = rs.getString("groups");
            String[] articleGroupList = articleGroups.split("\\s*,\\s*");//create array from current articles groups
            
            for (String element1 : groupList) {//check if any groups match
                    for (String element2 : articleGroupList) {
                        if (element1.equals(element2)) {
                            inGroup = true;
                            break;
                        }
                    }
                    if (inGroup) {
                        break;
                    }
                }
            
            if(inGroup == true) {//add article if any groups match
                writer.println(rs.getLong("id") + "~" + // get id
                   rs.getString("title") + "~" +
                               rs.getString("authors") + "~" +
                               rs.getString("abstract") + "~" +
                               rs.getString("keywords") + "~" +
                               rs.getString("body") + "~" + // the encrypted body
                               rs.getString("references") + "~" +
                               rs.getString("level") + "~" + 
                               rs.getString("groups"));
            }
            }
            System.out.println("Backup completed successfully.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to back up articles.");
        }
    }

    /**
     * This function merges articles from a file specified by the user with the existing articles in the database 
     * @param filename
     * @throws Exception
     */
    public void mergeArticles(String filename) throws Exception {
    	filename=filename+".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("~", -1); // -1 to include trailing empty strings
                if (parts.length == 9) {
                    long id = Long.parseLong(parts[0]);

                    if (idExists(id)) {
                        System.out.println("Article with ID " + id + " already exists. Skipping...");
                        continue; // skip this article if ID exists
                    }

                    // Insert the article
                    addRestoredArticle(id, parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], parts[8]);
                }
            }
            System.out.println("Merge completed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to merge articles.");
        }
    }
    
    /**
     * This function deletes all the articles in the database and restores the articles from a file specified by the user
     * @param filename
     * @throws Exception
     */
    public void deleteRestoreArticles(String filename) throws Exception {
    	filename=filename+".txt";
        //reset articles
        try {
            String deleteAll = "DELETE FROM articles";
            statement.executeUpdate(deleteAll);
            System.out.println("Existing articles deleted. Restoring from backup...");

            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("~", -1); // -1 to include trailing empty strings
                    if (parts.length == 9) {
                    long id = Long.parseLong(parts[0]);

                        if (idExists(id)) {
                            System.out.println("Article with ID " + id + " already exists. Skipping...");
                            continue; // skip this article if ID exists
                        }
                        // insert the article 
                    addRestoredArticle(id, parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], parts[8]);
                    }
                }
                System.out.println("Restore completed successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to restore articles.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to delete existing articles.");
        }
    }
    /**
    
    This function backs up the saved articles to a file of the user's choosing*/
      public void backupArticles(String filename) {
    	  	filename=filename+".txt";
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                String sql = "SELECT * FROM articles";
                ResultSet rs = statement.executeQuery(sql);
                while (rs.next()) {
                    writer.println(rs.getLong("id") + "~" + // get id
                                   rs.getString("title") + "~" +
                                   rs.getString("authors") + "~" +
                                   rs.getString("abstract") + "~" +
                                   rs.getString("keywords") + "~" +
                                   rs.getString("body") + "~" + // the encrypted body
                                   rs.getString("references") + "~" +
                                   rs.getString("level") + "~" + 
                                   rs.getString("groups"));
                }
                System.out.println("Backup completed successfully.");
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                System.err.println("Failed to back up articles.");
            }
        }



    /**
     * Closes the connection with the database
     */
    public void closeConnection() {
        try { 
            if (statement != null) statement.close(); 
        } catch (SQLException se2) { 
            se2.printStackTrace();
        } 
        try { 
            if (connection != null) connection.close(); 
        } catch (SQLException se) { 
            se.printStackTrace(); 
        } 
        scanner.close(); // dont forget to close out scanner
    }
}


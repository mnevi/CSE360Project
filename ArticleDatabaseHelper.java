package simpleDatabase;

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
    
    /** Default constructor for the class */
    public ArticleDatabaseHelper() throws Exception {
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
    	
 		/**
 		 * reset articles table
 		 * 								
    	String sql1 = "DROP TABLE IF EXISTS articles";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql1);
            System.out.println("Table 'articles' dropped successfully (if it existed).");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to drop table 'articles'.");
        }
        **/
    	
		/**
		 * reset groups table
		 * 								
    	String sql2 = "DROP TABLE IF EXISTS groups";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql2);
            System.out.println("Table 'groups' dropped successfully (if it existed).");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to drop table 'groups'.");
        }
        **/

    	
        String articleTable = "CREATE TABLE IF NOT EXISTS articles ("
                + "id BIGINT PRIMARY KEY, "
                + "title VARCHAR(255), "
                + "authors VARCHAR(255), "
                + "abstract TEXT, "
                + "keywords VARCHAR(255), "
                + "body TEXT, "
                + "references TEXT, "
                + "level VARCHAR(255))";
        statement.execute(articleTable);
        
        String groupTable = "CREATE TABLE IF NOT EXISTS groups ("
        		+ "groupID BIGINT PRIMARY KEY, "
        		+ "groupTitle TEXT, "
                + "groupArticles TEXT, "
                + "specialAccess BOOLEAN, "
                + "groupAdmin TEXT, "
                + "groupAccess TEXT) ";
        statement.execute(groupTable);
    }
    
    private long generateUniqueId() throws SQLException {
        Random random = new Random();
        long randomId;

        do {
            randomId = Math.abs(random.nextLong()); // Generate a positive random long
        } while (idExists(randomId)); // Check for uniqueness

        return randomId;
    }

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
     * This function helps create new articles
     * * @throws Exception
     */
    public void addArticle() throws Exception {
        System.out.print("Enter the article title: ");
        String title = scanner.nextLine();				//collect title from user
        System.out.print("Enter the authors: ");
        String authors = scanner.nextLine();			//collect author from user
        System.out.print("Enter the abstract: ");
        String abstractText = scanner.nextLine();		//collect abstract from user
        System.out.print("Enter the keywords: ");
        String keywords = scanner.nextLine();			//collect keywords from user
        System.out.print("Enter the body: ");
        String body = scanner.nextLine();				//collect body from user
        System.out.print("Enter the references: ");
        String references = scanner.nextLine();			//collect references from user

        System.out.print("Enter the level (beginner, intermediate, advanced, expert): ");
        String level = scanner.nextLine(); // Collect level from the user
        
        //System.out.print("Enter the groups for the article (separated by a comma): ");
        //String groups = scanner.nextLine(); // Collect level from the user

        addArticle(title, authors, abstractText, keywords, body, references, level); //add article to database

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
                           String keywords, String body, String references, String level) throws Exception {
    	String encryptedBody = Base64.getEncoder().encodeToString(body.getBytes());
    	
    	long uniqueId = generateUniqueId(); // Generate a unique ID
    	
    	System.out.println("Generated Unique ID: " + uniqueId); // Debugging output
    	
        String insertArticle = "INSERT INTO articles (id, title, authors, abstract, keywords, body, references, level) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
        	pstmt.setLong(1, uniqueId); // Set the unique ID
            pstmt.setString(2, title);
            pstmt.setString(3, authors);
            pstmt.setString(4, abstractText);
            pstmt.setString(5, keywords);
            pstmt.setString(6, encryptedBody); 	//send encrypted body
            pstmt.setString(7, references);
            pstmt.setString(8, level);
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
            String keywords, String body, String references, String level) throws Exception {
    	String insertArticle = "INSERT INTO articles (id, title, authors, abstract, keywords, body, references, level) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
        	pstmt.setLong(1, id);
            pstmt.setString(2, title);
            pstmt.setString(3, authors);
            pstmt.setString(4, abstractText);
            pstmt.setString(5, keywords);
            pstmt.setString(6, body);			//body already encrypted so send body
            pstmt.setString(7, references);
            pstmt.setString(8, level);
            pstmt.executeUpdate();
            System.out.println("Article added successfully.");
        }
    }
    
    /**
     * This function deletes an article saved in the database
     * @throws SQLException
     */
    public void deleteArticle(String titleToDelete) throws SQLException {

        String deleteArticle = "DELETE FROM articles WHERE title = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteArticle)) {
            pstmt.setString(1, titleToDelete);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Article deleted successfully.");
            } else {
                System.out.println("No article found with title: " + titleToDelete);
            }
        }
    }
    
    /**
     * This function shows an article based on an ID given by the user
     * @param idToView
     * @throws SQLException
     */
    public void viewArticle(long idToView) throws SQLException{
    	
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

            System.out.println("ID: " + idToView);
            System.out.println("Title: " + title);
            System.out.println("Authors: " + authors);
            System.out.println("Abstract: " + abstractText);
            System.out.println("Keywords: " + keywords);
            System.out.println("Body: " + bodyString);
            System.out.println("References: " + references);
            System.out.println("Level: " + level);
        }
        rs.close();
        preparedStatement.close();
    }
    
    /**
     * This function lists each article saved in the database
     * @throws Exception
     */
    public void listArticles() throws Exception {
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

            System.out.println("ID: " + id);
            System.out.println("Title: " + title);
            System.out.println("Authors: " + authors);
            System.out.println("Abstract: " + abstractText);
            System.out.println("Keywords: " + keywords);
            System.out.println("Body: "+ bodyString); 
            System.out.println("References: " + references);
            System.out.println("Level: " + level);
        }
    }
    
    /**
     * This function lists each article saved in the database by group
     * @throws Exception
     */
    /*public void listGroupedArticles() throws Exception {
    	System.out.print("Enter groups to list (separated by a comma): ");
        String backupGroups = scanner.nextLine(); 							//collect user input for groups
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

                System.out.println("ID: " + id);
                System.out.println("Title: " + title);
                System.out.println("Authors: " + authors);
                System.out.println("Abstract: " + abstractText);
                System.out.println("Keywords: " + keywords);
                System.out.println("Body: "+ bodyString); 
                System.out.println("References: " + references);
                System.out.println("Level: " + level);
                System.out.println("groups: " + groups);
        	}
        }
    }*/

    /**
     * This function backs up the saved articles to a file of the user's choosing
     */
    public void backupArticles(String filename) {

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
                               rs.getString("level"));
            }
            System.out.println("Backup completed successfully.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to back up articles.");
        }
    }
    
    /**
     * This function backs up the saved articles to a file of the user's choosing by group
     */
    /**public void backupGroupedArticles(String filename, String backupGroups) {
    	
        String[] groupList = backupGroups.split("\\s*,\\s*");					//create array from given groups

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            String sql = "SELECT * FROM articles";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
            	
            	boolean inGroup = false;
            	String articleGroups = rs.getString("groups");
            	String[] articleGroupList = articleGroups.split("\\s*,\\s*");	//create array from current articles groups
            	
            	for (String element1 : groupList) {								//check if any groups match
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
            	
            	if(inGroup == true) {						//add article if any groups match
                writer.println(rs.getLong("id") + "~" + // get id
                			   rs.getString("title") + "~" +
                               rs.getString("authors") + "~" +
                               rs.getString("abstract") + "~" +
                               rs.getString("keywords") + "~" +
                               rs.getString("body") + "~" + // the encrypted body
                               rs.getString("references") + "~" +
                               rs.getString("level"));
            	}
            }
            System.out.println("Backup completed successfully.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to back up articles.");
        }
    }
    **/

    /**
     * This function merges articles from a file specified by the user with the existing articles in the database 
     * @param filename
     * @throws Exception
     */
    public void mergeArticles(String filename) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("~", -1); // -1 to include trailing empty strings
                if (parts.length == 8) {
                    long id = Long.parseLong(parts[0]);

                    if (idExists(id)) {
                        System.out.println("Article with ID " + id + " already exists. Skipping...");
                        continue; // skip this article if ID exists
                    }

                    // Insert the article
                    addRestoredArticle(id, parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]);
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
        //reset articles
        try {
            String deleteAll = "DELETE FROM articles";
            statement.executeUpdate(deleteAll);
            System.out.println("Existing articles deleted. Restoring from backup...");

            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("~", -1); // -1 to include trailing empty strings
                    if (parts.length == 8) {
                    	long id = Long.parseLong(parts[0]);

                        if (idExists(id)) {
                            System.out.println("Article with ID " + id + " already exists. Skipping...");
                            continue; // skip this article if ID exists
                        }
                        // insert the article 
                    	addRestoredArticle(id, parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]);
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
     * This function restores articles from a file of the user's choosing
     * @throws Exception
     */
    public void restoreArticles() throws Exception {
        System.out.print("Enter the filename to restore from: ");
        String filename = scanner.nextLine();

        System.out.print("Do you want to merge articles (y/n)? ");
        String mergeOption = scanner.nextLine();

        if (!mergeOption.equalsIgnoreCase("y")) {	// if not merging, reset
            try {
                String deleteAll = "DELETE FROM articles";
                statement.executeUpdate(deleteAll);
                System.out.println("Existing articles deleted. Restoring from backup...");
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Failed to delete existing articles.");
                return; // exit if there's an error
            }
        } else {
            System.out.println("Merging articles...");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("~", -1); // -1 to include trailing empty strings
                if (parts.length == 9) {
                    long id = Long.parseLong(parts[0]);
                    
                    if (mergeOption.equalsIgnoreCase("y") && idExists(id)) {
                        System.out.println("Article with ID " + id + " already exists. Skipping...");
                        continue; // skip this article if merging and ID exists
                    }

                    //insert the article 
                    addRestoredArticle(id, parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]);
                }
            }
            System.out.println("Restore completed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to restore articles.");
        }
    }

    public void createGroup(String title, String articles, Boolean specialAccess, String groupAdmin, String groupAccess) throws SQLException {
    	long uniqueId = generateUniqueId(); // Generate a unique ID
    	
    	System.out.println("Generated Unique ID: " + uniqueId); // Debugging output
    	
        String insertGroup = "INSERT INTO groups (groupID, groupTitle, groupArticles, specialAccess, groupAdmin, groupAccess) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertGroup)) {
        	pstmt.setLong(1, uniqueId); // Set the unique ID
            pstmt.setString(2, title);
            pstmt.setString(3, articles);
            pstmt.setBoolean(4, specialAccess);
            pstmt.setString(5, groupAdmin);
            pstmt.setString(6, groupAccess);
            pstmt.executeUpdate();
            System.out.println("Group added successfully.");
        }
    }
    
    public void deleteGroup(String titleToDelete) throws SQLException {

        String deleteGroup = "DELETE FROM groups WHERE groupTitle = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteGroup)) {
            pstmt.setString(1, titleToDelete);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Group deleted successfully.");
            } else {
                System.out.println("No group found with title: " + titleToDelete);
            }
        }
    }
    
    public void listGroups() throws Exception {
        String sql = "SELECT * FROM groups"; 
        ResultSet rs = statement.executeQuery(sql); 

        while (rs.next()) { 
            long id = rs.getLong("groupID"); 
            String title = rs.getString("groupTitle"); 
            String articles = rs.getString("groupArticles");  
            Boolean specialAccess = rs.getBoolean("specialAccess");
            String admin = rs.getString("groupAdmin");
            String access = rs.getString("groupAccess");

            System.out.println("ID: " + id);
            System.out.println("Title: " + title);
            System.out.println("Articles: " + articles);
            System.out.println("Special Access Group: " + specialAccess);
            System.out.println("Admins of Group: " + admin);
            System.out.println("Users With Access: "+ access);
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


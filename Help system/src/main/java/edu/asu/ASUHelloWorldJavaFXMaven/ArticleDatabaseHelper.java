package edu.asu.ASUHelloWorldJavaFXMaven;

import java.util.Random;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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
            
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement(); 
            createTables();
            secondTables();
            thirdTables();
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
    private void thirdTables() throws SQLException {
    	 String groupTable = "CREATE TABLE IF NOT EXISTS groups ("
                 + "groupID BIGINT PRIMARY KEY, "
                 + "groupTitle TEXT, "
                 + "groupArticles TEXT, "
                 + "specialAccess BOOLEAN, "
                 + "groupAdmin TEXT, "
                 + "groupAccess TEXT) ";
         statement.execute(groupTable);
    }

    private void secondTables() throws SQLException {    
        String messageTable = "CREATE TABLE IF NOT EXISTS message ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "type VARCHAR(255), "
                + "body TEXT)";
        statement.execute(messageTable);
    }
    
public void savemessage(int c, String t1) throws Exception {
	

	String insertArticle = "INSERT INTO message (type, body) VALUES (?, ?)";
	try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
		pstmt.setInt(1, c); 
		pstmt.setString(2, t1); 
		pstmt.executeUpdate();
		System.out.println("Message added successfully.");
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

/**

this function checks if a group with a certain title exists
@param group
@return
@throws SQLException*/
public boolean groupExists(String group) throws SQLException {
    boolean exists = false;
    String sql = "SELECT * FROM groups";
    PreparedStatement preparedStatement = connection.prepareStatement(sql); 
    ResultSet rs = preparedStatement.executeQuery();
    while (rs.next()) {
        String title = rs.getString("groupTitle");
        if(group.equals(title)) {
            exists = true;
            break;}}
    return exists;}




public String viewallmessage() throws Exception {
	String sp="";
    String sql = "SELECT * FROM message"; 
    ResultSet rs = statement.executeQuery(sql); 
    String s="";
    while (rs.next()) { 
        int id = rs.getInt("id"); 
        String body = rs.getString("body"); 
        int type = rs.getInt("type");  
        if(type==1) sp="Generic message";
        else sp="Specific message";

       
        s+= 	"--------------------------------------"+"\n"+
        		"ID: "+ id+"\n"+
        		"Type: " + sp+"\n"+
        		"Body of message: " + body+"\n\n";
        
    }
	return s;
}



public boolean isSpecialGroup(String group) throws SQLException {

    String sql = "SELECT * FROM groups WHERE groupTitle = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setString(1, group);
    ResultSet rs = preparedStatement.executeQuery();
    if (rs.next()) {
        return rs.getBoolean("specialAccess");} else {
        return false;}}


/**
 * this funciton adds an article to a group
 * @param group
 * @param article
 * @throws SQLException
 */
public void addArticleToGroup(String group, String article) throws SQLException {
    
    String sql = "SELECT groupArticles FROM groups WHERE groupTitle = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setString(1, group);                                 // Set the title
    
    ResultSet rs = preparedStatement.executeQuery();
    
    if (!rs.next()) {
        throw new SQLException("Group not found: " + group);
    }
    
    String groupArticles = rs.getString("groupArticles");
    
    if (groupArticles == null) {                                        //if it is null or empty, initialize it as an empty string
        groupArticles = "";
    }
    
    String[] articles = groupArticles.split("\\s*,\\s*");
    
    boolean inGroup = Arrays.asList(articles).contains(article);        //check if the article is already in the list
    
    if (!inGroup) {                                                     //if the article is not in the group, add it
        List<String> articleList = new ArrayList<>(Arrays.asList(articles));
        articleList.add(article);
        
        String newGroupArticles = String.join(", ", articleList);
        
        String updateSql = "UPDATE groups SET groupArticles = ? WHERE groupTitle = ?";
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);
        updateStatement.setString(1, newGroupArticles);
        updateStatement.setString(2, group);
        
        updateStatement.executeUpdate();
        System.out.println("Successful");
    } 
    else {
        System.out.println("Article is already in the group.");
    }
}

/**

this function checks if a specific article is in a group
@param group
@param newArticle
@return
@throws SQLException*/
public boolean checkInArticles(String group, String newArticle) throws SQLException {
    String sql = "SELECT groupArticles FROM groups WHERE groupTitle = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setString(1, group); // Set the title parameter

        ResultSet rs = preparedStatement.executeQuery();

        if (!rs.next()) {
            
            return false;                                             //no group found, so the article can't be in it.
        }

        String groupArticles = rs.getString("groupArticles");

        if (groupArticles == null || groupArticles.trim().isEmpty()) {
            
            return false;                                             //no articles in the group
        }

        String[] articles = groupArticles.split("\\s*,\\s*");         //split the articles into an array

        //using a HashSet for fast lookups
        Set<String> articleSet = new HashSet<>(Arrays.asList(articles));

        if (articleSet.contains(newArticle)) {
           
            return true;                                             //article is found
        } else {
            
            return false;                                             //article is not found
        }
    }

/**
 * this function removes and articles from a group
 * @param group
 * @param article
 * @throws SQLException
 */
public void removeArticleFromGroup(String group, String article) throws SQLException {
    
    String sql = "SELECT groupArticles FROM groups WHERE groupTitle = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setString(1, group);                                 // Set the title
    
    ResultSet rs = preparedStatement.executeQuery();
    
    if (!rs.next()) {
        throw new SQLException("Group not found: " + group);
    }
    
    String groupArticles = rs.getString("groupArticles");
    
    if (groupArticles == null) {                                        //if it is null or empty, initialize it as an empty string
        groupArticles = "";
    }
    
    String[] articles = groupArticles.split("\\s*,\\s*");
    
    boolean inGroup = Arrays.asList(articles).contains(article);        //check if the article is already in the list
    
    if (inGroup) {                                                     //if the article is  in the group, remove it
        List<String> articleList = new ArrayList<>(Arrays.asList(articles));
        articleList.remove(article);
        
        String newGroupArticles = String.join(", ", articleList);
        
        String updateSql = "UPDATE groups SET groupArticles = ? WHERE groupTitle = ?";
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);
        updateStatement.setString(1, newGroupArticles);
        updateStatement.setString(2, group);
        
        updateStatement.executeUpdate();
        System.out.println("Removal succesful");
    } 
    else {
        System.out.println("Article is not in the group.");
    }
} 


/**
 * This function gives a user access to a group
 * @param group
 * @param access
 * @throws SQLException
 */
public void addAccessToGroup(String group, String access) throws SQLException {
    
    String sql = "SELECT groupAccess FROM groups WHERE groupTitle = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setString(1, group);                                 // Set the title
    
    ResultSet rs = preparedStatement.executeQuery();
    
    if (!rs.next()) {
        throw new SQLException("Group not found: " + group);
    }
    
    String groupAccess = rs.getString("groupAccess");
    
    if (groupAccess == null) {                                        //if it is null or empty, initialize it as an empty string
        groupAccess = "";
    }
    
    String[] canAccess = groupAccess.split("\\s*,\\s*");
    
    boolean inGroup = Arrays.asList(canAccess).contains(access);        //check if the access list is already in the list
    
    if (!inGroup) {                                                     //if user does not have access, add them
        List<String> accessList = new ArrayList<>(Arrays.asList(canAccess));
        accessList.add(access);
        
        String newGroupAccess = String.join(", ", accessList);
        
        String updateSql = "UPDATE groups SET groupAccess = ? WHERE groupTitle = ?";
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);
        updateStatement.setString(1, newGroupAccess);
        updateStatement.setString(2, group);
        
        updateStatement.executeUpdate();
        System.out.println("User added to the group");
    } 
    else {
        System.out.println("already has access.");
    }
}

/**
 * this function removes a users access from a group
 * @param group
 * @param access
 * @throws SQLException
 */
public void removeAccessFromGroup(String group, String access) throws SQLException {
    
    String sql = "SELECT groupAccess FROM groups WHERE groupTitle = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setString(1, group);                                 // Set the title
    
    ResultSet rs = preparedStatement.executeQuery();
    
    if (!rs.next()) {
        throw new SQLException("Group not found: " + group);
    }
    
    String groupAccess = rs.getString("groupAccess");
    
    if (groupAccess == null) {                                        //if it is null or empty, initialize it as an empty string
        groupAccess = "";
    }
    
    String[] canAccess = groupAccess.split("\\s*,\\s*");
    
    boolean inGroup = Arrays.asList(canAccess).contains(access);        //check if the access list is already in the list
    
    if (inGroup) {                                                     //if user has access, remove them
        List<String> accessList = new ArrayList<>(Arrays.asList(canAccess));
        accessList.remove(access);
        
        String newGroupAccess = String.join(", ", accessList);
        
        String updateSql = "UPDATE groups SET groupAccess = ? WHERE groupTitle = ?";
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);
        updateStatement.setString(1, newGroupAccess);
        updateStatement.setString(2, group);
        
        updateStatement.executeUpdate();
        System.out.println("User access removed.");
    } 
    else {
        System.out.println("doesn't have access already.");
    }
}

/**
 * this function gives a user admin privileges 
 * @param group
 * @param admin
 * @throws SQLException
 */
public void addAdminToGroup(String group, String admin) throws SQLException {
    
    String sql = "SELECT groupAdmin FROM groups WHERE groupTitle = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setString(1, group);                                 // Set the title
    
    ResultSet rs = preparedStatement.executeQuery();
    
    if (!rs.next()) {
        throw new SQLException("Group not found: " + group);
    }
    
    String groupAdmins = rs.getString("groupAdmin");
    
    if (groupAdmins == null) {                                        //if it is null or empty, initialize it as an empty string
        groupAdmins = "";
    }
    
    String[] admins = groupAdmins.split("\\s*,\\s*");
    
    boolean inGroup = Arrays.asList(admins).contains(admin);        //check if the article is already in the list
    
    if (!inGroup) {                                                     //if the article is not in the group, add it
        List<String> adminList = new ArrayList<>(Arrays.asList(admins));
        adminList.add(admin);
        
        String newGroupAdmins = String.join(", ", adminList);
        
        String updateSql = "UPDATE groups SET groupAdmin = ? WHERE groupTitle = ?";
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);
        updateStatement.setString(1, newGroupAdmins);
        updateStatement.setString(2, group);
        
        updateStatement.executeUpdate();
        System.out.println("Admin aded to the group");
    } 
    else {
        System.out.println("Admin is already in the group.");
    }
}


/**
 * this function removes a users privileges from a group
 * @param group
 * @param admin
 * @throws SQLException
 */
public void removeAdminFromGroup(String group, String admin) throws SQLException {
    
    String sql = "SELECT groupAdmin FROM groups WHERE groupTitle = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setString(1, group);                                 // Set the title
    
    ResultSet rs = preparedStatement.executeQuery();
    
    if (!rs.next()) {
        throw new SQLException("Group not found: " + group);
    }
    
    String groupAdmins = rs.getString("groupAdmin");
    
    if (groupAdmins == null) {                                        //if it is null or empty, initialize it as an empty string
        groupAdmins = "";
    }
    
    String[] admins = groupAdmins.split("\\s*,\\s*");
    
    boolean inGroup = Arrays.asList(admins).contains(admin);        //check if the article is already in the list
    
    if (inGroup) {                                                     //if the article is in the group, remove it
        List<String> adminList = new ArrayList<>(Arrays.asList(admins));
        adminList.remove(admin);
        
        String newGroupAdmins = String.join(", ", adminList);
        
        String updateSql = "UPDATE groups SET groupAdmin = ? WHERE groupTitle = ?";
        PreparedStatement updateStatement = connection.prepareStatement(updateSql);
        updateStatement.setString(1, newGroupAdmins);
        updateStatement.setString(2, group);
        
        updateStatement.executeUpdate();
    } 
    else {
        System.out.println("Admin is not in the group.");
    }
}



	

/**

this function check if a user has admin privileges over a group
@param group
@param newAdmin
@return
@throws SQLException*/
public boolean checkInAdmin(String group, String newAdmin) throws SQLException {
    String sql = "SELECT groupAdmin FROM groups WHERE groupTitle = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setString(1, group); // Set the title parameter

        ResultSet rs = preparedStatement.executeQuery();

        if (!rs.next()) {
            System.out.println("Group not found.");
            return false;                                             //no group found, so the admin can't be in it.
        }

        String groupAdmins = rs.getString("groupAdmin");

        if (groupAdmins == null || groupAdmins.trim().isEmpty()) {
            System.out.println("No admins in this group.");
            return false;                                             //no admin in the group
        }

        String[] articles = groupAdmins.split("\\s*,\\s*");         //split the admin into an array

        //using a HashSet for fast lookups
        Set<String> adminSet = new HashSet<>(Arrays.asList(articles));

        if (adminSet.contains(newAdmin)) {
            System.out.println(newAdmin + " is already an admin in the group.");
            return true;                                             //admin is found
        } else {
            System.out.println(newAdmin + " is not an admin in the group.");
            return false;                                             //admin is not found
        }
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
    public String listArticle() throws Exception {
    	String s="";
    	int count=1;
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

        
            s+= 	"--------------------------------------"+"\n"+
           		 count++ +":  "+"Title: " + title+"  "+"Authors: " + authors+"  "+"Abstract: " + abstractText+"\n\n";
        }
		return s;
    }
    
    public String filterbybeginner(String level2) throws Exception {
    	int count=1;
    	String s="";
        String sql = "SELECT * FROM articles WHERE level = ?"; 
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, level2);
        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) { 
            long id = rs.getLong("id"); 
            String title = rs.getString("title"); 
            String authors = rs.getString("authors");  
            String abstractText = rs.getString("abstract");
            String level = rs.getString("level");
            String groups = rs.getString("groups");

        
            s+= 	"--------------------------------------"+"\n"+
           		 count++ +":  "+"Title: " + title+"  "+"Authors: " + authors+"  "+"Abstract: " + abstractText+"\n\n";
        }
		return s+(count-1);
    }
    
    
    
    /**********
	 * This function returns all the data of an article depending on the ID
	 */
    public String getdata(long id) throws Exception {
    	String s="";
        String sql = "SELECT * FROM articles WHERE id = ?"; 
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setLong(1, id);

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
    
    
    public String filterbyTitle(String title2) throws SQLException {
    	int count=1;
    	String s="";
    	String sql = "SELECT * FROM articles WHERE title = ?"; 
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, title2); // Set the title parameter

        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) { 
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

            s+= 	"--------------------------------------"+"\n"+
           		 count++ +":  "+"Title: " + title+"  "+"Authors: " + authors+"  "+"Abstract: " + abstractText+"\n\n";
			}
		return s;
    }
    
    public String filterbyauthor(String author2) throws SQLException {
    	String s="";
    	int count=1;
    	String sql = "SELECT * FROM articles WHERE authors = ?"; 
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, author2); // Set the title parameter

        ResultSet rs = preparedStatement.executeQuery();

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

            s+= 	"--------------------------------------"+"\n"+
            		 count++ +":  "+"Title: " + title+"  "+"Authors: " + authors+"  "+"Abstract: " + abstractText+"\n\n";
			}
		return s;
    }
    
    public String filterbyabstract(String author2) throws SQLException {
    	int count=1;
    	String s="";
    	String sql = "SELECT * FROM articles WHERE abstract = ?"; 
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, author2); // Set the title parameter

        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) { 
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

            s+= 	"--------------------------------------"+"\n"+
           		 count++ +":  "+"Title: " + title+"  "+"Authors: " + authors+"  "+"Abstract: " + abstractText+"\n\n";
			}
		return s;
    }
    
    public String filterbyid(String id2) throws SQLException {
    	int count=1;
    	String s="";
    	String sql = "SELECT * FROM articles WHERE id = ?"; 
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, id2); // Set the title parameter

        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) { 
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

            s+= 	"--------------------------------------"+"\n"+
           		 count++ +":  "+"Title: " + title+"  "+"Authors: " + authors+"  "+"Abstract: " + abstractText+"\n\n";
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
    /**
     * this function backs up grouped articles to a file of the user's choosing
     * @param filename
     * @param groups
     * @throws SQLException
     */
    public void backupGroupedArticles(String filename, String groups) throws SQLException {
        
        String[] articleList = articlesFromGroups(groups);        //get the list of articles
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            String sql = "SELECT * FROM articles";
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                boolean titleMatchesGroup = false;
                String articleTitle = rs.getString("title");

                for (String article : articleList) {                //check if the article's title matches any of the groups in groupList
                    if (articleTitle.equals(article)) {
                        titleMatchesGroup = true;
                        break;
                    }
                }
                if (titleMatchesGroup) {                        // if the title matches any group, write the article to the file
                    writer.println(rs.getLong("id") + "~" +      // get id
                                   articleTitle + "~" +
                                   rs.getString("authors") + "~" +
                                   rs.getString("abstract") + "~" +
                                   rs.getString("keywords") + "~" +
                                   rs.getString("body") + "~" +  // the encrypted body
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
    
    /**
     * this function returns all of the articles in a group as an array
     * @param groupsList
     * @return
     * @throws SQLException
     */
    public String[] articlesFromGroups(String groupsList) throws SQLException {
        
        String[] groups = groupsList.split("\\s*,\\s*");                        //create array from given groups
        
        Set<String> uniqueArticles = new HashSet<>();
        
        for (String group : groups) {
            String query = "SELECT groupArticles FROM groups WHERE groupTitle = ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, group.trim());                                 //set the group name parameter
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String groupArticles = rs.getString("groupArticles");
                        
                        if (groupArticles != null && !groupArticles.isEmpty()) {
                            String[] articles = groupArticles.split(",");        // split the groupArticles into individual article titles
                            
                            for (String article : articles) {
                                uniqueArticles.add(article.trim());                //add each article title to the Set (duplicates will be ignored)
                            }
                        }
                    }
                }
            }
        }
        return uniqueArticles.toArray(new String[0]);
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


package edu.asu.ASUHelloWorldJavaFXMaven;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.Base64;

public class JUnitArticleTest {


    private ArticleDatabaseHelper dbArtHelper;
    private Connection connection;
    
    @BeforeEach
    void setUp() throws SQLException {
        // Use an in-memory database for testing
        String jdbcUrlArt = "jdbc:h2:mem:testdb";
        connection = DriverManager.getConnection(jdbcUrlArt, "sa", "");
        
        // Set up the database, matches ArticleDatabaseHelper
        
    	try (Statement stmt = connection.createStatement()){
    		
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
            stmt.execute(articleTable);
    	}
    	
    	try (Statement stmt = connection.createStatement()){
  
           	 String thirdTables = "CREATE TABLE IF NOT EXISTS groups ("
                        + "groupID BIGINT PRIMARY KEY, "
                        + "groupTitle TEXT, "
                        + "groupArticles TEXT, "
                        + "specialAccess BOOLEAN, "
                        + "groupAdmin TEXT, "
                        + "groupAccess TEXT) ";
           	 stmt.execute(thirdTables);
           }
    	
    	try (Statement stmt = connection.createStatement()){   
            String messageTable = "CREATE TABLE IF NOT EXISTS message ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "type VARCHAR(255), "
                    + "body TEXT)";
            stmt.execute(messageTable);
        }
    	
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM articles");  // Remove existing articles
            stmt.executeUpdate("DELETE FROM groups");    // Remove existing groups
        }
    	
        try (Statement stmt = connection.createStatement()) {
            // Insert test data
            stmt.executeUpdate("INSERT INTO articles (id, title, authors, abstract, keywords, body, references, level, groups) "
                    + "VALUES (1, 'title', 'authors', 'abstract', 'keys', 'body', 'references', 'level', 'group') ");
            stmt.executeUpdate("INSERT INTO groups (groupID, groupTitle, groupArticles, specialAccess, groupAdmin, groupAccess) "
                    + "VALUES (7, 'Gtitle', 'Garticles', true, 'Gadmin', 'Gaccess') ");
        }
        
        dbArtHelper = new ArticleDatabaseHelper(connection);
    }
    
    @Test
    void testCreateGroup() throws SQLException {
        //Set up test data for creating a group
        String title = "Gtitle";
        String articles = "Garticles";
        Boolean specialAccess = true;
        String groupAdmin = "Gadmin";
        String groupAccess = "Gaccess";

        //Create a new group
        dbArtHelper.createGroup(title, articles, specialAccess, groupAdmin, groupAccess);

        //Verify that the group was created
        assertTrue(dbArtHelper.groupExists(title), "Group should exist in the database after creation");
    }

    @Test
    void testGroupExists() throws SQLException {
        //Set up a group for testing
        String title = "Gtitle";
        String articles = "Garticles";
        Boolean specialAccess = true;
        String groupAdmin = "Gadmin";
        String groupAccess = "Gaccess";
        
        dbArtHelper.createGroup(title, articles, specialAccess, groupAdmin, groupAccess);
        
        //Check if the group exists
        boolean exists = dbArtHelper.groupExists(title);
        
        //The group should exist
        assertTrue(exists, "Group exists");
    }
    
    @Test
    void testGroupNotExists() throws SQLException {
        //A group that has not been created
        String title = "NonGroup";
        
        // When: Check if the non-existent group exists
        boolean exists = dbArtHelper.groupExists(title);
        
        // Then: The group should not exist
        assertFalse(exists, "Group should not exist");
    }

   
	//TEST THIS FOR MAX
    @Test
    void testViewAllMessages() throws Exception {
        //Insert some test messages, testing the savemessage function
    	dbArtHelper.savemessage(1, "Generic message content");
    	dbArtHelper.savemessage(2, "Specific message content");
        
        //Retrieve all messages
        String messages = dbArtHelper.viewallmessage();
        
        //Verify that the messages are correctly retrieved
        assertTrue(messages.contains("Type: Generic message"), "should contain Generic message");
        assertTrue(messages.contains("Type: Specific message"), "should contain Specific message");
        
        //Check if the actual message bodies are present
        assertTrue(messages.contains("Body of message: Generic message content"), "should contain Generic message body");
        assertTrue(messages.contains("Body of message: Specific message content"), "should contain Specific message body");
    }
    
    @Test
    void testIsSpecialGroup() throws SQLException {
        //Set up a group
        String title = "Special Group";
        String articles = "Article1, Article2";
        Boolean specialAccess = true;
        String groupAdmin = "adminUser";
        String groupAccess = "read";
        
        dbArtHelper.createGroup(title, articles, specialAccess, groupAdmin, groupAccess);
        
        //Check if the group is marked as special
        boolean isSpecial = dbArtHelper.isSpecialGroup(title);
        
        //The group should have special access
        assertTrue(isSpecial, "The group is special");
    }
    
    @Test
    void testAddArticleToGroup() throws SQLException {
        //Set up a group and an article
        String title = "Group1";
        String articles = "Article1";
        Boolean specialAccess = true;
        String groupAdmin = "adminUser";
        String groupAccess = "read";
        
        dbArtHelper.createGroup(title, articles, specialAccess, groupAdmin, groupAccess);
        
        String article = "Article3";

        //Add an article to the group
        dbArtHelper.addArticleToGroup(title, article);

        //Verify the article was added to the group
        String sql = "SELECT groupArticles FROM groups WHERE groupTitle = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, title);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String updatedArticles = rs.getString("groupArticles");
                assertTrue(updatedArticles.contains(article), "The article should be added");
            }
        }
    }
    
    @Test
    void testCheckInArticles() throws SQLException {
        //Set up a group with a list of articles
        String group = "Gtitle";
        String newArticle = "Article1";
        String groupArticles = "Article1, Article2, Article3";
        
        //Manually generate a unique GROUPID
        String getGroupId = "SELECT MAX(groupID) FROM groups";
        long groupId = 1; // Default to 1 for the first insert
        
        try (PreparedStatement maxIdStmt = connection.prepareStatement(getGroupId);
                ResultSet maxIdRs = maxIdStmt.executeQuery()) {
               if (maxIdRs.next()) {
                   groupId = maxIdRs.getLong(1) + 1;
               }
           }
        
        //Insert the group into the database with the GROUPID
        String insertGroupSql = "INSERT INTO groups (groupID, groupTitle, groupAccess) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertGroupSql)) {
            stmt.setLong(1, groupId);
            stmt.setString(2, group);
            stmt.setString(3, groupArticles);
            stmt.executeUpdate();
        }

        //Check if the article exists in the group
        boolean result = dbArtHelper.checkInArticles(group, newArticle);

        //The result should be true, as the article exists
        assertTrue(result, "Article should be in the group.");
    }

    @Test
    void testRemoveArticleFromGroup_ArticleExists() throws SQLException {
        //Set up a group with some articles
        String group = "Gtitle";
        String article = "Article1";
        String initialArticles = "Article1, Article2, Article3";
        
        //Same manual generation
        String getGroupId = "SELECT MAX(groupID) FROM groups";
        long groupId = 1; 
        
        try (PreparedStatement maxIdStmt = connection.prepareStatement(getGroupId);
                ResultSet maxId = maxIdStmt.executeQuery()) {
               if (maxId.next()) {
                   groupId = maxId.getLong(1) + 1;  // Increment the maximum GROUPID
               }
           }
        
        //Insert the group into the database with the GROUPID
        String insertGroup = "INSERT INTO groups (groupID, groupTitle, groupAccess) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertGroup)) {
            stmt.setLong(1, groupId); 
            stmt.setString(2, group);
            stmt.setString(3, initialArticles);
            stmt.executeUpdate();
        }

        //Remove the article from the group
        dbArtHelper.removeArticleFromGroup(group, article);

        //Verify that the article was removed from the group
        String updatedGroup = null;
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT groupArticles FROM groups WHERE groupTitle = ?")) {
            stmt.setString(1, group);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
            	updatedGroup = rs.getString("groupArticles");
            }
        }

        // Verify that the updated groupArticles no longer contains the article
        assertFalse(updatedGroup.contains(article), "Article should be removed from the group.");
    }

  //TEST FOR TUSHCAR
    @Test
    void testAddAccessToGroup() throws SQLException {
    	//Set up a group with some initial access
    	String group = "Gtitle";
    	String initialAccess = "user1";
    	String newAccess = "user3";

    	String getGroupId = "SELECT MAX(groupID) FROM groups";
    	long groupId = 1;
    
    	try (PreparedStatement maxIdStmt = connection.prepareStatement(getGroupId);
    			ResultSet maxIdRs = maxIdStmt.executeQuery()) {
    			if (maxIdRs.next()) {
    				groupId = maxIdRs.getLong(1) + 1;  // Increment the maximum GROUPID
    			}
 	  }

 	  String insertGroup = "INSERT INTO groups (groupID, groupTitle, groupAccess) VALUES (?, ?, ?)";
 	  try (PreparedStatement stmt = connection.prepareStatement(insertGroup)) {
 		  stmt.setLong(1, groupId); 
 		  stmt.setString(2, group);
 		  stmt.setString(3, initialAccess);
 		  stmt.executeUpdate();
 	  }

 	  //Verify group exists before proceeding
 	  String checkGroup = "SELECT 1 FROM groups WHERE groupTitle = ?";
 	  try (PreparedStatement checkStmt = connection.prepareStatement(checkGroup)) {
 		  checkStmt.setString(1, group);
 		  try (ResultSet checkRs = checkStmt.executeQuery()) {
 			  assertTrue(checkRs.next(), "Group should exist in the database before adding access.");
 		  }
 	  }

 	  //Add new access to the group
 	  dbArtHelper.addAccessToGroup(group, initialAccess);
 	  dbArtHelper.addAccessToGroup(group, newAccess);
    
 	  //Verify that the new access has been added to the groupAccess list
 	  String sql = "SELECT groupAccess FROM groups WHERE groupTitle = ?";
 	  try (PreparedStatement stmt = connection.prepareStatement(sql)) {
 		  stmt.setString(1, group);
 		  try (ResultSet rs = stmt.executeQuery()) {
 	          assertTrue(rs.next(), "Group should exist in the database");

 	          //Check the groupAccess field
 	          String updatedAccess = rs.getString("groupAccess");
 	          assertTrue(updatedAccess.contains(newAccess), "New access should be added to groupAccess.");
 	          assertTrue(updatedAccess.contains(initialAccess), "Initial access should remain in groupAccess.");
 		  }
 	  }
    }

  //TEST FOR TUSHCAR
    @Test
    void testAddAdminToGroup() throws SQLException {
    	//A group exists and does not have 'admin1' as a member.
    	String group = "groupA";
    	String admin = "admin1";

    	String getGroupId = "SELECT MAX(groupID) FROM groups";
    	long groupId = 1;
    
    	try (PreparedStatement maxIdStmt = connection.prepareStatement(getGroupId);
    			ResultSet maxIdRs = maxIdStmt.executeQuery()) {
    			if (maxIdRs.next()) {
    				groupId = maxIdRs.getLong(1) + 1; 
    			}
    	}
    	
    	String insertGroup = "INSERT INTO groups (groupID, groupTitle, groupAccess) VALUES (?, ?, ?)";
    	try (PreparedStatement stmt = connection.prepareStatement(insertGroup)) {
    		stmt.setLong(1, groupId);  
    		stmt.setString(2, group);
    		stmt.setString(3, "");
    		stmt.executeUpdate();
    	}
    	
    	//Add an admin to the group
    	dbArtHelper.addAdminToGroup(group, admin);

    	//Verify the admin was added to the group
    	String sql = "SELECT groupAdmin FROM groups WHERE groupTitle = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
    		pstmt.setString(1, group);
    		ResultSet rs = pstmt.executeQuery();
    		if (rs.next()) {
    			String admins = rs.getString("groupAdmin");
    			assertTrue(admins.contains(admin), "Admin should be added to the group.");
    		}
    	}
    }
    
  //TEST FOR ALAN
    @Test
    void testRemoveAdminFromGroup() throws SQLException {
        //A group exists and has 'admin1' as a member.
        String group = "groupA";
        String admin = "admin1";

        String getGroupId = "SELECT MAX(groupID) FROM groups";
        long groupId = 1;
        
        try (PreparedStatement maxIdStmt = connection.prepareStatement(getGroupId);
             ResultSet maxIdRs = maxIdStmt.executeQuery()) {
            if (maxIdRs.next()) {
                groupId = maxIdRs.getLong(1) + 1; 
            }
        }

        String insertGroup = "INSERT INTO groups (groupID, groupTitle, groupAdmin, groupAccess) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertGroup)) {
            stmt.setLong(1, groupId);  
            stmt.setString(2, group);
            stmt.setString(3, admin); 
            stmt.setString(4, "");
            stmt.executeUpdate();
        }
        
        // When: Remove the admin from the group
        dbArtHelper.removeAdminFromGroup(group, admin);

        // Then: Verify the admin was removed from the group
        String sql = "SELECT groupAdmin FROM groups WHERE groupTitle = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, group);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String admins = rs.getString("groupAdmin");
                // Assert that 'admin1' is no longer in the list of admins
                assertFalse(admins.contains(admin), "Admin should be removed from the group.");
            }
        }
    }

  //TEST FOR ALAN
    @Test
    void testRemoveAdminThatDoesNotExist() throws SQLException {
        //A group exists and does not have 'admin1' as a member.
        String group = "groupB";
        String admin = "admin1";


        String getGroupId = "SELECT MAX(groupID) FROM groups";
        long groupId = 1;
        
        try (PreparedStatement maxIdStmt = connection.prepareStatement(getGroupId);
             ResultSet maxIdRs = maxIdStmt.executeQuery()) {
            if (maxIdRs.next()) {
                groupId = maxIdRs.getLong(1) + 1;
            }
        }

        String insertGroup = "INSERT INTO groups (groupID, groupTitle, groupAdmin, groupAccess) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertGroup)) {
            stmt.setLong(1, groupId); 
            stmt.setString(2, group);
            stmt.setString(3, "admin2");
            stmt.setString(4, "");
            stmt.executeUpdate();
        }
        
        //Try to remove an admin ('admin1') who is not part of the group
        dbArtHelper.removeAdminFromGroup(group, admin);

        //Verify the admin was not removed because they were not part of the group
        String sql = "SELECT groupAdmin FROM groups WHERE groupTitle = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, group);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String admins = rs.getString("groupAdmin");
                //Check that 'admin2' is still in the list of admins.
                assertTrue(admins.contains("admin2"), "Admin 'admin2' should still be in the group.");
            }
        }
    }
    
    //TEST THIS FOR TAJ
    @Test
    void testAddArticle() throws Exception {
        //Define article details
        String title = "TestTitle";
        String authors = "TestAuthor";
        String abstractText = "TestAbstract.";
        String keywords = "TestKey";
        String body = "TestBody, should be encrypted.";
        String references = "TestReference";
        String level = "Beginner";
        String groups = "TestGroup";

        //Add the article to the database
        dbArtHelper.addArticle(title, authors, abstractText, keywords, body, references, level, groups);

        //Verify the article was inserted into the database
        String selectArticle = "SELECT * FROM articles WHERE title = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectArticle)) {
            pstmt.setString(1, title);
            ResultSet rs = pstmt.executeQuery();
            
            //Ensure that the article was inserted and retrieved
            if (rs.next()) {
                //Check the article
                long id = rs.getLong("id");
                String retrievedTitle = rs.getString("title");
                String retrievedAuthors = rs.getString("authors");
                String retrievedAbstract = rs.getString("abstract");
                String retrievedKeywords = rs.getString("keywords");
                String encryptedBody = rs.getString("body");
                String retrievedReferences = rs.getString("references");
                String retrievedLevel = rs.getString("level");
                String retrievedGroups = rs.getString("groups");

                //Check that the data matches the inserted values
                assertEquals(title, retrievedTitle);
                assertEquals(authors, retrievedAuthors);
                assertEquals(abstractText, retrievedAbstract);
                assertEquals(keywords, retrievedKeywords);
                assertEquals(references, retrievedReferences);
                assertEquals(level, retrievedLevel);
                assertEquals(groups, retrievedGroups);
                
                //Check that the body is properly encrypted (Base64 encoded)
                String decodedBody = new String(Base64.getDecoder().decode(encryptedBody));
                assertEquals(body, decodedBody);  //Verify that the decoded body matches the original body

                //Verify that the unique ID is greater than 0 (indicating it's a valid ID)
                assertTrue(id > 0, "Unique ID should be greater than 0.");
            }
        }
    }
    
    //TEST THIS FOR TAJ
    @Test
    void testDeleteArticle() throws SQLException {
        //Define article details
        String title = "TempTitle";
        String authors = "TempAuthor";
        String abstractText = "TempAbstract.";
        String keywords = "TempKeys";
        String body = "TempBody";
        String references = "TempReference";
        String level = "Beginner";
        String groups = "TempGroup";

        //Insert an article into the database, the catch should fail as there is no exception
        try {
			dbArtHelper.addArticle(title, authors, abstractText, keywords, body, references, level, groups);
		} catch (Exception e) {
			e.printStackTrace();
		}

        //Retrieve the ID of the inserted article
        String selectArticleSql = "SELECT id FROM articles WHERE title = ?";
        long articleId = 0;
        try (PreparedStatement pstmt = connection.prepareStatement(selectArticleSql)) {
            pstmt.setString(1, title);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                articleId = rs.getLong("id");
            }
        }

        //Delete the article using its ID
        dbArtHelper.deleteArticle(articleId);

        //Verify that the article was deleted, console also prints the trace from the method
        String checkDeletedSql = "SELECT * FROM articles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(checkDeletedSql)) {
            pstmt.setLong(1, articleId);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                System.out.println("Test article successfully deleted.");
            }
        }
    }

    //TEST THIS FOR TAJ
    @Test
    void testDeleteNonExistingArticle() throws SQLException {
        //Define a non-existing article ID
        long nonExistingArticleId = 9999;  //This ID should not exist in the database

        //Try to delete a non-existing article
        dbArtHelper.deleteArticle(nonExistingArticleId);

        //Ensure that no exception is thrown and the article was not found
        String checkNonExistingSql = "SELECT * FROM articles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(checkNonExistingSql)) {
            pstmt.setLong(1, nonExistingArticleId);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                System.out.println("No article found with ID: " + nonExistingArticleId);
            }
        }
    }
}
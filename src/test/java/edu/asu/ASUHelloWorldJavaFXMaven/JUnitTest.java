package edu.asu.ASUHelloWorldJavaFXMaven;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.Base64;

public class JUnitTest {

    private DatabaseHelper dbHelper;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private Statement mockStatement;

 
   @BeforeEach
    void setUp() throws SQLException {
        // You can use H2 in-memory database for testing
        String jdbcUrl = "jdbc:h2:mem:testdb"; // H2 in-memory DB
        mockConnection = DriverManager.getConnection(jdbcUrl, "sa", "");
        
    	try (Statement stmt = mockConnection.createStatement()){
    		
    		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
    				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
    				+ "password VARCHAR(255), "
    				+ "role VARCHAR(20), "
    				+ "access BOOLEAN, "
    				+ "email VARCHAR(255), "
    				+ "first VARCHAR(255), "
    				+ "middle VARCHAR(255), "
    				+ "last VARCHAR(255), "
    				+ "preferred VARCHAR(255), "
    				+ "USERNAME VARCHAR(255), "
    				+ "temp VARCHAR(255), "
    				+ "date VARCHAR(255))";
    				
    		stmt.execute(userTable);
    	}
    	
        try (Statement stmt = mockConnection.createStatement()) {
            // Insert test users
            stmt.executeUpdate("INSERT INTO cse360users (password, role, access, email, first, middle, last, preferred, USERNAME, temp, date) "
                    + "VALUES ('pass123', 'admin', true, 'admin', 'First', 'Middle', 'Last', 'Pref', 'USERNAME', 'temp123', '2024-11-01')");
            stmt.executeUpdate("INSERT INTO cse360users (password, role, access, email, first, middle, last, preferred, USERNAME, temp, date) "
                    + "VALUES ('pass456', 'user', false, 'user', 'SFirst', 'SMiddle', 'SLast', 'SPref', 'SUSERNAME', 'temp456', '2024-11-02')");
        }
        
        dbHelper = new DatabaseHelper(mockConnection);
    }
    
/*    @Test
    void testIsDatabaseEmpty_whenDatabaseIsEmpty() throws SQLException {
        // When
        boolean isEmpty = dbHelper.isDatabaseEmpty();

        // Then
        assertTrue(isEmpty, "Database should be empty initially");
    }
*/
   @Test
   void testAccess() throws SQLException {
       // Given
       String username = "testUser";  // The username you're testing for
       boolean expectedAccess = true; // The expected access value

       // When
       dbHelper.access(username); // Call the method to test

       // Then
       String query = "SELECT access FROM cse360users WHERE username = ?";
       try (PreparedStatement pstmt = mockConnection.prepareStatement(query)) {
           pstmt.setString(1, username); // Set the username in the query
           
           try (ResultSet rs = pstmt.executeQuery()) {
               assertTrue(rs.next(), "User should be found in the database");
               assertEquals(expectedAccess, rs.getBoolean("access"), "Access value should match the expected value");
           }
       }
   }
   
   
   @Test
   void testRegister() throws SQLException {
       // Given
       String username = "testUser";
       String password = "testPassword";
       String role = "student";  // Example role

       // When
       dbHelper.register(username, password, role); // Call the method to test
       
       // Then
       String query = "SELECT * FROM cse360users WHERE username = ? AND role = ?";
       try (PreparedStatement pstmt = mockConnection.prepareStatement(query)) {
           pstmt.setString(1, username); // Set the username in the query
           pstmt.setString(2, role);     // Set the role in the query
           
           try (ResultSet rs = pstmt.executeQuery()) {
               assertTrue(rs.next(), "User should be inserted into the database");
               assertEquals(username, rs.getString("username"));
               assertEquals(role, rs.getString("role"));
               // Optionally check the encrypted password:
               String encryptedPasswordFromDb = rs.getString("password");
               String expectedEncryptedPassword = Base64.getEncoder().encodeToString(password.getBytes());
               assertEquals(expectedEncryptedPassword, encryptedPasswordFromDb, "Passwords should match");
           }
       }
   }
   
    @Test
    void testInvitedata() throws SQLException {
        // Given
        String role = "role";
        String temp = "temp123";
        String date = "2024-11-20";

        // When
        dbHelper.invitedata(role, temp, date); // Call the method to test
        
        // Then
        String query = "SELECT * FROM cse360users WHERE role = ? AND temp = ? AND date = ?";
        try (PreparedStatement pstmt = mockConnection.prepareStatement(query)) {
            pstmt.setString(1, role);
            pstmt.setString(2, temp);
            pstmt.setString(3, date);        
            
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "User should be inserted into the database");
                assertEquals(role, rs.getString("role"));
                assertEquals(temp, rs.getString("temp"));
                assertEquals(date, rs.getString("date"));
                }
        }
    }	
    
    @Test
    void testUpdate() throws SQLException {
        // Given
        String username = "testUser";
        String initialEmail = "initialEmail@example.com";
        String initialFirst = "InitialFirst";
        String initialMiddle = "InitialMiddle";
        String initialLast = "InitialLast";
        String initialPreferred = "InitialPreferred";
        
        // Insert initial data into the database for the given username
        String insertUser = "INSERT INTO cse360users (username, email, first, middle, last, preferred, access) VALUES (?, ?, ?, ?, ?, ?, false)";
        try (PreparedStatement pstmt = mockConnection.prepareStatement(insertUser)) {
            pstmt.setString(1, username);
            pstmt.setString(2, initialEmail);
            pstmt.setString(3, initialFirst);
            pstmt.setString(4, initialMiddle);
            pstmt.setString(5, initialLast);
            pstmt.setString(6, initialPreferred);
            pstmt.executeUpdate();
        }

        // New data to update
        String newEmail = "newEmail@example.com";
        String newFirst = "NewFirst";
        String newMiddle = "NewMiddle";
        String newLast = "NewLast";
        String newPreferred = "NewPreferred";
        
        // When: Call the method to update the user details
        dbHelper.update(newEmail, newFirst, newMiddle, newLast, newPreferred, username);
        
        // Then: Check if the data is updated in the database
        String query = "SELECT * FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = mockConnection.prepareStatement(query)) {
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "User should be updated in the database");
                // Check updated fields
                assertEquals(newEmail, rs.getString("email"));
                assertEquals(newFirst, rs.getString("first"));
                assertEquals(newMiddle, rs.getString("middle"));
                assertEquals(newLast, rs.getString("last"));
                assertEquals(newPreferred, rs.getString("preferred"));
                // Check if access is set to true
                assertTrue(rs.getBoolean("access"));
            }
        }
    }
    
    @Test
    void testResetUser() throws SQLException {
        // Given: Setup a user with a specific username and temp value to test
        String username = "testUser";
        String temp = "temp123"; // This is the temp value we will use to find the user
        String date = "2024-11-20";  // The new date value to update

        // Insert the user with a specific username and temp value
        String insertUser = "INSERT INTO cse360users (username, temp, password) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = mockConnection.prepareStatement(insertUser)) {
            pstmt.setString(1, username);
            pstmt.setString(2, temp);
            pstmt.setString(3, "oldPassword");  // Set an initial password
            pstmt.executeUpdate();
        }

        // Confirm the user is inserted
        String checkInsertQuery = "SELECT username, temp, password FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = mockConnection.prepareStatement(checkInsertQuery)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "User should be inserted into the database");
                assertEquals(temp, rs.getString("temp"));
                assertNotNull(rs.getString("password"), "Password should not be null initially");
            }
        }

        // When: Call the resetuser method to reset the password, update the temp and date
        dbHelper.resetuser(username, temp, date);

        // Then: Check that the password is null, the temp is updated, and the date is set
        String query = "SELECT password, temp, date FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = mockConnection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "User should be found in the database with the specified username");
                // Check that the password is set to null
                assertNull(rs.getString("password"), "Password should be set to null");
                // Check that the temp is still the same
                assertEquals(temp, rs.getString("temp"), "Temp value should remain the same");
                // Check that the date is set correctly
                assertEquals(date, rs.getString("date"), "Date should be updated");
            }
        }
    }
    
    @Test
    void testUpdatePass() throws SQLException {
        // Given: Setup a user with a specific username and password to test
        String username = "testUser";
        String oldPassword = "oldPassword123"; // Old password to insert
        String newPassword = "newPassword456";  // The new password to update
        String encryptedOldPassword = Base64.getEncoder().encodeToString(oldPassword.getBytes());
        String encryptedNewPassword = Base64.getEncoder().encodeToString(newPassword.getBytes());

        // Insert the user with an initial password
        String insertUser = "INSERT INTO cse360users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = mockConnection.prepareStatement(insertUser)) {
            pstmt.setString(1, username);
            pstmt.setString(2, encryptedOldPassword);  // Insert the old password
            pstmt.executeUpdate();
        }

        // Confirm the user is inserted and the password is correct
        String checkInsertQuery = "SELECT username, password FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = mockConnection.prepareStatement(checkInsertQuery)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "User should be inserted into the database");
                assertEquals(encryptedOldPassword, rs.getString("password"), "Password should match the old one");
            }
        }

        // When: Call the updatepass method to update the password
        dbHelper.updatepass(newPassword, username); // Update the password

        // Then: Check that the password is updated to the new encrypted password
        String query = "SELECT password FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = mockConnection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "User should be found in the database with the specified username");
                // Check that the password is updated to the new encrypted password
                assertEquals(encryptedNewPassword, rs.getString("password"), "Password should be updated to the new encrypted password");
            }
        }
    }
    
    @Test
    void testSetRole() throws SQLException {
        // Given: Create a user with an initial role
        String username = "testuser";
        String initialRole = "student";
        dbHelper.register(username, "password", initialRole); // Assuming you have a register method for this purpose

        // When: Update the user's role
        String newRole = "admin";
        dbHelper.setrole(newRole, username);

        // Then: Verify the role is updated in the database
        String query = "SELECT role FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = mockConnection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "User should be found in the database");
                assertEquals(newRole, rs.getString("role"), "Role should be updated to the new role");
            }
        }
    }

    @Test
    void testLogin() throws SQLException {
        // Given: Create a user with known credentials
        String username = "testuser";
        String password = "password123";  // This will be encrypted in the login method
        dbHelper.register(username, password, "student"); // Assuming register method is available

        // When: Attempt to log in with correct credentials
        boolean loginSuccessful = dbHelper.login(username, password);

        // Then: The login should be successful
        assertTrue(loginSuccessful, "Login should be successful with correct username and password");
    }
    
    void testHelpTemp() throws SQLException {
        // Given: Insert a user with a known temp value
        String temp = "temp123";
        String username = "testuser";
       
        dbHelper.register(username, "password123", "student"); // Assuming the register method works
        
        // Insert a row with the known temp value (directly using SQL for this test)
        String insertTemp = "INSERT INTO cse360users (username, temp) VALUES (?, ?)";
        try (PreparedStatement pstmt = mockConnection.prepareStatement(insertTemp)) {
            pstmt.setString(1, username);
            pstmt.setString(2, temp);
            pstmt.executeUpdate();
        }

        // When: Check if the temp value exists
        boolean result = dbHelper.helptemp(temp);

        // Then: The result should be true since the temp exists in the database
        assertTrue(result, "Temp should exist in the database");
    }

    @Test
    void testDoesUserExistWithExistingUser() throws SQLException {
        // Given: A user "testuser" exists in the database

        String username = "testuser"; // Known user

        // When: Check if the user exists
        boolean result = dbHelper.doesUserExist(username);

        // Then: The result should be true since the user exists
        assertTrue(result, "User should exist in the database");
    }

    @Test
    void testDoesUserExistWithNonExistingUser() throws SQLException {
        // Given: A user "nonexistentuser" does not exist in the database

        String username = "nonexistentuser"; // Non-existing user

        // When: Check if the user exists
        boolean result = dbHelper.doesUserExist(username);

        // Then: The result should be false since the user does not exist
        assertFalse(result, "User should not exist in the database");
    }

    
    @Test
    void testPrefnameWithExistingUserAndPreferredName() throws SQLException {
        // Given: A user "testuser" with a preferred name "Pref"
        String username = "testuser";

        // When: Call the prefname method
        String result = dbHelper.prefname(username);

        // Then: The result should be the preferred name "Pref"
        assertEquals("Pref", result, "The preferred name should be 'Pref'");
    }
    
    @Test
    void testDisplayUsersByAdmin() throws SQLException {
        // Given: Two users are inserted into the database.
        
        // When: The displayUsersByAdmin method is called
        dbHelper.displayUsersByAdmin();
        
        // Then: Verify that the data for these users exists in the database.
        String query = "SELECT * FROM cse360users WHERE id = ?";
        
        try (PreparedStatement pstmt = mockConnection.prepareStatement(query)) {
            pstmt.setInt(1, 1);  // Check for first user (ID = 1)
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "First user should be present in the database");
                assertEquals("test1@example.com", rs.getString("email"));
                assertEquals("password1", rs.getString("password"));
                assertEquals("admin", rs.getString("role"));
            }
            
            pstmt.setInt(1, 2);  // Check for second user (ID = 2)
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "Second user should be present in the database");
                assertEquals("test2@example.com", rs.getString("email"));
                assertEquals("password2", rs.getString("password"));
                assertEquals("user", rs.getString("role"));
            }
        }
    }
    
    
}
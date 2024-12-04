package edu.asu.ASUHelloWorldJavaFXMaven;
import java.sql.*;

/*******
 * <p> DatabaseHelper Class </p>
 * 
 * <p> Description: This class creates a table and acts as front of the database </p>
 * 
 * 
 * @author Tushar Sachan, Max Neville, Taj Yoshimura, Alan Lintemuth, William McLean
 * 
 * @version 1.0		Development Phase 1 (User authrorization)
 * 
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;


class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/firs";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

    public DatabaseHelper(Connection connection) {
        this.connection = connection;
    }
    
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
	
	/**********
	 * This function creates the table in database
	 */
	private void createTables() throws SQLException {
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
				
		statement.execute(userTable);
	}
	
	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}
	
	/**********
	 * This function gets if the user is logging in for the first time
	 */
	public boolean access(String username) throws SQLException {
	    String query = "SELECT access FROM cse360users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        
	        // Execute the query and get the result
	        ResultSet rs = pstmt.executeQuery();
	        
	        // Check if there's a result
	        if (rs.next()) {
	            // Assuming "access" is a boolean column
	            return rs.getBoolean("access");
	        }
	    }
	    return false;  // If no result is found
	}
		
	/**********
	 * This function gets role of a user 
	 */
	public String role(String username) throws SQLException {
	    String query = "SELECT role FROM cse360users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        
	        // Execute the query and get the result
	        ResultSet rs = pstmt.executeQuery();
	        
	        // Check if there's a result
	        if (rs.next()) {
	            // Assuming "access" is a boolean column
	            return rs.getString("role");
	        }
	    }
	    return "";  // If no result is found
	}
	
	/**********
	 * This function registers a new user with username, password and role
	 *        
	 */
	public void register(String username, String password, String role) throws SQLException {
    	String encryptedpassword = Base64.getEncoder().encodeToString(password.getBytes());
    	String insertUser = "INSERT INTO cse360users (password, role, username, access) VALUES (?, ?, ?, false)";
		
    	System.out.println("encrypted Password: " + encryptedpassword); // Debugging output

		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			
			pstmt.setString(1, encryptedpassword);
			pstmt.setString(2, role);
			pstmt.setString(3, username);
			
			pstmt.executeUpdate();
		}
	}
	/**********
	 * This function sets the temporary password along with expiration date
	 */
	public void invitedata(String role, String temp,String date) throws SQLException {
		String insertUser = "INSERT INTO cse360users (role, temp, date) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			
			
			pstmt.setString(1, role);
			pstmt.setString(2, temp);
			pstmt.setString(3, date);
			
			pstmt.executeUpdate();
			System.out.println("In the system");
		}
	}
	/**********
	 * This function updates the details when user logs in for the first time
	 */
	public void update(String email, String first, String middle, String last, String preferred, String username) throws SQLException {
	    String updateUser = "UPDATE cse360users SET email = ?, first = ?, middle = ?, last = ?, preferred = ?, access = true WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateUser)) {
	        pstmt.setString(1, email);
	        pstmt.setString(2, first);
	        pstmt.setString(3, middle);
	        pstmt.setString(4, last);
	        pstmt.setString(5, preferred);
	        pstmt.setString(6, username);
	        
	        pstmt.executeUpdate();
	    }
	}
	/**********
	 * This functionupdates the user info
	 */
	public void updateuser(String username,String temp) throws SQLException {
	    String updateUser = "UPDATE cse360users SET username = ?, temp = null WHERE temp = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateUser)) {
	        pstmt.setString(2, temp);

	        pstmt.setString(1, username);
	        
	        pstmt.executeUpdate();
	    }
	    System.out.println("updated username");
	}
	/**********
	 * This function resets the user info and remove password
	 */
	public void resetuser(String username,String temp,String date) throws SQLException {
	    String updateUser = "UPDATE cse360users SET password = null, temp = ?, date = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateUser)) {
	        pstmt.setString(2, date);
	        pstmt.setString(3, username);
	        pstmt.setString(1, temp);
	        
	        pstmt.executeUpdate();
	    }
	    System.out.println("reset username successful");
	}
	/**********
	 * This function updates the pass after OTP
	 */
	public void updatepass(String password,String username) throws SQLException {
	    String updateUser = "UPDATE cse360users SET password = ? WHERE USERNAME= ?";
	    String encryptedpassword = Base64.getEncoder().encodeToString(password.getBytes());

	    
	    try (PreparedStatement pstmt = connection.prepareStatement(updateUser)) {
	        pstmt.setString(1, encryptedpassword);

	        pstmt.setString(2, username);
	        
	        pstmt.executeUpdate();
	    }
	    System.out.println("updated password");
	}
	/**********
	 * This function helps setting the role as per the invite
	 */
	public void setrole(String role,String username) throws SQLException {
	    String updateUser = "UPDATE cse360users SET role = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateUser)) {

	        pstmt.setString(1, role);
	        pstmt.setString(2, username);
	        
	        pstmt.executeUpdate();
	    }
	}
	
	/**********
	 * This function checks if the username and password already are in the database
	 */
	public boolean login(String username, String password) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE username = ? AND password = ?";
		String encryptedpassword = Base64.getEncoder().encodeToString(password.getBytes());	
    	System.out.println("encrypted Password: " + encryptedpassword); // Debugging output
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			pstmt.setString(2, encryptedpassword);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	/**********
	 * this gets if the user still has an active OTP
	 */
	public boolean helptemp(String temp) throws SQLException {
		String query = "SELECT temp FROM cse360users WHERE temp = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, temp);

			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	/**********
	 * This function checks if a username is in the database
	 */
	public boolean doesUserExist(String username) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	/**********
	 * This function returns preferred name
	 */
	public String prefname(String username) throws SQLException {
	    String query = "SELECT preferred FROM cse360users WHERE username = ?";
	    String preferredName = null; 

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                preferredName = rs.getString("preferred");
	            }
	        }
	    }

	    return preferredName;
	}
	/**********
	 * This function displays users by admin
	 */
	public void displayUsersByAdmin() throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 

		while(rs.next()) { 
			// Retrieve by column name 
			int id  = rs.getInt("id"); 
			String  email = rs.getString("email"); 
			String password = rs.getString("password"); 
			String role = rs.getString("role");  

			// Display values 
			System.out.print("ID: " + id); 
			System.out.print(", Age: " + email); 
			System.out.print(", First: " + password); 
			System.out.println(", Last: " + role); 
		} 
	}
	
	/**********
	 * This function returns all the users in the database as a list object
	 */
	public String displayUsers() throws SQLException{
		String sql = "SELECT * FROM cse360users"; 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); 
		String s="";

		while(rs.next()) { 
			// Retrieve by column name 
			
			String roles="";
			String  username = rs.getString("username"); 
			String role = rs.getString("role");  
			String  email = rs.getString("email");  
			String first = rs.getString("first");  
			String  middle = rs.getString("middle"); 
			String last = rs.getString("last"); 
			String exp = rs.getString("date"); 
			switch(role) {
			case "111":
		           roles="Admin, Instructor and Student"; 
		            break;
		        case "110":
		        	roles="Admin and Instructor";  
		            break;
		        case "101":
		        	roles="Admin and Student";
		            break;
		        case "100":
		        	roles="Admin";
		            break;
		        case "011":
		        	roles="Instructor and Student";
		            break;
		        case "010":
		        	roles="Instructor";
		            break;
		        case "001":
		        	roles="Student";
		            break;
			}
			s+="----------------------------------------------"+"\n"
					+"Username: "+username+"\n"
					+"First Name: "+first+"\n"
					+"Middle Name: "+middle+"\n"
					+"Last Name: "+last+"\n"
					+"Roles: "+roles+"\n"
					+"Email: "+email+"\n"
					+"Expiration: "+exp+"\n";
			
			 
		} 
		return s;
	}
	
	/**********
	 * This function helps delete a user
	 */
	public void deleteUser(String username) throws SQLException{
		String deleteQuery = "DELETE FROM cse360users WHERE username = ?";
try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
			
			pstmt.setString(1, username);
			
			
			pstmt.executeUpdate();
		}
	    System.out.println("User have been deleted.");
		
		
		} 
	
	

	/**********
	 * This function closes the connection between program and database
	 */
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

}

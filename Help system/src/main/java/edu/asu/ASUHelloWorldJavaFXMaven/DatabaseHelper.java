package edu.asu.ASUHelloWorldJavaFXMaven;
import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


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
	

	public void register(String username, String password, String role) throws SQLException {
		String insertUser = "INSERT INTO cse360users (password, role, username, access) VALUES (?, ?, ?, false)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			
			pstmt.setString(1, password);
			pstmt.setString(2, role);
			pstmt.setString(3, username);
			
			pstmt.executeUpdate();
		}
	}
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
	public void updateuser(String username,String temp) throws SQLException {
	    String updateUser = "UPDATE cse360users SET username = ?, temp = null WHERE temp = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateUser)) {
	        pstmt.setString(2, temp);

	        pstmt.setString(1, username);
	        
	        pstmt.executeUpdate();
	    }
	    System.out.println("updated username");
	}
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
	public void updatepass(String password,String username) throws SQLException {
	    String updateUser = "UPDATE cse360users SET password = ? WHERE USERNAME= ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateUser)) {
	        pstmt.setString(1, password);

	        pstmt.setString(2, username);
	        
	        pstmt.executeUpdate();
	    }
	    System.out.println("updated password");
	}
	public void setrole(String role,String username) throws SQLException {
	    String updateUser = "UPDATE cse360users SET role = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateUser)) {

	        pstmt.setString(1, role);
	        pstmt.setString(2, username);
	        
	        pstmt.executeUpdate();
	    }
	}

	public boolean login(String username, String password) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE username = ? AND password = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	public boolean helptemp(String temp) throws SQLException {
		String query = "SELECT temp FROM cse360users WHERE temp = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, temp);

			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
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
	
	public void deleteUser(String username) throws SQLException{
		String deleteQuery = "DELETE FROM cse360users WHERE username = ?";
try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
			
			pstmt.setString(1, username);
			
			
			pstmt.executeUpdate();
		}
	    System.out.println("User have been deleted.");
		
		
		} 
	public void deleteall() throws SQLException{
		String deleteQuery = "DELETE FROM cse360users";
try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
			
			
			
			pstmt.executeUpdate();
		}
	    System.out.println("User have been deleted.");
		
		
		} 
	


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

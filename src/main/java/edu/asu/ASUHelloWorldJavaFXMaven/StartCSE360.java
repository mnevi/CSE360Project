package edu.asu.ASUHelloWorldJavaFXMaven;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/*******
 * <p> StartCSE360 Class </p>
 * 
 * <p> Description: This class is the front end for the database class and has 
 * necessary functions to handle data </p>
 * 
 * 
 * @author Tushar Sachan, Max Neville, Taj Yoshimura, Alan Lintemuth, William McLean
 * 
 * @version 1.0		Development Phase 1 (User authrorization)
 * 
 */

public class StartCSE360 {
	/**********
	 * This function creates a connection between database and program
	 */
    private static Connection mockConnection;

    // Static block to initialize mockConnection
    static {
        try {
            mockConnection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception (logging, rethrow, etc.)
        }
    }

    private static final DatabaseHelper databaseHelper = new DatabaseHelper(mockConnection);

    public StartCSE360() {
        try {
            databaseHelper.connectToDatabase();  // Connect to the database
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
	
	/**********
	 * This function checks if the database is empty
	 */
	public boolean check() throws SQLException {
		
			if (databaseHelper.isDatabaseEmpty()) {
				System.out.println( "In-Memory Database  is empty" );
				return true;
				}
				return false;
			}
			
		
	
	/**********
	 * This function checks if the user exists in the database
	 */
	public boolean checkuser(String username)
	{
		
		return databaseHelper.doesUserExist(username);
		
		
	}
	/**********
	 * This function sets up the first administrator
	 */
	public int setupAdministrator(String username,String password,String role) throws SQLException {
		
		if(!databaseHelper.doesUserExist(username)) {
			databaseHelper.register(username, password, role);
			System.out.println("Administrator setup completed.");
			return 0;
		}
		return -1;
		
	}
	/**********
	 * This function sets up the new users exceot the first admin
	 */
public int userSetUp(String username,String password,String role) throws SQLException {
		
		if(!databaseHelper.doesUserExist(username)) {
			databaseHelper.register(username, password,role);
			System.out.println("User Setup complete");
			return 0;

		}
		
		return -1;
		
	}
/**********
 * This function returns preferred name
 */
public String prefname(String username) throws SQLException {
	return databaseHelper.prefname(username);
}
/**********
 * This function deletes user from the database
 */
public void delete(String username) throws SQLException {
	
	
	databaseHelper.deleteUser(username);
	System.out.println("Delete complete");

}
/**********
 * This function returns the OTP from the database
 */
public boolean checktemp(String temp) throws SQLException {
	
	
	return databaseHelper.helptemp(temp);
	

}
/**********
 * This function sets up the new password and username
 */
public void updatepass(String password,String username) throws SQLException {
	
	
	databaseHelper.updatepass(password,username);
	

}
/**********
 * This function update username if someone has the right OTP
 */
public void updateuser(String username, String temp) throws SQLException {
	
	
	databaseHelper.updateuser(username,temp);
	

}

/**********
 * This function returns if the person is logging in for the first time
 */
public boolean findaccess(String username) throws SQLException {
	return databaseHelper.access(username);
}
/**********
 * This function returns the current role of users in database
 */
public String findrole(String username) throws SQLException {
	return databaseHelper.role(username);
}
/**********
 * This function sets all the details in the database
 */
public void execute(String email, String first,String middle, String last, String preferred,String username)
{
	try {
		databaseHelper.update(email,first,middle,last,preferred,username);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

/**********
 * This function checks is the person is authorized
 */
	public boolean login(String username, String password) {
		try {
			boolean a= databaseHelper.login(username,password);
			return a;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}
	
	public void inviteroles(String roles,String s,String date) throws SQLException {
		databaseHelper.invitedata(roles,s,date);
	}
	
	public String show() throws SQLException {
		return databaseHelper.displayUsers();
	}
	public void setrole(String role,String username) throws SQLException {
		databaseHelper.setrole(role,username);
	}
	public void resetuser(String username,String s,String date) throws SQLException {
		 databaseHelper.resetuser(username,s,date);
	}

	


}

package edu.asu.ASUHelloWorldJavaFXMaven;

import java.sql.SQLException;


public class StartCSE360 {

	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	StartCSE360(){
try { 
			
			databaseHelper.connectToDatabase();  // Connect to the database

			// Check if the database is empty (no users registered)
			
			
		} catch (SQLException e) {
			System.err.println("Database error: " + e.getMessage());
			e.printStackTrace();
		}
	}
	public boolean check() throws SQLException {
		
			if (databaseHelper.isDatabaseEmpty()) {
				System.out.println( "In-Memory Database  is empty" );
				return true;
				}
				return false;
			}
			
		
	

	public boolean checkuser(String username)
	{
		
		return databaseHelper.doesUserExist(username);
		
		
	}

	public int setupAdministrator(String username,String password,String role) throws SQLException {
		
		if(!databaseHelper.doesUserExist(username)) {
			databaseHelper.register(username, password, role);
			System.out.println("Administrator setup completed.");
			return 0;
		}
		return -1;
		
	}
public int userSetUp(String username,String password,String role) throws SQLException {
		
		if(!databaseHelper.doesUserExist(username)) {
			databaseHelper.register(username, password,role);
			System.out.println("User Setup complete");
			return 0;

		}
		
		return -1;
		
	}
public String prefname(String username) throws SQLException {
	return databaseHelper.prefname(username);
}
public void delete(String username) throws SQLException {
	
	
	databaseHelper.deleteUser(username);
	System.out.println("Delete complete");

}
public void deleteall() throws SQLException {
	
	
	databaseHelper.deleteall();
	System.out.println("Delete complete");

}
public boolean checktemp(String temp) throws SQLException {
	
	
	return databaseHelper.helptemp(temp);
	

}
public void updatepass(String password,String username) throws SQLException {
	
	
	databaseHelper.updatepass(password,username);
	

}
public void updateuser(String username, String temp) throws SQLException {
	
	
	databaseHelper.updateuser(username,temp);
	

}


public boolean findaccess(String username) throws SQLException {
	return databaseHelper.access(username);
}
public String findrole(String username) throws SQLException {
	return databaseHelper.role(username);
}

public void execute(String email, String first,String middle, String last, String preferred,String username)
{
	try {
		databaseHelper.update(email,first,middle,last,preferred,username);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}


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

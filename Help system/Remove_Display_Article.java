	/**	Methods to remove and display encrypted articles in a help system program. First is necessary imports*/

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import org.bouncycastle.util.Arrays;
import Encryption.EncryptionUtils;
	  
	 /** Displaying article call from main, I used a switch case to organize the menus	*/
					
//	case "3": 				//Prints out the Title, simple abstract, extended abs, body and references
		System.out.println("Display an article (enter in the ID number)");
		long articleChoice = scanner.nextLong();
		scanner.nextLine();
		DatabaseHelper.displayArticle(articleChoice);
		System.out.println("");
		break;
		
		 /** Displaying article method from DatabaseHelper.java, makes a method call with the article ID. 
		  *  Uses the title to set the encrypted body, could be switched to the more unique article ID.*/
		
		public void displayArticle(long artID) throws Exception {
			System.out.println("Here is article (ID: " + artID + ")");
		    String query = "SELECT title, body, abstract FROM articles WHERE id = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setLong(1, artID);
		        try (ResultSet rs = pstmt.executeQuery()) {
		            if (rs.next()) {
		                String title = rs.getString("title");
		                String encryptedBody = rs.getString("body");
		                String simpleAbstract = rs.getString("abstract"); 
		                
		                System.out.println("Title: " + title); 
//Not necessary if
//we encrypt abstract   String encryptedExtendedAbstract = rs.getString("extended_abstract"); 
//Also not necessary    System.out.println("Simple Abstract: " + simpleAbstract); 
		                	               
		                byte[] iv = EncryptionUtils.getInitializationVector(title.toCharArray());  //Use title for IV for decryption

		                char[] decryptedAbstract = EncryptionUtils.toCharArray(
		                        encryptionHelper.decrypt(Base64.getDecoder().decode(simpleAbstract), iv)
		                    );
		                }
		                char[] decryptedBody = EncryptionUtils.toCharArray(				    //Decrypt the body
		                    encryptionHelper.decrypt(Base64.getDecoder().decode(encryptedBody), iv)
		                );

//Error Checking if we want to check decryption
//		                System.out.println("Encrypted Technical Abstract: " + simpleAbstract);	//Test of encryption
		                System.out.print("Article Abstract: "); 
		                EncryptionUtils.printCharArray(decryptedAbstract);
		                System.out.println(); 
		                
	                    Arrays.fill(decryptedAbstract, '\0');  // Clear old encrypted data
		                
		                // Display decrypted body
//Error Checking if we want to check decryption
//						System.out.println("Encrypted Body: " + encryptedBody);		//Test of encryption
		                System.out.print("Decrypted Body: "); 
		                EncryptionUtils.printCharArray(decryptedBody);
	                    System.out.println("");

		                Arrays.fill(decryptedBody, '\0');				 // Clear old encrypted data
		        }
		    }
		}
		
		 /** Remove article call from main, I used a switch case to organize the menus	*/
		
//		case "4": 				//Deletes an article by ID. Checks to make sure the user is sure
			System.out.println("Delete an article (enter in ID number)");
			long choiceDelete = scanner.nextLong();
			scanner.nextLine();
			
			System.out.println("Are you sure about deleting article #" + choiceDelete + " Enter Y for Yes");
			
//May be better as a radial button instead of a char entry
			String yesDelete = scanner.nextLine();
				if (yesDelete.equalsIgnoreCase("Y")) {
			        databaseHelper.deleteArticle(choiceDelete);
				}
				else {
					System.out.println("Article Not Deleted.");
				}
				System.out.println("");
				break;
				
	/**	Method for deleting articles from the Article Table. First accepts the article ID, then deletes
	* the correct rows from the articles table. (AL)*/
	public void deleteArticle(long articleId) throws SQLException {
	connectToDatabase();
	String deleteArticle = "DELETE FROM articles WHERE id = ?";		//Database call
	try (PreparedStatement pstmt = connection.prepareStatement(deleteArticle)) {	//Deletes by the article ID
		pstmt.setLong(1, articleId);
		int rowsAffected = pstmt.executeUpdate();
		if (rowsAffected > 0) {
			System.out.println("Article with ID " + articleId + " has been deleted.");
			} else {
			System.out.println("No article found with ID " + articleId + ".");
			}
		}
	}			

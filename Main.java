package simpleDatabase;

import java.util.Scanner;

/*******
* <p> Main Class. </p>
*
* <p> Description: A Java Class for working with the article database </p>
*
* @author Taj Yoshimura
*
* @version 1.00 2024-10-20 works with ArticleDatabaseHelper to run the program
*/

public class Main {
    public static void main(String[] args) {
    	System.out.println("Hello!");
        try {
            ArticleDatabaseHelper dbHelper = new ArticleDatabaseHelper();
            Scanner scanner = new Scanner(System.in);
            int choice;

            do {
            	System.out.println("--------------------------------------------------------");
                System.out.println("1. Add Article");
                System.out.println("2. Delete Article");
                System.out.println("3. List Articles");
                System.out.println("4. Backup Articles");
                System.out.println("5. Restore Articles");
                System.out.println("6. List Articles by group");
                System.out.println("7. Backup Articles by group");
                System.out.println("8. view specific");
                System.out.println("9. merge and restore articles");
                System.out.println("10. delete and restore articles");
                System.out.println("11. create group");
                System.out.println("12. edit group");
                System.out.println("13. delete group");
                System.out.println("14. list groups");
                System.out.println("15. Exit");
                System.out.print("Type the corresponding number for an option: ");
                System.out.println();
                System.out.println("--------------------------------------------------------");
                choice = scanner.nextInt();
                scanner.nextLine(); // get input
                System.out.println("--------------------------------------------------------");
                if (choice == 1) {
                	dbHelper.addArticle();		// adds an article if a "1" is typed
                } else if (choice == 2) {
                	System.out.print("Enter the title of the Article to delete: ");
                    String titleToDelete = scanner.nextLine();
                	dbHelper.deleteArticle(titleToDelete);	// deletes an article if a "2" is typed
                } else if (choice == 3) {
                	dbHelper.listArticles();	// lists each article if a "3" is typed
                } else if (choice == 4) {
                	System.out.print("Enter the filename for backup: ");
                    String filename = scanner.nextLine();
                	dbHelper.backupArticles(filename);	// backs up each article if a "4" is typed
                } else if (choice == 5) {
                	dbHelper.restoreArticles();	// restores articles if a "5" is typed
                } else if (choice == 6) {
                	//dbHelper.listGroupedArticles();	// lists articles by group if a "6" is typed
                } else if (choice == 7) {
                	//System.out.print("Enter the filename for backup: ");
                    //String filename = scanner.nextLine();
                    //System.out.print("Enter the groups (separated by a comma): ");
                    //String backupGroups = scanner.nextLine();
                	//dbHelper.backupGroupedArticles(filename, backupGroups);	// backs up grouped articles if a "7" is typed
                } else if (choice == 8) {
                	System.out.println("Enter a title to search for: ");
                	long id = scanner.nextLong();
                	dbHelper.viewArticle(id);	// backs up grouped articles if a "7" is typed
                } else if (choice == 9) {
                	System.out.println("Enter a file: ");
                	String file = scanner.nextLine();
                	dbHelper.mergeArticles(file);	// backs up grouped articles if a "7" is typed
                } else if (choice == 10) {
                	System.out.println("Enter a file: ");
                	String file = scanner.nextLine();
                	dbHelper.deleteRestoreArticles(file);	// backs up grouped articles if a "7" is typed
                } else if (choice == 11) {
                	System.out.print("Enter the title for group: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter the articles (separated by a comma): ");
                    String articles = scanner.nextLine();
                    System.out.println("Enter 'T' to set true, 'F' to set false:");
                    Boolean specialAccess = null;
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("T")) {
                        specialAccess = true;
                    } else if (input.equalsIgnoreCase("F")) {
                        specialAccess = false;
                    } else {
                        System.out.println("Invalid input! Please enter 'T' or 'F'.");
                    }
                    System.out.print("Enter the admins for group: ");
                    String admins = scanner.nextLine();
                    System.out.print("Enter who has access: ");
                    String access = scanner.nextLine();
                	dbHelper.createGroup(title, articles, specialAccess, admins, access);
                } else if (choice == 12) {
                	//create using UI stuff
                } else if (choice == 13) {
                	System.out.print("Enter the title of the group to delete: ");
                    String titleToDelete = scanner.nextLine();
                	dbHelper.deleteGroup(titleToDelete);
                } else if (choice == 14) {
                	dbHelper.listGroups();
                } else if (choice == 15) {
                	dbHelper.closeConnection();	// ends the program
                	System.out.println("See ya...");
                } else {
                	System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 15);
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}


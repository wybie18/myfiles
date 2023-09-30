package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
	static Connection conn = null;
	static Scanner scan = new Scanner(System.in);
	
	static Menu menu;
	public static void main(String[] args) throws SQLException {
		try{
			String url= "jdbc:mysql://localhost:3306/banking_system";
			String user="root";
			String pass="";
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn=DriverManager.getConnection(url,user,pass);
		}
		catch(Exception e){
		System.out.println("connection() error: " + e.getMessage());
		}
		
		 int choice = 0;
	        while (choice != 4) {
	            System.out.println("+------------------------+");
	            System.out.println("| Login/Register         |");
	            System.out.println("+------------------------+");
	            System.out.println("| 1. Login               |");
	            System.out.println("| 2. Sign-up             |");
	            System.out.println("| 3. Exit                |");
	            System.out.println("+------------------------+");
	            System.out.print("Enter your choice: ");
	            choice = scan.nextInt();
	            scan.nextLine();
	            
	            switch (choice) {
	            	case 1:
	            		Accounts.login(conn);
	            		if(Accounts.asUser) {
	                		menu = new Menu(conn);
	                	}
	            		break;
	                case 2:
	                	Accounts.signup(conn);
	                    break;
	                case 3:
	                	System.out.println("Exiting the application...");
	                    System.out.println("Exited!");
	                    System.exit(0);
	                    break;
	                default:
	                    System.out.println("Invalid choice, please try again.");
	                    break;
	            }
	        }

	}
	
	

}

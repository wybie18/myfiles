package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Accounts {
	
	static Scanner scan = new Scanner(System.in);
	public static int ID;
	public static boolean asUser = false;
	public static boolean login(Connection conn) throws SQLException {
		System.out.println("+======================+");
        System.out.println("| Login to the mBank   |");
        System.out.println("+======================+");
        System.out.print("Enter your email: ");
        String email = scan.nextLine();
        System.out.print("Enter your pincode: ");
        String pincode = scan.nextLine();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM customers WHERE email = ? AND pincode = ?");
        ps.setString(1, email);
        ps.setString(2, pincode);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
        	System.out.println("Login successful! Welcome " + rs.getString("LASTNAME"));
        	ID = rs.getInt("ID");
        	return asUser = true;
         } else {
        	System.out.println("Invalid email or password.");
        	return asUser = false;
         }
	}
	public static void signup(Connection conn) {
		System.out.println("+=========================+");
        System.out.println("| Register to mBank       |");
        System.out.println("+=========================+");
        System.out.print("Enter your last name: ");
        String lName = scan.nextLine();
        System.out.print("Enter your first name: ");
        String fName = scan.nextLine();
        System.out.print("Enter your age: ");
        int age = scan.nextInt();
        scan.nextLine();
        System.out.print("Enter your email: ");
        String email = scan.nextLine();
        System.out.print("Enter your password: ");
        String password = scan.nextLine();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO customers (lastname, firstname, age, email, pincode, balance) VALUES (?, ?, ?, ?, ?, 0)");
            ps.setString(1, lName);
            ps.setString(2, fName);
            ps.setInt(3, age);
            ps.setString(4, email);
            ps.setString(5, password);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Registration successful! You may now login.");
            } else {
                System.out.println("Registration failed. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
	}
}

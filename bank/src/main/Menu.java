package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Scanner;

public class Menu {
	static Scanner scan = new Scanner(System.in);
	Statement stmt = null;
	static boolean isFrozen;
	int id = Accounts.ID;
	public Menu(Connection conn) throws SQLException {
		stmt = conn.createStatement();
		int choice = 0;
		while(Accounts.asUser) {
			System.out.println("+------------------------+");
	        System.out.println("| Welcome                |");
	        System.out.println("+------------------------+");
	        System.out.println("| 1. Show Account Info   |");
	        System.out.println("| 2. Deposit             |");
	        System.out.println("| 3. Withdraw            |");
	        System.out.println("| 4. Balance             |");
	        System.out.println("| 5. Transfer            |");
	        System.out.println("| 6. Transaction history |");
	        System.out.println("| 7. Change pincode      |");
	        System.out.println("| 8. Freeze Account      |");
	        System.out.println("| 9. Logout              |");
	        System.out.println("+------------------------+");
	        System.out.print("Enter your choice: ");
			choice = scan.nextInt();
	        scan.nextLine();
			switch(choice) {
			case 1:
				showAccountInfo(conn, id);
				break;
			case 2:
				deposit(conn, id);
				break;
			case 3:
				withdraw(conn, id);
				break;
			case 4:
				showBalance(stmt, id);
				break;
			case 5:
				transferFunds(conn, id);
				break;
			case 6:
				getTransactionHistory(conn, id);
				break;
			case 7:
				changePin(conn, id);
				break;
			case 8:
				freezeAccount(conn, id);
				break;
			case 9:
				System.out.println("Logged Out");
				Accounts.asUser = false;
				break;
			default:
				System.out.println("***Invalid Choice***");
			}
		}
	}
	public static void showAccountInfo(Connection conn, int userId) {
	    try {
	        String sql = "SELECT * FROM customers WHERE id = ?";
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        stmt.setInt(1, userId);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            System.out.println("Account Information:");
	            System.out.println("ID: " + rs.getInt("id"));
	            System.out.println("Name: " + rs.getString("lastname") + ", " + rs.getString("firstname"));
	            System.out.println("Email: " + rs.getString("email"));
	            System.out.println("Balance: " + rs.getDouble("balance"));
	            System.out.println("Is Frozen: " + rs.getBoolean("is_frozen"));
	        } else {
	            System.out.println("Account not found. Please try again.");
	        }
	    } catch (SQLException e) {
	        System.out.println("Error retrieving account information");
	        e.printStackTrace();
	    }
	}
	public void deposit(Connection conn, int userId) {
		try {
			String sql = "SELECT is_frozen FROM customers WHERE ID = ?";
			PreparedStatement fCheck = conn.prepareStatement(sql);
			fCheck.setInt(1, userId);
			ResultSet fResult = fCheck.executeQuery();
			fResult.next();
			isFrozen = fResult.getBoolean("is_frozen");
			if(isFrozen) {
				System.out.println("Your account is frozen. Deposit not allowed.");
				return;
			}
			
            System.out.print("Enter amount to deposit: ");
            double amount = scan.nextDouble();
            
            if(amount <= 0 || amount >= 1000000000) {
            	System.out.println("Invalid amount!");
            	amount = 0;
            }
            
            PreparedStatement ps = conn.prepareStatement("UPDATE customers SET balance = balance + ? WHERE ID = ?");
            ps.setDouble(1, amount);
            ps.setInt(2, userId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                if(amount <= 0 || amount >= 1000000000) {
                	System.out.println("Deposit failed.");
                }else {
                	System.out.println("Deposit successful.");
                }
            } else {
                System.out.println("Account not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error depositing funds: " + e.getMessage());
        }
	}
	public void withdraw(Connection conn, int userId) {
		 try {	
			 String sql = "SELECT is_frozen FROM customers WHERE ID = ?";
			 PreparedStatement fCheck = conn.prepareStatement(sql);
			 fCheck.setInt(1, userId);
			 ResultSet fResult = fCheck.executeQuery();
			 fResult.next();
			 isFrozen = fResult.getBoolean("is_frozen");
			 if(isFrozen) {
				 System.out.println("Your account is frozen. Withdraw not allowed.");
				 return;
			 }
			 System.out.print("Enter amount to withdraw: ");
			 double amount = scan.nextDouble();
	            
			 if(amount <= 0) {
				 System.out.println("Invalid amount!");
				 return;
	         }
	            
			 PreparedStatement ps = conn.prepareStatement("SELECT balance FROM customers WHERE ID = ?");
			 ps.setInt(1, userId);
			 ResultSet rs = ps.executeQuery();
			 if (rs.next()) {
				 double balance = rs.getDouble("balance");
				 if (balance >= amount) {
					 ps = conn.prepareStatement("UPDATE customers SET balance = balance - ? WHERE ID = ?");
					 ps.setDouble(1, amount);
					 ps.setInt(2, userId);
					 int rows = ps.executeUpdate();
					 if (rows > 0) {
						 if(amount <= 0) {
							 System.out.println("Withdrawal failed.");
						 }else {
							 System.out.println("Withdrawal successful.");
						 }
					 } else {
						 System.out.println("Error withdrawing funds.");
					 }
				 } 
			 }
		 }catch (SQLException e) {
			 System.out.println("Error depositing funds: " + e.getMessage());
		 }
}
	private void showBalance(Statement stmtm, int userId) throws SQLException {
		String balQuery = "SELECT balance FROM customers WHERE ID = \""+ userId +"\"";
        ResultSet bal = stmt.executeQuery(balQuery);

        if (bal.next()) {
        	System.out.println("+-------------------------------+");
            System.out.println("|Balance : " + bal.getDouble("balance")+"                |");
            System.out.println("+-------------------------------+");
        }
        else {
        	System.out.println("Account not found");
        }
	}
	public static void transferFunds(Connection conn, int fromId) {
	    try {
	        conn.setAutoCommit(false);
	        System.out.println("Enter amount to be transferred: ");
	        double amount = scan.nextDouble();
	        System.out.println("Transfer to ID: ");
	        int toId = scan.nextInt();
	        
	        String sql = "SELECT is_frozen FROM customers WHERE ID = ?";
			PreparedStatement fCheck = conn.prepareStatement(sql);
			fCheck.setInt(1, fromId);
			ResultSet fResult = fCheck.executeQuery();
			fResult.next();
			isFrozen = fResult.getBoolean("is_frozen");
	        if (isFrozen) {
	            System.out.println("Transaction failed: your account is frozen");
	            conn.rollback();
	            return;
	        }
	        sql = "SELECT balance, is_frozen FROM customers WHERE id = ?";
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setInt(1, fromId);
	        ResultSet rs = ps.executeQuery();
	        double balance = 0;
	        boolean isFrozenReceiver = false;
	        if (rs.next()) {
	            balance = rs.getDouble("balance");
	            isFrozenReceiver = rs.getBoolean("is_frozen");
	        }
	        if (amount > balance) {
	            System.out.println("Transaction failed: insufficient funds");
	            conn.rollback();
	            return;
	        }
	        if (amount <= 0) {
	            System.out.println("Transaction failed: invalid amount");
	            conn.rollback();
	            return;
	        }
	        if (isFrozenReceiver) {
	            System.out.println("Transaction failed: the receiver account is frozen");
	            conn.rollback();
	            return;
	        }

	        sql = "UPDATE customers SET balance = balance - ? WHERE id = ?";
	        PreparedStatement stmt1 = conn.prepareStatement(sql);
	        stmt1.setDouble(1, amount);
	        stmt1.setInt(2, fromId);
	        int rows1 = stmt1.executeUpdate();
	        sql = "UPDATE customers SET balance = balance + ? WHERE id = ?";
	        PreparedStatement stmt2 = conn.prepareStatement(sql);
	        stmt2.setDouble(1, amount);
	        stmt2.setInt(2, toId);
	        int rows2 = stmt2.executeUpdate();
	        if (rows1 > 0 && rows2 > 0) {
	            System.out.println("Transaction successful");
	            // insert a new row into the transactions table
	            sql = "INSERT INTO transactions (from_id, to_id, amount) VALUES (?, ?, ?)";
	            PreparedStatement stmt3 = conn.prepareStatement(sql);
	            stmt3.setInt(1, fromId);
	            stmt3.setInt(2, toId);
	            stmt3.setDouble(3, amount);
	            stmt3.executeUpdate();
	            conn.commit();
	        } else {
	            System.out.println("Transaction failed");
	            conn.rollback();
	        }
	    } catch (SQLException e) {
	        System.out.println("Error transferring funds");
	        e.printStackTrace();
	    }
	}
	public static void getTransactionHistory(Connection conn, int userId) {
	    try {
	        String sql = "SELECT t.*, c1.lastname AS from_name, c2.lastname AS to_name FROM transactions t " +
	                "INNER JOIN customers c1 ON t.from_id = c1.id " +
	                "INNER JOIN customers c2 ON t.to_id = c2.id " +
	                "WHERE t.from_id = ? OR t.to_id = ? " +
	                "ORDER BY t.date_time DESC";
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        stmt.setInt(1, userId);
	        stmt.setInt(2, userId);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            int id = rs.getInt("id");
	            int fromId = rs.getInt("from_id");
	            int toId = rs.getInt("to_id");
	            double amount = rs.getDouble("amount");
	            String fromName = rs.getString("from_name");
	            String toName = rs.getString("to_name");
	            Timestamp dateTime = rs.getTimestamp("date_time");
	            String type;
	            if (fromId == userId) {
	                type = "Debit";
	            } else {
	                type = "Credit";
	            }
	            System.out.println("ID: " + id + ", Type: " + type + ", From: " + fromName +
	                    ", To: " + toName + ", Amount: " + amount + ", Date/Time: " + dateTime);
	        }	
	    } catch (SQLException e) {
	        System.out.println("Error getting transaction history");
	        e.printStackTrace();
	    }
	}
	public void changePin(Connection conn, int userId) {
	    try {
	        System.out.print("Enter your current pincode: ");
	        String currentPin = scan.nextLine();

	        // Check if current pin matches with database
	        PreparedStatement ps = conn.prepareStatement("SELECT * FROM customers WHERE ID = ? AND pincode = ?");
	        ps.setInt(1, userId);
	        ps.setString(2, currentPin);
	        ResultSet rs = ps.executeQuery();
	        if (!rs.next()) {
	            System.out.println("Incorrect current pincode!");
	            return;
	        }

	        // Ask user for new pin code and update database
	        System.out.print("Enter your new pincode: ");
	        String newPin = scan.nextLine();
	        ps = conn.prepareStatement("UPDATE customers SET pincode = ? WHERE ID = ?");
	        ps.setString(1, newPin);
	        ps.setInt(2, userId);
	        int rows = ps.executeUpdate();
	        if (rows > 0) {
	            System.out.println("Pincode changed successfully.");
	        } else {
	            System.out.println("Error changing pincode.");
	        }
	    } catch (SQLException e) {
	        System.out.println("Error changing pincode: " + e.getMessage());
	    }
	}
	public static void freezeAccount(Connection conn, int userId) {
	    try {
	        System.out.print("Enter your pincode: ");
	        String pincode = scan.nextLine();
	        
	        String sql = "SELECT COUNT(*) FROM customers WHERE ID = ? AND pincode = ?";
	        PreparedStatement checkPincodeStmt = conn.prepareStatement(sql);
	        checkPincodeStmt.setInt(1, userId);
	        checkPincodeStmt.setString(2, pincode);
	        ResultSet pincodeResult = checkPincodeStmt.executeQuery();
	        int count = 0;
	        if (pincodeResult.next()) {
	            count = pincodeResult.getInt(1);
	        }
	        if (count == 0) {
	            System.out.println("Incorrect pincode. Account cannot be frozen/unfrozen.");
	            return;
	        }
	        
	        System.out.println("Enter 'f' to freeze account or 'u' to unfreeze account: ");
	        String option = scan.next();
	        
	        if (option.equals("f")) {
	            isFrozen = true;
	        } else if (option.equals("u")) {
	            isFrozen = false;
	        } else {
	            System.out.println("Invalid option. Please try again.");
	            return;
	        }
	        sql = "UPDATE customers SET is_frozen = ? WHERE ID = ?";
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        stmt.setBoolean(1, isFrozen);
	        stmt.setInt(2, userId);
	        int rows = stmt.executeUpdate();
	        if (rows > 0) {
	            if (isFrozen) {
	                System.out.println("Your account has been frozen successfully");
	            } else {
	                System.out.println("Your account has been unfrozen successfully");
	            }
	        } else {
	            System.out.println("Failed to freeze/unfreeze account. Please try again.");
	        }
	    } catch (SQLException e) {
	        System.out.println("Error freezing/unfreezing account");
	        e.printStackTrace();
	    }
	}
}

import java.sql.*;
import java.util.Scanner;

public class Main {
    // Database connection details
    private static final String jdbcUrl = "jdbc:mysql://localhost:3306/giveTake";
    private static final String username = "root";
    private static final String password = "********";

    public static void main(String[] args) {
        // Load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found: " + e.getMessage());
            return; // Exit if driver is not found
        }

        // Establish database connection and perform transaction
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            connection.setAutoCommit(false); // Begin transaction

            // SQL update queries
            String queryCredit = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
            String queryDebit = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
            PreparedStatement preparedStatementCredit = connection.prepareStatement(queryCredit);
            PreparedStatement preparedStatementDebit = connection.prepareStatement(queryDebit);

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the amount: ");
            double amount = scanner.nextDouble();
            System.out.print("Enter the account number to credit: ");
            int account1 = scanner.nextInt();
            System.out.print("Enter your account number: ");
            int account2 = scanner.nextInt();

            // Set parameters for credit and debit operations



            // Check if the source account has sufficient balance
            if (isSufficient(connection, account2, amount)) {
                preparedStatementCredit.setDouble(1, amount);
                preparedStatementCredit.setInt(2, account1);
                preparedStatementDebit.setDouble(1, amount);
                preparedStatementDebit.setInt(2, account2);

                preparedStatementCredit.executeUpdate();
                preparedStatementDebit.executeUpdate();

                // Execute update statements

                // Commit the transaction
                connection.commit();
                System.out.println("Transaction successful");
            } else {
                System.out.println("Insufficient balance");
                connection.rollback(); // Rollback transaction if balance is insufficient
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static boolean isSufficient(Connection connection, int accountNumber, double amount) {
        String query = "SELECT balance FROM accounts WHERE account_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, accountNumber);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    double currentBalance = resultSet.getDouble("balance");
                    return currentBalance >= amount;
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return false;
    }
}

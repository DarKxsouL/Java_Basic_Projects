package Tasks.FinalTask;

import java.util.Scanner;

public class BankAccount {
    // Private fields for encapsulation
    private String accountNumber;
    private String accountHolder;
    private double balance;

    // Constructor
    public BankAccount(String accountNumber, String accountHolder, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
    }

    // Deposit method
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("₹" + amount + " deposited successfully. New balance: ₹" + balance);
        } else {
            System.out.println("Deposit amount must be greater than zero.");
        }
    }

    // Withdraw method
    public void withdraw(double amount) {
        if (amount > 0) {
            if (balance >= amount) {
                balance -= amount;
                System.out.println("₹" + amount + " withdrawn successfully. New balance: ₹" + balance);
            } else {
                System.out.println("Insufficient balance.");
            }
        } else {
            System.out.println("Withdrawal amount must be greater than zero.");
        }
    }

    // View balance method
    public void viewBalance() {
        System.out.println("Current balance: ₹" + balance);
    }

    // Main method for user interaction
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Create an account dynamically
        System.out.println("Welcome to the Bank!");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter account holder name: ");
        String accountHolder = scanner.nextLine();
        System.out.print("Enter initial balance: ");
        double initialBalance = scanner.nextDouble();

        BankAccount account = new BankAccount(accountNumber, accountHolder, initialBalance);

        boolean exit = false;

        // Menu for user operations
        while (!exit) {
            System.out.println("\nChoose an operation:");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. View Balance");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1: // Deposit
                    System.out.print("Enter amount to deposit: ");
                    double depositAmount = scanner.nextDouble();
                    account.deposit(depositAmount);
                    break;

                case 2: // Withdraw
                    System.out.print("Enter amount to withdraw: ");
                    double withdrawAmount = scanner.nextDouble();
                    account.withdraw(withdrawAmount);
                    break;

                case 3: // View Balance
                    account.viewBalance();
                    break;

                case 4: // Exit
                    exit = true;
                    System.out.println("Thank you for using our services!");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }
}

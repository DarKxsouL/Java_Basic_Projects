import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BankAccountGUI {

    private String accountNumber;
    private String accountHolder;
    private double balance;

    private JFrame frame;
    private JLabel balanceLabel;
    private JTextField amountField;

    public BankAccountGUI(String accountNumber, String accountHolder, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = initialBalance;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Bank Account - " + accountHolder);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(6, 1));

        JLabel welcomeLabel = new JLabel("Welcome, " + accountHolder, JLabel.CENTER);
        balanceLabel = new JLabel("Current Balance: ₹" + balance, JLabel.CENTER);
        amountField = new JTextField();

        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton exitButton = new JButton("Exit");

        depositButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                deposit(amount);
            } catch (NumberFormatException ex) {
                showError("Please enter a valid amount.");
            }
        });

        withdrawButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                withdraw(amount);
            } catch (NumberFormatException ex) {
                showError("Please enter a valid amount.");
            }
        });

        exitButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Thank you for using our services!");
            System.exit(0);
        });

        frame.add(welcomeLabel);
        frame.add(balanceLabel);
        frame.add(new JLabel("Enter Amount:", JLabel.CENTER));
        frame.add(amountField);
        frame.add(depositButton);
        frame.add(withdrawButton);
        frame.add(exitButton);

        frame.setVisible(true);
    }

    private void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            updateBalance();
            showInfo("₹" + amount + " deposited successfully.");
        } else {
            showError("Deposit amount must be greater than zero.");
        }
    }

    private void withdraw(double amount) {
        if (amount > 0) {
            if (balance >= amount) {
                balance -= amount;
                updateBalance();
                showInfo("₹" + amount + " withdrawn successfully.");
            } else {
                showError("Insufficient balance.");
            }
        } else {
            showError("Withdrawal amount must be greater than zero.");
        }
    }

    private void updateBalance() {
        balanceLabel.setText("Current Balance: ₹" + balance);
        amountField.setText("");
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(frame, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JTextField accNumField = new JTextField();
            JTextField accHolderField = new JTextField();
            JTextField initBalanceField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Enter Account Number:"));
            panel.add(accNumField);
            panel.add(new JLabel("Enter Account Holder Name:"));
            panel.add(accHolderField);
            panel.add(new JLabel("Enter Initial Balance:"));
            panel.add(initBalanceField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Create Account", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String accNum = accNumField.getText();
                    String accHolder = accHolderField.getText();
                    double initBal = Double.parseDouble(initBalanceField.getText());

                    if (accNum.isEmpty() || accHolder.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    new BankAccountGUI(accNum, accHolder, initBal);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid initial balance!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}

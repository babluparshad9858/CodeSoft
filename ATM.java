import java.util.ArrayList;
import java.util.Scanner;

// Class to represent the user's bank account
class BankAccount {
    private double balance;
    private String pin;
    private ArrayList<String> transactionHistory;
    private String securityAnswer; // For PIN reset

    public BankAccount(double initialBalance, String pin, String securityAnswer) {
        this.balance = Math.max(initialBalance, 0);
        this.pin = pin;
        this.securityAnswer = securityAnswer.toLowerCase();
        this.transactionHistory = new ArrayList<>();
    }

    // Authenticate user
    public boolean authenticate(String inputPin) {
        return this.pin.equals(inputPin);
    }

    // Reset PIN after security verification
    public boolean resetPIN(String answer, String newPIN) {
        if (this.securityAnswer.equals(answer.toLowerCase())) {
            this.pin = newPIN;
            return true;
        } else {
            return false;
        }
    }

    // Deposit money
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactionHistory.add(String.format("Deposited: $%.2f", amount));
            System.out.printf("✅ Successfully deposited: $%.2f\n", amount);
        } else {
            System.out.println("❌ Deposit amount must be greater than zero.");
        }
    }

    // Withdraw money
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("❌ Withdrawal amount must be greater than zero.");
        } else if (amount > balance) {
            System.out.println("❌ Insufficient balance for this transaction.");
        } else {
            balance -= amount;
            transactionHistory.add(String.format("Withdrawn: $%.2f", amount));
            System.out.printf("✅ Successfully withdrawn: $%.2f\n", amount);
        }
    }

    // Check balance
    public void checkBalance() {
        System.out.printf("💰 Current balance: $%.2f\n", balance);
    }

    // Show transaction history
    public void showTransactionHistory() {
        if (transactionHistory.isEmpty()) {
            System.out.println("📄 No transactions yet.");
        } else {
            System.out.println("📄 Transaction History:");
            for (String record : transactionHistory) {
                System.out.println(" - " + record);
            }
        }
    }
}

// ATM class
public class ATM {
    private BankAccount account;
    private Scanner sc;

    public ATM(BankAccount account) {
        this.account = account;
        sc = new Scanner(System.in);
    }

    // Start ATM interface
    public void start() {
        System.out.println("===================================");
        System.out.println("       🏦 Welcome to ATM Machine");
        System.out.println("===================================");

        // Authenticate user with PIN
        boolean authenticated = false;
        int pinAttempts = 0;

        while (!authenticated && pinAttempts < 3) {
            System.out.print("Enter your 4-digit PIN (or type 'forgot' to reset PIN): ");
            String input = sc.next();

            if (input.equalsIgnoreCase("forgot")) {
                handleForgotPIN();
                continue; // Retry PIN authentication after reset
            }

            if (account.authenticate(input)) {
                authenticated = true;
            } else {
                pinAttempts++;
                System.out.println("❌ Incorrect PIN. Attempts left: " + (3 - pinAttempts));
            }
        }

        if (!authenticated) {
            System.out.println("❌ Too many incorrect attempts. Exiting...");
            return;
        }

        // Main menu
        boolean exit = false;
        while (!exit) {
            System.out.println("\n===============================");
            System.out.println("Please choose an option:");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transaction History");
            System.out.println("5. Exit");
            System.out.print("Enter your choice (1-5): ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    account.checkBalance();
                    break;
                case 2:
                    System.out.print("Enter amount to deposit: $");
                    double depositAmount = sc.nextDouble();
                    account.deposit(depositAmount);
                    break;
                case 3:
                    System.out.print("Enter amount to withdraw: $");
                    double withdrawAmount = sc.nextDouble();
                    account.withdraw(withdrawAmount);
                    break;
                case 4:
                    account.showTransactionHistory();
                    break;
                case 5:
                    System.out.println("👋 Thank you for using the ATM. Goodbye!");
                    exit = true;
                    break;
                default:
                    System.out.println("❌ Invalid option. Please enter 1-5.");
            }
        }
    }

    // Handle PIN reset
    private void handleForgotPIN() {
        System.out.print("To reset your PIN, answer the security question.\nWhat is your favorite color? ");
        String answer = sc.next();

        String newPIN;
        while (true) {
            System.out.print("Enter a new 4-digit PIN: ");
            newPIN = sc.next();
            if (newPIN.matches("\\d{4}")) break;
            System.out.println("❌ Invalid PIN! PIN must be exactly 4 digits.");
        }

        if (account.resetPIN(answer, newPIN)) {
            System.out.println("✅ PIN reset successful! You can now log in with the new PIN.");
        } else {
            System.out.println("❌ Security answer incorrect. PIN reset failed.");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Set initial PIN and security question
        System.out.println("===================================");
        System.out.println("       🏦 Welcome to ATM Machine");
        System.out.println("===================================");

        String pin;
        while (true) {
            System.out.print("Set your 4-digit PIN: ");
            pin = sc.next();
            if (pin.matches("\\d{4}")) break;
            System.out.println("❌ Invalid PIN! PIN must be exactly 4 digits.");
        }

        System.out.print("Set answer for security question (What is your favorite color?): ");
        String securityAnswer = sc.next();

        // Create bank account with $1000 initial balance
        BankAccount myAccount = new BankAccount(1000.0, pin, securityAnswer);

        ATM atm = new ATM(myAccount);
        atm.start();

        sc.close();
    }
}

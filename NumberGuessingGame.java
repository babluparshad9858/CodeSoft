import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random random = new Random();

        int score = 0; 
        boolean playAgain = true;

        System.out.println("===================================");
        System.out.println(" Welcome to Number Guessing Game");
        System.out.println("===================================");

        while (playAgain) {
            int lowerBound = 1;
            int upperBound = 100;
            int numberToGuess = random.nextInt(upperBound - lowerBound + 1) + lowerBound;

            int maxAttempts = 7; 
            boolean guessedCorrectly = false;

            System.out.println("\nI have selected a number between " + lowerBound + " and " + upperBound + ".");
            System.out.println("You have " + maxAttempts + " attempts. Good luck!");

            
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                System.out.println("Attempt " + attempt + " - Enter your guess: ");
                int userGuess = sc.nextInt();

                if (userGuess == numberToGuess) {
                    System.out.println(" Congratulations! You guessed the correct number!");
                    guessedCorrectly = true;
                    score++;
                    break;
                } else if (userGuess < numberToGuess) {
                    System.out.println(" Too low!");
                } else {
                    System.out.println(" Too high!");
                }

                System.out.println("Attempts left: " + (maxAttempts - attempt));
            }

            if (!guessedCorrectly) {
                System.out.println("Sorry, you're out of attempts. The number was: " + numberToGuess);
            }

            System.out.println("Current Score: " + score);

            System.out.println("\nDo you want to play another round? (yes/no): ");
            String response = sc.next();
            playAgain = response.equalsIgnoreCase("yes");
        }

        System.out.println("\n===================================");
        System.out.println(" Final Score: " + score);
        System.out.println("Thanks for playing. Goodbye!");
        System.out.println("===================================");

        sc.close();
    }
}

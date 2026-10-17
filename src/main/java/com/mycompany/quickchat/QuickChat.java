package com.mycompany.quickchat;
 
import java.util.Scanner;
 
/**

* QuickChat console application.

* Part 1: register and log in. Part 2: send messages from a numeric menu.

*/

public class QuickChat {
 
    private static final int MENU_SEND_MESSAGES = 1;

    private static final int MENU_RECENT_MESSAGES = 2;

    private static final int MENU_QUIT = 3;
 
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        Login login = new Login();
 
        register(scanner, login);
 
        // Messages can only be sent after a successful login

        if (!logIn(scanner, login)) {

            scanner.close();

            return;

        }
 
        System.out.println();

        System.out.println("Welcome to QuickChat.");
 
        int numberOfMessages = readNumber(scanner,

                "How many messages do you want to enter? ", 1, 1000);

        int messagesEntered = 0;
 
        int menuChoice = 0;

        while (menuChoice != MENU_QUIT) {

            System.out.println();

            System.out.println("1) Send Messages");

            System.out.println("2) Show recently sent messages");

            System.out.println("3) Quit");

            menuChoice = readNumber(scanner, "Choose an option: ", 1, 3);
 
            if (menuChoice == MENU_SEND_MESSAGES) {

                if (messagesEntered >= numberOfMessages) {

                    System.out.println("You have already entered all " + numberOfMessages + " messages.");

                } else {

                    messagesEntered = sendMessages(scanner, messagesEntered, numberOfMessages);

                }

            } else if (menuChoice == MENU_RECENT_MESSAGES) {

                System.out.println("Coming Soon.");

            }

        }
 
        System.out.println("Goodbye!");

        scanner.close();

    }
 
    /** Keeps asking for registration details until the registration is successful. */

    private static void register(Scanner scanner, Login login) {

        System.out.println("=== User Registration ===");

        boolean registered = false;
 
        while (!registered) {

            System.out.print("Enter a username (must contain an underscore, max 5 characters): ");

            String username = scanner.nextLine();

            System.out.print("Enter a password (min 8 characters, capital letter, number, special character): ");

            String password = scanner.nextLine();

            System.out.print("Enter your cell phone number (e.g. +27831234567): ");

            String cellPhoneNumber = scanner.nextLine();

            System.out.print("Enter your first name: ");

            String firstName = scanner.nextLine();

            System.out.print("Enter your last name: ");

            String lastName = scanner.nextLine();
 
            String result = login.registerUser(username, password, cellPhoneNumber, firstName, lastName);

            System.out.println(result);

            registered = result.contains("User registered successfully.");

        }

    }
 
    /** Gives the user three attempts to log in. Returns true if they succeed. */

    private static boolean logIn(Scanner scanner, Login login) {

        System.out.println();

        System.out.println("=== Login ===");
 
        for (int attempt = 1; attempt <= 3; attempt++) {

            System.out.print("Enter your username: ");

            String username = scanner.nextLine();

            System.out.print("Enter your password: ");

            String password = scanner.nextLine();
 
            boolean loginSuccessful = login.loginUser(username, password);

            System.out.println(login.returnLoginStatus(loginSuccessful));
 
            if (loginSuccessful) {

                return true;

            }

        }
 
        System.out.println("Too many failed attempts. Exiting.");

        return false;

    }
 
    /**

     * Lets the user enter the rest of their messages (for loop).

     * Returns how many messages have been entered so far.

     */

    private static int sendMessages(Scanner scanner, int messagesEntered, int numberOfMessages) {

        for (int i = messagesEntered; i < numberOfMessages; i++) {

            System.out.println();

            System.out.println("--- Message " + (i + 1) + " of " + numberOfMessages + " ---");
 
            String recipient = readRecipient(scanner, i);

            String text = readMessageText(scanner, recipient, i);
 
            Message message = new Message(Message.generateMessageId(), i, recipient, text);

            System.out.println(message.getMessageIdStatus());
 
            handleSendChoice(scanner, message);

            messagesEntered++;

        }
 
        System.out.println();

        System.out.println("Total number of messages sent: " + Message.returnTotalMessages());

        return messagesEntered;

    }
 
    /** Asks for the recipient until a correctly formatted number is entered. */

    private static String readRecipient(Scanner scanner, int messageNumber) {

        while (true) {

            System.out.print("Enter the recipient's cell phone number: ");

            String recipient = scanner.nextLine();
 
            // A temporary message is used only to check the number

            Message check = new Message("0000000000", messageNumber, recipient, "check");

            String result = check.checkRecipientCell();

            System.out.println(result);
 
            if (result.equals("Cell phone number successfully captured.")) {

                return recipient;

            }

        }

    }
 
    /** Asks for the message until it is not longer than 250 characters. */

    private static String readMessageText(Scanner scanner, String recipient, int messageNumber) {

        while (true) {

            System.out.print("Enter your message (max 250 characters): ");

            String text = scanner.nextLine();
 
            if (text.trim().isEmpty()) {

                System.out.println("The message cannot be empty.");

                continue;

            }
 
            Message check = new Message("0000000000", messageNumber, recipient, text);

            String result = check.checkMessageLength();

            System.out.println(result);
 
            if (result.equals("Message ready to send.")) {

                return text;

            }

        }

    }
 
    /** Asks whether to send, disregard or store the message, and acts on the answer. */

    private static void handleSendChoice(Scanner scanner, Message message) {

        boolean finished = false;
 
        while (!finished) {

            System.out.println("1) Send Message");

            System.out.println("2) Disregard Message");

            System.out.println("3) Store Message to send later");

            int choice = readNumber(scanner, "Choose an option: ", 1, 3);
 
            System.out.println(message.sentMessage(choice));
 
            if (choice == Message.SEND) {

                System.out.println();

                System.out.println(message.getDetails());

                finished = true;

            } else if (choice == Message.STORE) {

                finished = true;

            } else if (choice == Message.DISREGARD) {

                // The message is only deleted if the user presses 0

                String answer = scanner.nextLine().trim();

                if (answer.equals("0")) {

                    System.out.println("Message deleted.");

                    finished = true;

                }

            }

        }

    }
 
    /** Reads a whole number between min and max, asking again if the input is not valid. */

    private static int readNumber(Scanner scanner, String prompt, int min, int max) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            try {

                int number = Integer.parseInt(input);

                if (number >= min && number <= max) {

                    return number;

                }

            } catch (NumberFormatException e) {

                // fall through to the error message below

            }

            System.out.println("Please enter a number between " + min + " and " + max + ".");

        }

    }

}
 
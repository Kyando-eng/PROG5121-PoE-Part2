package com.mycompany.quickchat;
 
import com.google.gson.Gson;

import com.google.gson.GsonBuilder;

import com.google.gson.reflect.TypeToken;
 
import java.io.FileReader;

import java.io.FileWriter;

import java.io.IOException;

import java.lang.reflect.Type;

import java.util.ArrayList;

import java.util.List;

import java.util.Random;
 
/**

* One chat message in QuickChat.

* Holds the message details and the methods that check, hash, send and store it.

*/

public class Message {
 
    // Menu choices after a message has been written

    public static final int SEND = 1;

    public static final int DISREGARD = 2;

    public static final int STORE = 3;
 
    public static final int MAX_MESSAGE_LENGTH = 250;

    public static final String DEFAULT_STORAGE_FILE = "messages.json";
 
    // Shared by every message while the program is running

    private static final List<Message> sentMessages = new ArrayList<>();

    private static int totalMessagesSent = 0;
 
    private final String messageId;

    private final int messageNumber;

    private final String recipient;

    private final String text;

    private final String messageHash;
 
    public Message(String messageId, int messageNumber, String recipient, String text) {

        this.messageId = messageId;

        this.messageNumber = messageNumber;

        this.recipient = recipient;

        this.text = text;

        this.messageHash = createMessageHash();

    }
 
    /**

     * Builds a random ten-digit message ID by joining ten random digits.

     * Leading zeros are allowed, so the ID is a String, not an int.

     */

    public static String generateMessageId() {

        Random random = new Random();

        StringBuilder id = new StringBuilder();

        for (int i = 0; i < 10; i++) {

            id.append(random.nextInt(10));

        }

        return id.toString();

    }
 
    /** Returns true if the message ID is not empty and no more than ten characters. */

    public boolean checkMessageID() {

        return messageId != null && !messageId.isEmpty() && messageId.length() <= 10;

    }
 
    /** Text shown to the user once the ID has been created. */

    public String getMessageIdStatus() {

        return "Message ID generated: " + messageId;

    }
 
    /**

     * Checks the recipient number: international code (+) followed by the number.

     * Re-uses the regex checker written in Part 1 (Login.checkCellPhoneNumber).

     */

    public String checkRecipientCell() {

        Login login = new Login();

        if (login.checkCellPhoneNumber(recipient)) {

            return "Cell phone number successfully captured.";

        }

        return "Cell phone number is incorrectly formatted or does not contain an "

                + "international code. Please correct the number and try again.";

    }
 
    /** Checks that the message is not longer than 250 characters. */

    public String checkMessageLength() {

        if (text.length() <= MAX_MESSAGE_LENGTH) {

            return "Message ready to send.";

        }

        int extraCharacters = text.length() - MAX_MESSAGE_LENGTH;

        return "Message exceeds 250 characters by " + extraCharacters

                + "; please reduce the size.";

    }
 
    /**

     * Creates the hash: first two digits of the ID, a colon, the message number,

     * a colon, then the first and last word of the message. All in capitals.

     * Example: 00:0:HITONIGHT

     */

    public String createMessageHash() {

        String firstTwoDigits = messageId.substring(0, 2);
 
        String[] words = text.trim().split("\\s+");

        String firstWord = cleanWord(words[0]);

        String lastWord = cleanWord(words[words.length - 1]);
 
        return (firstTwoDigits + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();

    }
 
    /** Removes punctuation, e.g. "tonight?" becomes "tonight". */

    private String cleanWord(String word) {

        return word.replaceAll("[^A-Za-z0-9]", "");

    }
 
    /**

     * Lets the user send, disregard or store the message.

     * Stored messages go to the default JSON file.

     */

    public String sentMessage(int choice) {

        return sentMessage(choice, DEFAULT_STORAGE_FILE);

    }
 
    /** Same as above, but the JSON file can be chosen (used by the unit tests). */

    public String sentMessage(int choice, String storageFile) {

        switch (choice) {

            case SEND:

                sentMessages.add(this);

                totalMessagesSent++;

                return "Message successfully sent.";

            case DISREGARD:

                return "Press 0 to delete the message.";

            case STORE:

                storeMessage(storageFile);

                return "Message successfully stored.";

            default:

                return "Invalid option. Please choose 1, 2 or 3.";

        }

    }
    /**
     * The full details of this message, in the order the task asks for.
     */

    public String getDetails() {

        return "Message ID: " + messageId + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + text;

    }

    /**
     * Returns the details of every message sent while the program has been
     * running.
     */
    public static String printMessages() {

        StringBuilder allMessages = new StringBuilder();

        for (Message sent : sentMessages) {

            allMessages.append(sent.getDetails()).append("\n\n");

        }

        return allMessages.toString();

    }

    /**
     * Returns how many messages have been sent.
     */
    public static int returnTotalMessages() {

        return totalMessagesSent;

    }

    /**
     * Clears the sent messages (used by the unit tests so they do not affect
     * each other).
     */
    public static void resetSentMessages() {

        sentMessages.clear();

        totalMessagesSent = 0;

    }

    /**
     *
     * Saves this message to a JSON file. Messages already in the file are kept.
     *
     * Gson usage follows the Gson User Guide:
     *
     * Google (2023) Gson User Guide. Available at:
     *
     * https://github.com/google/gson/blob/main/UserGuide.md (Accessed: add your
     * access date).
     *
     */
    public void storeMessage(String storageFile) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<Message> storedMessages = readStoredMessages(storageFile);

        storedMessages.add(this);

        try (FileWriter writer = new FileWriter(storageFile)) {

            gson.toJson(storedMessages, writer);

        } catch (IOException e) {

            System.out.println("Could not store the message: " + e.getMessage());

        }

    }

    /**
     * Reads the messages already saved in the JSON file (an empty list if there
     * is no file yet).
     */
    public static List<Message> readStoredMessages(String storageFile) {

        Gson gson = new Gson();

        Type listType = new TypeToken<List<Message>>() {
        }.getType();

        try (FileReader reader = new FileReader(storageFile)) {

            List<Message> messages = gson.fromJson(reader, listType);

            return messages == null ? new ArrayList<>() : messages;

        } catch (IOException e) {

            return new ArrayList<>();

        }

    }

    public String getMessageId() {

        return messageId;

    }

    public String getMessageHash() {

        return messageHash;

    }

    public String getRecipient() {

        return recipient;

    }

    public String getText() {

        return text;

    }

}

 
package com.mycompany.quickchat;
 
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.io.TempDir;
 
import java.nio.file.Path;

import java.util.List;
 
import static org.junit.jupiter.api.Assertions.*;
 
public class MessageTest {
 
    // Test data from the task: Message 1 (send) and Message 2 (discard)

    private static final String RECIPIENT_1 = "+27718693002";

    private static final String TEXT_1 = "Hi Mike, can you join us for dinner tonight?";

    private static final String RECIPIENT_2 = "08575975889";

    private static final String TEXT_2 = "Hi Keegan, did you receive the payment?";
 
    private Message message1;

    private Message message2;
 
    @TempDir

    Path tempDir;
 
    @BeforeEach

    public void setUp() {

        Message.resetSentMessages();

        // IDs start with 00 so the hash matches the expected value 00:0:HITONIGHT

        message1 = new Message("0012345678", 0, RECIPIENT_1, TEXT_1);

        message2 = new Message("0098765432", 1, RECIPIENT_2, TEXT_2);

    }
 
    // ---------- Message length ----------
 
    @Test

    public void testMessageLength_Success() {

        assertEquals("Message ready to send.", message1.checkMessageLength());

    }
 
    @Test

    public void testMessageLength_Failure() {

        String longText = "a".repeat(260);

        Message tooLong = new Message("0012345678", 0, RECIPIENT_1, longText);

        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",

                tooLong.checkMessageLength());

    }
 
    @Test

    public void testMessageLength_Exactly250IsAllowed() {

        Message exact = new Message("0012345678", 0, RECIPIENT_1, "a".repeat(250));

        assertEquals("Message ready to send.", exact.checkMessageLength());

    }
 
    // ---------- Recipient number ----------
 
    @Test

    public void testRecipientCell_Success() {

        assertEquals("Cell phone number successfully captured.", message1.checkRecipientCell());

    }
 
    @Test

    public void testRecipientCell_Failure() {

        assertEquals("Cell phone number is incorrectly formatted or does not contain an "

                + "international code. Please correct the number and try again.",

                message2.checkRecipientCell());

    }
 
    // ---------- Message hash ----------
 
    @Test

    public void testMessageHash_Message1() {

        assertEquals("00:0:HITONIGHT", message1.createMessageHash());

    }
 
    @Test

    public void testMessageHash_AllMessagesInLoop() {

        Message[] messages = { message1, message2 };

        String[] expectedHashes = { "00:0:HITONIGHT", "00:1:HIPAYMENT" };
 
        for (int i = 0; i < messages.length; i++) {

            assertEquals(expectedHashes[i], messages[i].createMessageHash());

        }

    }
 

    // ---------- Message ID ----------
 
    @Test

    public void testMessageId_GeneratedMessage() {

        assertEquals("Message ID generated: 0012345678", message1.getMessageIdStatus());

    }
 
    @Test

    public void testMessageId_RandomIdIsTenDigits() {

        for (int i = 0; i < 50; i++) {

            String id = Message.generateMessageId();

            assertEquals(10, id.length());

            assertTrue(id.matches("\\d{10}"));

        }

    }
 
    @Test

    public void testCheckMessageId_Valid() {

        assertTrue(message1.checkMessageID());

    }
 
    @Test

    public void testCheckMessageId_TooLong() {

        Message badId = new Message("12345678901", 0, RECIPIENT_1, TEXT_1);

        assertFalse(badId.checkMessageID());

    }
 
    // ---------- Send / disregard / store ----------
 
    @Test

    public void testSentMessage_Send() {

        assertEquals("Message successfully sent.", message1.sentMessage(Message.SEND));

    }
 
    @Test

    public void testSentMessage_Disregard() {

        assertEquals("Press 0 to delete the message.", message1.sentMessage(Message.DISREGARD));

    }
 
    @Test

    public void testSentMessage_Store() {

        String file = tempDir.resolve("messages.json").toString();

        assertEquals("Message successfully stored.", message1.sentMessage(Message.STORE, file));

    }
 
    @Test

    public void testSentMessage_InvalidChoice() {

        assertEquals("Invalid option. Please choose 1, 2 or 3.", message1.sentMessage(9));

    }
 
    // ---------- Totals and printing ----------
 
    @Test

    public void testReturnTotalMessages_OneSentOneDiscarded() {

        message1.sentMessage(Message.SEND);

        message2.sentMessage(Message.DISREGARD);

        assertEquals(1, Message.returnTotalMessages());

    }
 
    @Test

    public void testPrintMessages_ShowsSentMessageOnly() {

        message1.sentMessage(Message.SEND);

        message2.sentMessage(Message.DISREGARD);
 
        String printed = Message.printMessages();

        assertTrue(printed.contains("Message ID: 0012345678"));

        assertTrue(printed.contains("Message Hash: 00:0:HITONIGHT"));

        assertTrue(printed.contains("Recipient: +27718693002"));

        assertTrue(printed.contains("Message: " + TEXT_1));

        assertFalse(printed.contains("Keegan"));

    }
 
    @Test

    public void testGetDetails_InCorrectOrder() {

        String expected = "Message ID: 0012345678\n"

                + "Message Hash: 00:0:HITONIGHT\n"

                + "Recipient: +27718693002\n"

                + "Message: " + TEXT_1;

        assertEquals(expected, message1.getDetails());

    }
 
    // ---------- JSON storage ----------
 
    @Test

    public void testStoreMessage_SavesAndKeepsEarlierMessages() {

        String file = tempDir.resolve("stored.json").toString();
 
        message1.storeMessage(file);

        message2.storeMessage(file);
 
        List<Message> stored = Message.readStoredMessages(file);

        assertEquals(2, stored.size());

        assertEquals(TEXT_1, stored.get(0).getText());

        assertEquals("00:1:HIPAYMENT", stored.get(1).getMessageHash());

    }
 
    @Test

    public void testReadStoredMessages_NoFileGivesEmptyList() {

        String file = tempDir.resolve("missing.json").toString();

        assertTrue(Message.readStoredMessages(file).isEmpty());

    }

}
 
package com.chat.e2e;

import java.io.PrintStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Scanner;

public class NetworkManager
{
    //TODO: Test text message encryption while sending and receiving from server
    final private static boolean USE_ENCRYPTION = true;

    private static boolean busy = false;
    private static boolean connected = false;
    private static boolean loggedIn = false;
    private static Socket serverConnection;
    private static Scanner serverIncomingStream;
    private static PrintStream serverOutgoingStream;
    private static Thread probingThread;

    private static Thread connectionMakerThread;

    synchronized public static boolean isBusy()
    {
        return busy;
    }

    synchronized public static boolean isConnected()
    {
        return connected;
    }

    synchronized public static boolean isLoggedIn()
    {
        return loggedIn;
    }
    synchronized public static void setBusy(boolean busy)
    {
        NetworkManager.busy = busy;
    }

    synchronized public static void setLoggedIn(boolean loggedIn)
    {
        NetworkManager.loggedIn = loggedIn;
    }

    synchronized public static Socket getConnectionSocket()
    {
        return serverConnection;
    }

    static
    {
        connectionMakerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    while(true)
                    {
                        if(!connected)
                            connectToServer();
                        Thread.sleep(Long.parseLong(ConfigManager.getConnectionProbeTimePeriod()) * 1000);
                    }
                }
                catch (Exception e)
                {

                }
            }
        });
        connectionMakerThread.start();
    }

    synchronized public static boolean connectToServer()
    {
        if(connected)
            return true;
        try {
            if(ConfigManager.getClientPortAddressType().equals("Dynamic"))
                serverConnection = new Socket(ConfigManager.getServerIpAddress(), Integer.parseInt(ConfigManager.getServerPortAddress()));
            else if(ConfigManager.getClientPortAddressType().equals("Custom"))
                serverConnection = new Socket(ConfigManager.getServerIpAddress(), Integer.parseInt(ConfigManager.getServerPortAddress()), null, Integer.parseInt(ConfigManager.getClientPortAddress()));
            connected = true;
            loggedIn = false;
            serverIncomingStream = new Scanner(serverConnection.getInputStream());
            serverOutgoingStream = new PrintStream(serverConnection.getOutputStream());

            probingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //System.out.println("Probe thread: started");
                    Thread replyThread = null;
                    while(true)
                    {
                        try{
                            //System.out.println("Probe thread: sleeping for 5 seconds");
                            Thread.sleep(Long.parseLong(ConfigManager.getConnectionProbeTimePeriod()) * 1000);
                            //System.out.println("Probe thread: sleep complete");
                            if(busy) {
                                //System.out.println("Probe thread: network busy");
                                if(replyThread != null && replyThread.isAlive())
                                {
                                    replyThread.interrupt();
                                }
                                continue;
                            }
                            if(!connected){
                                //System.out.println("Probe thread: not connected");
                                if(replyThread != null && replyThread.isAlive())
                                {
                                    replyThread.interrupt();
                                }
                                break;
                            }

                            if(replyThread != null && replyThread.isAlive())
                            {
                                //System.out.println("Probe thread: Reply not received in time");
                                replyThread.interrupt();
                                if(serverConnection != null)
                                    serverConnection.close();
                                connected = false;
                                busy = false;
                                loggedIn = false;
                                serverIncomingStream = null;
                                serverOutgoingStream = null;
                                break;
                            }

                            //System.out.println("Probe thread: probe sent");
                            boolean success = sendToServer("PROBE");
                            if(!success)
                            {
                                //System.out.println("Probe thread: probe failed to send");
                                if(replyThread != null && replyThread.isAlive())
                                {
                                    replyThread.interrupt();
                                }
                                if(serverConnection != null)
                                    serverConnection.close();
                                connected = false;
                                busy = false;
                                loggedIn = false;
                                serverIncomingStream = null;
                                serverOutgoingStream = null;
                                break;
                            }

                            //System.out.println("Probe thread: Reply thread started");
                            replyThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //System.out.println("Reply thread: started");
                                    String receivedInfo = receiveFromServer();
                                    try {
                                        if (receivedInfo == null)
                                            Thread.sleep(Long.parseLong(ConfigManager.getConnectionProbeTimePeriod()) * 1000 * 2);
                                    }
                                    catch (Exception e)
                                    {

                                    }
                                    //if(receivedInfo != null)
                                        //System.out.println("Reply thread: reply received");
                                    //System.out.println("Reply thread: exiting");
                                }
                            });
                            replyThread.start();
                        }
                        catch(Exception e)
                        {

                        }
                    }
                    //System.out.println("Probe thread: exiting");
                }
            });
            probingThread.start();
        }
        catch(Exception e)
        {
            //System.out.println(e);
            return false;
        }
        return true;
    }

    synchronized public static boolean disconnectFromServer(boolean forAppClosing)
    {
        if(!connected) {
            if(forAppClosing)
            {
                connectionMakerThread.interrupt();
            }
            return true;
        }
        try
        {
            if(probingThread != null && probingThread.isAlive())
            {
                probingThread.interrupt();
            }
            if(serverConnection != null)
                serverConnection.close();
            connected = false;
            loggedIn = false;
            serverIncomingStream = null;
            serverOutgoingStream = null;

            if(forAppClosing)
            {
                connectionMakerThread.interrupt();
            }
        }
        catch(Exception e)
        {
            return false;
        }
        return true;
    }

    synchronized public static boolean sendToServer(String message)
    {
        if(!connected)
            return false;
        try {
            serverOutgoingStream.writeBytes((message + "\n").getBytes());
        }
        catch(Exception e)
        {
            return false;
        }
        return true;
    }

    synchronized public static String receiveFromServer()
    {
        String reply;
        if(!connected)
            return null;
        try
        {
            reply = serverIncomingStream.nextLine();
        }
        catch(Exception e)
        {
            return null;
        }
        return reply;
    }

    synchronized public static Boolean registerNewUser(String username, String password, String displayName, String securityQs1, String securityAns1, String securityQs2, String securityAns2, String securityQs3, String securityAns3)
    {
        if(!connected)
            return null;
        boolean success = sendToServer("RGSTUSER");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("USERNAME " + username);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("PASSWORD " + password);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("DSPLNAME " + displayName);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        if(securityQs1 != null)
        {
            success = sendToServer("SECQS " + securityQs1);
            if(!success)
                return null;
            reply = receiveFromServer();
            if(reply == null)
                return null;
            if(!reply.equals("OK"))
                return false;

            success = sendToServer("SECANS " + securityAns1);
            if(!success)
                return null;
            reply = receiveFromServer();
            if(reply == null)
                return null;
            if(!reply.equals("OK"))
                return false;
        }

        if(securityQs2 != null)
        {
            success = sendToServer("SECQS " + securityQs2);
            if(!success)
                return null;
            reply = receiveFromServer();
            if(reply == null)
                return null;
            if(!reply.equals("OK"))
                return false;

            success = sendToServer("SECANS " + securityAns2);
            if(!success)
                return null;
            reply = receiveFromServer();
            if(reply == null)
                return null;
            if(!reply.equals("OK"))
                return false;
        }

        if(securityQs3 != null)
        {
            success = sendToServer("SECQS " + securityQs3);
            if(!success)
                return null;
            reply = receiveFromServer();
            if(reply == null)
                return null;
            if(!reply.equals("OK"))
                return false;

            success = sendToServer("SECANS " + securityAns3);
            if(!success)
                return null;
            reply = receiveFromServer();
            if(reply == null)
                return null;
            if(!reply.equals("OK"))
                return false;
        }

        success = sendToServer("DONE");
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        return true;
    }

    synchronized public static Boolean checkUsernameAvailability(String username)
    {
        if(!connected)
            return null;
        boolean success = sendToServer("USRAVAIL " + username);
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("OK"))
            return true;
        else
            return false;
    }

    synchronized public static Boolean loginToNewSession(String username, String password, boolean stayLoggedIn)
    {
        if(!connected)
            return null;

        if(loggedIn)
            return true;

        boolean success = sendToServer("LOGINNEW");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("USERNAME " + username);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("PASSWORD " + password);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("STAYLGIN " + stayLoggedIn);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("DONE");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return false;
        DatabaseManager.closeDB();
        reply = reply.substring(6);
        ConfigManager.setAccountID(reply);
        success = sendToServer("OK");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return false;
        reply = reply.substring(9);
        ConfigManager.setAccountUsername(reply);
        success = sendToServer("OK");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return false;
        reply = reply.substring(9);
        ConfigManager.setAccountDisplayName(reply);
        success = sendToServer("OK");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return false;
        reply = reply.substring(9);
        ConfigManager.setAllowNewPersonalChat(reply);
        success = sendToServer("OK");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return false;
        reply = reply.substring(9);
        ConfigManager.setAllowNewGroupChat(reply);
        success = sendToServer("OK");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return false;
        //TODO: server should return session id as "null" if stay logged in is false, it shouldn't generate any session id for a single login session
        // this is to make the app see that session id is null, so the next time it starts, it won't try to login automatically
        // it will also reset account details back to default
        reply = reply.substring(9);
        ConfigManager.setAccountSessionID(reply);
        success = sendToServer("OK");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("DONE"))
            return false;
        success = sendToServer("OK");
        if(!success)
            return null;

        DatabaseManager.initializeDB();
        loggedIn = true;
        return true;
    }

    synchronized public static String[] retrieveSecurityQuestions(String username)
    {
        if(!connected)
            return null;
        boolean success = sendToServer("GETSECQS " + username);
        if(!success)
            return null;

        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return new String[0];
        reply = reply.substring(7);
        String[] securityQuestions = new String[Integer.parseInt(reply)];
        success = sendToServer("OK");
        if(!success)
            return null;

        for(int i = 0; i < securityQuestions.length; i++)
        {
            reply = receiveFromServer();
            if(reply == null)
                return null;
            if(reply.equals("ERROR"))
                return new String[0];
            reply = reply.substring(7);
            securityQuestions[i] = reply;
            success = sendToServer("OK");
            if(!success)
                return null;
        }

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("DONE"))
            return new String[0];
        success = sendToServer("OK");
        if(!success)
            return null;

        return securityQuestions;
    }

    synchronized public static Boolean checkSecurityQsAnswer(String username, String question, String answer)
    {
        if(!connected)
            return null;
        boolean success = sendToServer("CHKSCANS");
        if(!success)
            return null;

        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;
        success = sendToServer("USERNAME " + username);
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;
        success = sendToServer("QUESTION " + question);
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;
        success = sendToServer("ANSWER " + answer);
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;
        success = sendToServer("DONE");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        return true;
    }

    synchronized public static Boolean resetPassword(String username, String newPassword)
    {
        if(!connected)
            return null;
        boolean success = sendToServer("RESETPW");
        if(!success)
            return null;

        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;
        success = sendToServer("USERNAME " + username);
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;
        success = sendToServer("PASSWORD " + newPassword);
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;
        success = sendToServer("DONE");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        return true;
    }

    synchronized public static Boolean loginToOldSession(String accountID, String sessionID)
    {
        if(!connected || busy)
            return null;
        if(loggedIn)
            return true;

        boolean success = sendToServer("LOGINOLD");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("ACCID " + accountID);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("SESIONID " + sessionID);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("DONE");
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        DatabaseManager.initializeDB();
        loggedIn = true;
        return true;
    }

    synchronized public static Boolean reloginToTemporarySession(String accountID)
    {
        if(!connected || busy)
            return null;
        if(loggedIn)
            return true;

        boolean success = sendToServer("RELOGTMP");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("ACCID " + accountID);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("DONE");
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        DatabaseManager.initializeDB();
        loggedIn = true;
        return true;
    }

    synchronized public static Boolean checkForNewChats()
    {
        if(!connected || busy)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("CHKNEWCH");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        success = sendToServer("OK");
        if(!success)
            return null;
        reply = reply.substring(7);
        int numberOfNewChats = Integer.parseInt(reply);
        if(numberOfNewChats == 0)
            return false;

        for(int i = 0; i < numberOfNewChats; i++)
        {
            reply = receiveFromServer();
            if(reply == null)
                return null;
            String chatID = reply.substring(7);
            success = sendToServer("OK");
            if(!success)
                return null;

            reply = receiveFromServer();
            if(reply == null)
                return null;
            String chatType = reply.substring(9);
            success = sendToServer("OK");
            if(!success)
                return null;

            reply = receiveFromServer();
            if(reply == null)
                return null;
            String chatName = reply.substring(9);
            success = sendToServer("OK");
            if(!success)
                return null;

            reply = receiveFromServer();
            if(reply == null)
                return null;
            String numberOfParticipants = reply.substring(9);
            success = sendToServer("OK");
            if(!success)
                return null;

            String[][] participantsList = new String[Integer.parseInt(numberOfParticipants)][2];
            for(int j = 0; j < Integer.parseInt(numberOfParticipants); j++)
            {
                reply = receiveFromServer();
                if(reply == null)
                    return null;
                participantsList[j][0] = reply.substring(6);
                success = sendToServer("OK");
                if(!success)
                    return null;

                reply = receiveFromServer();
                if(reply == null)
                    return null;
                participantsList[j][1] = reply.substring(8);
                success = sendToServer("OK");
                if(!success)
                    return null;
            }
            String participants = "";
            for(int j = 0; j < participantsList.length; j++)
            {
                participants = participants + participantsList[j][0] + ",";
            }
            participants = participants.substring(0, participants.length() - 1);

            //TODO: Find a way to generate and synchronize chat key and iv either locally or over the network
            String chatKey = "";
            String chatIV = "";
            if(chatType.equals("PERSONAL"))
            {
                chatKey = EncryptionManager.generateDeterministicKeyForPersonalChats(ConfigManager.getAccountID(), participantsList[0][0]);
                chatIV = EncryptionManager.generateDeterministicInitializationVectorForPersonalChats(ConfigManager.getAccountID(), participantsList[0][0]);
            }
            else if(chatType.equals("GROUP"))
            {
                chatKey = EncryptionManager.generateDeterministicKeyForGroupChats(chatName, chatID);
                chatIV = EncryptionManager.generateDeterministicInitializationVectorForGroupChats(chatName, chatID);
            }

            //TODO: Test how database reacts for inserting into both chat and savedUsers when the chat's ID or the user's ID is already there in the database
            DatabaseManager.makeUpdate("insert into chat values(" + chatID + ", ?, ?, ?, ?, ?, 0);", new boolean[]{false, false, false, false, false}, chatKey, chatIV, chatType, chatName, participants);

            // TODO: CHANGE MESSAGE TABLE SCHEMA HERE AS WELL AS IN DATABASE MANAGER
            DatabaseManager.makeUpdate("create table chat" + chatID + "(message_id number(64) primary key not null, from_account_id char(16) not null, sent_to_server integer(2) not null, message_timestamp number(64) not null, message_type varchar(16) not null, message_content varchar(1048576) not null);", null);

            for(int j = 0; j < participantsList.length; j++)
            {
                String ID = participantsList[j][0];
                String name = participantsList[j][1];
                DatabaseManager.makeUpdate("insert into savedUsers values(?, ?);", new boolean[]{false, false}, ID, name);
            }
        }

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("DONE"))
            return false;
        success = sendToServer("OK");
        if(!success)
            return null;

        //DatabaseManager.initializeDB();

        return true;
    }

    synchronized public static Boolean checkForNewMessages()
    {
        if(!connected || busy)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("CHKNWMSG");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        success = sendToServer("OK");
        if(!success)
            return null;
        reply = reply.substring(7);
        int numberOfNewMessages = Integer.parseInt(reply);
        if(numberOfNewMessages == 0)
            return false;

        for(int i = 0; i < numberOfNewMessages; i++)
        {
            reply = receiveFromServer();
            if(reply == null)
                return null;
            String chatID = reply.substring(7);
            success = sendToServer("OK");
            if(!success)
                return null;

            reply = receiveFromServer();
            if(reply == null)
                return null;
            String messageID = reply.substring(6);
            success = sendToServer("OK");
            if(!success)
                return null;

            reply = receiveFromServer();
            if(reply == null)
                return null;
            String senderAccountID = reply.substring(9);
            success = sendToServer("OK");
            if(!success)
                return null;

            reply = receiveFromServer();
            if(reply == null)
                return null;
            String messageTimestamp = reply.substring(8);
            success = sendToServer("OK");
            if(!success)
                return null;

            reply = receiveFromServer();
            if(reply == null)
                return null;
            String messageType = reply.substring(8);
            success = sendToServer("OK");
            if(!success)
                return null;

            String message = "";
            String documentSize = "0";
            if(messageType.equals("TEXT"))
            {
                reply = receiveFromServer();
                if(reply == null)
                    return null;
                message = reply.substring(8);
                //TODO: For text messages, decrypt it right after receiving it from server. Since text messages won't take much
                // time to decrypt, it won't keep the server waiting for the reply from client and any further communications.
                // But for file type messages, it might take long time to decrypt depending on the size of the file, so find some
                // alternative way to not slow down the communications between the client and the server. One possible way is to
                // store the encrypted file in that format initially and to start a new separate thread which decrypts the file
                // later on. The message receiver thread itself may be used for the decrypting. A queue may be associated with
                // the thread, and when a received file needs to be decrypted, it should be added to that queue. The receiver
                // thread periodically checks the queue and decrypts any message if found.
                if(USE_ENCRYPTION && !senderAccountID.equals("0000000000000000"))
                {
                    String[][] chatInfo = DatabaseManager.makeQuery("select encryption_key, encryption_IV from chat where chat_id = " + chatID + ";", null);
                    if(chatInfo != null && chatInfo.length == 1)
                        message = EncryptionManager.decryptText(message, chatInfo[0][0], chatInfo[0][1]);
                }
                success = sendToServer("OK");
                if(!success)
                    return null;
            }
            else if(messageType.equals("DOCUMENT"))
            {
                //TODO: Get file from different functions which does byte transfer
                reply = receiveFromServer();
                if(reply == null)
                    return null;
                message = reply.substring(8);
                String originalMessage = new String(message);
                if(USE_ENCRYPTION && !senderAccountID.equals("0000000000000000"))
                {
                    String[][] chatInfo = DatabaseManager.makeQuery("select encryption_key, encryption_IV from chat where chat_id = " + chatID + ";", null);
                    if(chatInfo != null && chatInfo.length == 1)
                        message = EncryptionManager.decryptText(message, chatInfo[0][0], chatInfo[0][1]);
                }
                if(!message.equals(originalMessage)) {
                    documentSize = message.substring(message.indexOf('|') + 1);
                    message = message.substring(0, message.indexOf('|'));
                }
                success = sendToServer("OK");
                if(!success)
                    return null;
            }

            //TODO: Test how database reacts with properly updating the chat last message timestamp

            if(!DatabaseManager.checkIfTableExists("chat" + chatID))
            {
                DatabaseManager.makeUpdate("insert into uncategorizedChatMessages values(" + chatID + ", " + messageID + ", ?, 2, " + messageTimestamp + ", ?, ?);", new boolean[]{false, false, false}, senderAccountID, messageType, message);
                if(messageType.equals("DOCUMENT"))
                {
                    DatabaseManager.makeUpdate("insert into mediaMessagesDetails values(" + chatID + ", " + messageID + ", ?);", new boolean[]{false}, documentSize);
                }
                DatabaseManager.makeUpdate("insert into unknownChats values(" + chatID + ");", null);
                continue;
            }

            if((new BigInteger(DatabaseManager.makeQuery("select last_message_timestamp from chat where chat_id = " + chatID + ";", null)[0][0])).compareTo(new BigInteger(messageTimestamp)) == -1)
            {
                DatabaseManager.makeUpdate("update chat set last_message_timestamp = " + messageTimestamp + " where chat_id = " + chatID + ";", null);
            }

            DatabaseManager.makeUpdate("insert into chat" + chatID + " values(" + messageID + ", ?, 2, " + messageTimestamp + ", ?, ?);", new boolean[]{false, false, false}, senderAccountID, messageType, message);
            if(messageType.equals("DOCUMENT"))
            {
                DatabaseManager.makeUpdate("insert into mediaMessagesDetails values(" + chatID + ", " + messageID + ", ?);", new boolean[]{false}, documentSize);
            }
        }

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("DONE"))
            return false;
        success = sendToServer("OK");
        if(!success)
            return null;

        return true;
    }

    synchronized public static Boolean logoutUser()
    {
        DatabaseManager.closeDB();
        if(!connected || busy)
            return null;
        if(!loggedIn)
            return false;

        boolean success = sendToServer("LOGOUT");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("DONE");
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        loggedIn = false;
        return true;
    }

    //TODO: Add network routines for updating chat and messages info from server if required
    // Also add another two routines for sending new chat and messages to server
    // Add threads in chat main panel for continuously checking for chat and message updates from server
    // and for sending new chat and message info to server

    synchronized public static Boolean sendNewMessage(String chatID, String messageID, String messageType, String messageContent)
    {
        if(!connected || busy)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("SENDMSG");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("CHATID " + chatID);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("TMPMSGID " + messageID);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("MSGTYPE " + messageType);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        //TODO: For text messages, encrypt it just before sending it to the server. Since text messages won't take much time to
        // encrypt, it wouldn't slow down the communications between the client and the server, and wouldn't keep the server
        // waiting for further replies from the client.
        // But for file type messages, encrypt it beforehand, that is, before the communication to send a message is initiated
        // with the server. This way, since encrypting the file type message might take a long time depending on the size of
        // the file, it won't keep the server waiting and possibly create some errors due to it. One possible solution is to
        // make the message sender thread first encrypt the file before calling the network manager routine.
        if(messageType.equals("TEXT")) {
            if(USE_ENCRYPTION)
            {
                String[][] chatInfo = DatabaseManager.makeQuery("select encryption_key, encryption_IV from chat where chat_id = " + chatID + ";", null);
                if(chatInfo != null && chatInfo.length == 1)
                    messageContent = EncryptionManager.encryptText(messageContent, chatInfo[0][0], chatInfo[0][1]);
            }
            success = sendToServer("CONTENT " + messageContent);
            if (!success)
                return null;
            reply = receiveFromServer();
            if (reply == null)
                return null;
            if (!reply.equals("OK"))
                return false;
        }
        else if(messageType.equals("DOCUMENT"))
        {
            //TODO: Add functionality to send other types of messages
            if(USE_ENCRYPTION)
            {
                String[][] chatInfo = DatabaseManager.makeQuery("select encryption_key, encryption_IV from chat where chat_id = " + chatID + ";", null);
                if(chatInfo != null && chatInfo.length == 1)
                    messageContent = EncryptionManager.encryptText(messageContent, chatInfo[0][0], chatInfo[0][1]);
            }
            success = sendToServer("CONTENT " + messageContent);
            if (!success)
                return null;
            reply = receiveFromServer();
            if (reply == null)
                return null;
            if (!reply.equals("OK"))
                return false;
        }

        success = sendToServer("DONE");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return false;
        String newMessageID = reply.substring(9);
        success = sendToServer("OK");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return false;
        String newMessageTimestamp = reply.substring(8);
        success = sendToServer("OK");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("DONE"))
            return false;
        success = sendToServer("OK");
        if(!success)
            return null;

        DatabaseManager.makeUpdate("update chat" + chatID + " set message_id = " + newMessageID + ", sent_to_server = 1, message_timestamp = " + newMessageTimestamp + " where message_id = " + messageID + ";", null);
        if((new BigInteger(DatabaseManager.makeQuery("select last_message_timestamp from chat where chat_id = " + chatID + ";", null)[0][0])).compareTo(new BigInteger(newMessageTimestamp)) == -1)
        {
            DatabaseManager.makeUpdate("update chat set last_message_timestamp = " + newMessageTimestamp + " where chat_id = " + chatID + ";", null);
        }
        DatabaseManager.makeUpdate("update mediaMessagesDetails set message_id = " + newMessageID + " where chat_id = " + chatID + " and message_id = " + messageID + ";", null);

        return true;
    }

    synchronized public static Boolean createNewPersonalChat(String chatType, String chatParticipant)
    {
        if(!connected)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("CREATPCH");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("CHATTYPE " + chatType);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("PPLACCID " + chatParticipant);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("DONE");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return false;
        String chatID = reply.substring(7);
        success = sendToServer("OK");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return false;
        String chatName = reply.substring(9);
        success = sendToServer("OK");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("DONE"))
            return false;
        success = sendToServer("OK");
        if(!success)
            return null;

        String encryptionKey = EncryptionManager.generateDeterministicKeyForPersonalChats(ConfigManager.getAccountID(), chatParticipant);
        String encryptionIV = EncryptionManager.generateDeterministicInitializationVectorForPersonalChats(ConfigManager.getAccountID(), chatParticipant);

        DatabaseManager.makeUpdate("insert into chat values(" + chatID + ", ?, ?, ?, ?, ?, 0);", new boolean[]{false, false, false, false, false}, encryptionKey, encryptionIV, chatType, chatName, chatParticipant);
        // TODO: CHANGE MESSAGE TABLE SCHEMA HERE AS WELL AS IN DATABASE MANAGER
        DatabaseManager.makeUpdate("create table chat" + chatID + "(message_id number(64) primary key not null, from_account_id char(16) not null, sent_to_server integer(2) not null, message_timestamp number(64) not null, message_type varchar(16) not null, message_content varchar(1048576) not null);", null);
        DatabaseManager.makeUpdate("insert into savedUsers values(?, ?);", new boolean[]{false, false}, chatParticipant, chatName);
        //DatabaseManager.initializeDB();

        return true;
    }

    synchronized public static Boolean checkAccountIdValid(String accountID)
    {
        if(!connected)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("CHKACCID " + accountID);
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        return true;
    }

    synchronized public static Boolean updateAllowNewPersonalChat(boolean newValue)
    {
        if(!connected)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("SETNPCAL " + newValue);
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        ConfigManager.setAllowNewPersonalChat(String.valueOf(newValue));

        return true;
    }

    synchronized public static Boolean updateAllowNewGroupChat(boolean newValue)
    {
        if(!connected)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("SETNGCAL " + newValue);
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        ConfigManager.setAllowNewGroupChat(String.valueOf(newValue));

        return true;
    }

    synchronized public static Boolean checkIfNewPersonalChatAllowed(String accountID)
    {
        if(!connected)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("CHKNPCAL " + accountID);
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        return true;
    }

    synchronized public static Boolean checkIfNewGroupChatAllowed(String accountID)
    {
        if(!connected)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("CHKNGCAL " + accountID);
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        return true;
    }

    synchronized public static Boolean changePassword(String oldPassword, String newPassword)
    {
        if(!connected)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("CHNGPW");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("OLDPW " + oldPassword);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("NEWPW " + newPassword);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        return true;
    }

    synchronized public static Boolean changeDisplayName(String displayName)
    {
        if(!connected)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("CHGDSPNM");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("NEWDSPNM " + displayName);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        ConfigManager.setAccountDisplayName(displayName);

        return true;
    }

    synchronized public static Boolean checkForNewReadReceipts()
    {
        if(!connected || busy)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("CHKNWRDR");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        success = sendToServer("OK");
        if(!success)
            return null;
        reply = reply.substring(7);
        int numberOfNewReadReceipts = Integer.parseInt(reply);
        if(numberOfNewReadReceipts == 0)
            return false;

        for(int i = 0; i < numberOfNewReadReceipts; i++)
        {
            reply = receiveFromServer();
            if(reply == null)
                return null;
            String chatID = reply.substring(7);
            success = sendToServer("OK");
            if(!success)
                return null;

            reply = receiveFromServer();
            if(reply == null)
                return null;
            String messageID = reply.substring(6);
            success = sendToServer("OK");
            if(!success)
                return null;

            reply = receiveFromServer();
            if(reply == null)
                return null;
            reply = reply.substring(8);
            int newReadState = Integer.parseInt(reply);
            success = sendToServer("OK");
            if(!success)
                return null;

            DatabaseManager.makeUpdate("update chat" + chatID + " set sent_to_server = " + newReadState + " where message_id = " + messageID + ";", null);
        }

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("DONE"))
            return false;
        success = sendToServer("OK");
        if(!success)
            return null;

        return true;
    }

    synchronized public static Boolean sendNewReadReceipts(String chatID, String messageID)
    {
        if(!connected || busy)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("SENDNRDR");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("CHATID " + chatID);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("MSGID " + messageID);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("DONE");
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        return true;
    }

    synchronized public static Boolean checkIfUserIsOnline(String accountID)
    {
        if(!connected)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("CHKONL " + accountID);
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        return true;
    }

    synchronized public static Boolean createNewGroupChat(String chatType, String chatName, String chatParticipantsList)
    {
        if(!connected)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("CREATGCH");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("CHATTYPE " + chatType);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("CHATNAME " + chatName);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("PPLACCID " + chatParticipantsList);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("DONE");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return false;
        String chatID = reply.substring(7);
        success = sendToServer("OK");
        if(!success)
            return null;

        String[] otherChatParticipantsID = chatParticipantsList.split(",");
        String[] otherChatParticipantsDisplayName = new String[otherChatParticipantsID.length];
        for(int i = 0; i < otherChatParticipantsID.length; i++)
        {
            reply = receiveFromServer();
            if(reply == null)
                return null;
            if(reply.equals("ERROR"))
                return false;
            otherChatParticipantsDisplayName[i] = reply.substring(9);
            success = sendToServer("OK");
            if(!success)
                return null;
        }

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("DONE"))
            return false;
        success = sendToServer("OK");
        if(!success)
            return null;

        String encryptionKey = EncryptionManager.generateDeterministicKeyForGroupChats(chatName, chatID);
        String encryptionIV = EncryptionManager.generateDeterministicInitializationVectorForGroupChats(chatName, chatID);

        DatabaseManager.makeUpdate("insert into chat values(" + chatID + ", ?, ?, ?, ?, ?, 0);", new boolean[]{false, false, false, false, false}, encryptionKey, encryptionIV, chatType, chatName, chatParticipantsList);
        // TODO: CHANGE MESSAGE TABLE SCHEMA HERE AS WELL AS IN DATABASE MANAGER
        DatabaseManager.makeUpdate("create table chat" + chatID + "(message_id number(64) primary key not null, from_account_id char(16) not null, sent_to_server integer(2) not null, message_timestamp number(64) not null, message_type varchar(16) not null, message_content varchar(1048576) not null);", null);
        for(int i = 0; i < otherChatParticipantsID.length; i++)
        {
            DatabaseManager.makeUpdate("insert into savedUsers values(?, ?);", new boolean[]{false, false}, otherChatParticipantsID[i], otherChatParticipantsDisplayName[i]);
        }
        //DatabaseManager.initializeDB();

        return true;
    }

    synchronized public static Boolean checkForChatInfoUpdates()
    {
        if(!connected || busy)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("CHKCHUPT");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        success = sendToServer("OK");
        if(!success)
            return null;
        reply = reply.substring(7);
        int numberOfNewUpdates = Integer.parseInt(reply);
        if(numberOfNewUpdates == 0)
            return false;

        for(int i = 0; i < numberOfNewUpdates; i++)
        {
            reply = receiveFromServer();
            if(reply == null)
                return null;
            String chatID = reply.substring(7);
            success = sendToServer("OK");
            if(!success)
                return null;

            reply = receiveFromServer();
            if(reply == null)
                return null;
            String chatType = reply.substring(9);
            success = sendToServer("OK");
            if(!success)
                return null;

            reply = receiveFromServer();
            if(reply == null)
                return null;
            String chatName = reply.substring(9);
            success = sendToServer("OK");
            if(!success)
                return null;

            reply = receiveFromServer();
            if(reply == null)
                return null;
            String numberOfParticipants = reply.substring(9);
            success = sendToServer("OK");
            if(!success)
                return null;

            String[][] participantsList = new String[Integer.parseInt(numberOfParticipants)][2];
            for(int j = 0; j < Integer.parseInt(numberOfParticipants); j++)
            {
                reply = receiveFromServer();
                if(reply == null)
                    return null;
                participantsList[j][0] = reply.substring(6);
                success = sendToServer("OK");
                if(!success)
                    return null;

                reply = receiveFromServer();
                if(reply == null)
                    return null;
                participantsList[j][1] = reply.substring(8);
                success = sendToServer("OK");
                if(!success)
                    return null;
            }
            String participants = "";
            for(int j = 0; j < participantsList.length; j++)
            {
                participants = participants + participantsList[j][0] + ",";
            }
            participants = participants.substring(0, participants.length() - 1);

            DatabaseManager.makeUpdate("update chat set chat_type = ?, chat_name = ?, chat_participants = ? where chat_id = " + chatID + ";", new boolean[]{false, false, false}, chatType, chatName, participants);

            for(int j = 0; j < participantsList.length; j++)
            {
                String ID = participantsList[j][0];
                String name = participantsList[j][1];
                DatabaseManager.makeUpdate("insert into savedUsers values(?, ?);", new boolean[]{false, false}, ID, name);
            }
        }

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("DONE"))
            return false;
        success = sendToServer("OK");
        if(!success)
            return null;

        //DatabaseManager.initializeDB();

        return true;
    }

    synchronized public static Boolean addNewUserToGroupChat(String chatID, String newUserID)
    {
        if(!connected)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("ADDGCUSR");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("CHATID " + chatID);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("NEWUSRID " + newUserID);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("DONE");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return false;
        String newUserDisplayName = reply.substring(9);
        success = sendToServer("OK");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("DONE"))
            return false;
        success = sendToServer("OK");
        if(!success)
            return null;

        DatabaseManager.makeUpdate("insert into savedUsers values(?, ?);", new boolean[]{false, false}, newUserID, newUserDisplayName);
        String[][] currentChatParticipants = DatabaseManager.makeQuery("select chat_participants from chat where chat_id = " + chatID + ";", null);
        if(currentChatParticipants != null && currentChatParticipants.length > 0) {
            String newParticipantList = currentChatParticipants[0][0] + "," + newUserID;
            if(newParticipantList.charAt(0) == ',')
            {
                newParticipantList = newParticipantList.substring(1);
            }
            DatabaseManager.makeUpdate("update chat set chat_participants = ? where chat_id = " + chatID + ";", new boolean[]{false}, newParticipantList);
        }

        return true;
    }

    synchronized public static Boolean leaveFromGroupChat(String chatID)
    {
        if(!connected)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("LEAVEGCH");
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("CHATID " + chatID);
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("DONE");
        if(!success)
            return null;
        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        DatabaseManager.makeUpdate("update chat set chat_type = 'GROUP_LF' where chat_id = " + chatID + ";", null);

        return true;
    }

    synchronized public static Boolean retrieveUnknownUserDisplayName(String userAccountID)
    {
        if(!connected)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("GETDSPNM " + userAccountID);
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("DONE");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return false;
        String userDisplayName = reply.substring(9);
        success = sendToServer("OK");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("DONE"))
            return false;
        success = sendToServer("OK");
        if(!success)
            return null;

        DatabaseManager.makeUpdate("delete from unknownUsers where account_id = ?;", new boolean[]{false}, userAccountID);

        DatabaseManager.makeUpdate("insert into savedUsers values(?, ?);", new boolean[]{false, false}, userAccountID, userDisplayName);

        return true;
    }

    synchronized public static Boolean retrieveUnknownChatInfo(String chatID)
    {
        if(!connected)
            return null;
        if(!loggedIn)
            return null;

        boolean success = sendToServer("GETCHINF " + chatID);
        if(!success)
            return null;
        String reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("OK"))
            return false;

        success = sendToServer("DONE");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return false;
        String chatType = reply.substring(9);
        success = sendToServer("OK");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return false;
        String chatName = reply.substring(9);
        success = sendToServer("OK");
        if(!success)
            return null;

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(reply.equals("ERROR"))
            return false;
        String numberOfParticipants = reply.substring(9);
        success = sendToServer("OK");
        if(!success)
            return null;

        String[][] participantsList = new String[Integer.parseInt(numberOfParticipants)][2];
        for(int i = 0; i < Integer.parseInt(numberOfParticipants); i++)
        {
            reply = receiveFromServer();
            if(reply == null)
                return null;
            if(reply.equals("ERROR"))
                return false;
            participantsList[i][0] = reply.substring(6);
            success = sendToServer("OK");
            if(!success)
                return null;

            reply = receiveFromServer();
            if(reply == null)
                return null;
            if(reply.equals("ERROR"))
                return false;
            participantsList[i][1] = reply.substring(8);
            success = sendToServer("OK");
            if(!success)
                return null;
        }
        String participants = "";
        for(int j = 0; j < participantsList.length; j++)
        {
            participants = participants + participantsList[j][0] + ",";
        }
        participants = participants.substring(0, participants.length() - 1);

        reply = receiveFromServer();
        if(reply == null)
            return null;
        if(!reply.equals("DONE"))
            return false;
        success = sendToServer("OK");
        if(!success)
            return null;

        //TODO: Find a way to generate and synchronize chat key and iv either locally or over the network
        String chatKey = "";
        String chatIV = "";
        if(chatType.equals("PERSONAL"))
        {
            chatKey = EncryptionManager.generateDeterministicKeyForPersonalChats(ConfigManager.getAccountID(), participantsList[0][0]);
            chatIV = EncryptionManager.generateDeterministicInitializationVectorForPersonalChats(ConfigManager.getAccountID(), participantsList[0][0]);
        }
        else if(chatType.equals("GROUP"))
        {
            chatKey = EncryptionManager.generateDeterministicKeyForGroupChats(chatName, chatID);
            chatIV = EncryptionManager.generateDeterministicInitializationVectorForGroupChats(chatName, chatID);
        }

        for(int i = 0; i < participantsList.length; i++)
        {
            String ID = participantsList[i][0];
            String name = participantsList[i][1];
            DatabaseManager.makeUpdate("insert into savedUsers values(?, ?);", new boolean[]{false, false}, ID, name);
        }

        DatabaseManager.makeUpdate("delete from unknownChats where chat_id = " + chatID + ";", null);

        DatabaseManager.makeUpdate("insert into chat values(" + chatID + ", ?, ?, ?, ?, ?, 0);", new boolean[]{false, false, false, false, false}, chatKey, chatIV, chatType, chatName, participants);

        // TODO: CHANGE MESSAGE TABLE SCHEMA HERE AS WELL AS IN DATABASE MANAGER
        DatabaseManager.makeUpdate("create table chat" + chatID + "(message_id number(64) primary key not null, from_account_id char(16) not null, sent_to_server integer(2) not null, message_timestamp number(64) not null, message_type varchar(16) not null, message_content varchar(1048576) not null);", null);

        String[][] uncategorizedMessages = DatabaseManager.makeQuery("select * from uncategorizedChatMessages where chat_id = " + chatID + ";", null);
        if(uncategorizedMessages != null && uncategorizedMessages.length > 0)
        {
            for(int i = 0; i < uncategorizedMessages.length; i++) {
                String messageID = uncategorizedMessages[i][1];
                String senderAccountID = uncategorizedMessages[i][2];
                String messageTimestamp = uncategorizedMessages[i][4];
                String messageType = uncategorizedMessages[i][5];
                String encryptedMessage = uncategorizedMessages[i][6];
                String message;
                String documentSize = "0";
                if(messageType.equals("TEXT"))
                {
                    message = EncryptionManager.decryptText(encryptedMessage, chatKey, chatIV);
                }
                else if(messageType.equals("DOCUMENT"))
                {
                    message = EncryptionManager.decryptText(encryptedMessage, chatKey, chatIV);
                    documentSize = message.substring(message.indexOf('|') + 1);
                    message = message.substring(0, message.indexOf('|'));
                }
                else {
                    message = messageType;
                }

                if ((new BigInteger(DatabaseManager.makeQuery("select last_message_timestamp from chat where chat_id = " + chatID + ";", null)[0][0])).compareTo(new BigInteger(messageTimestamp)) == -1) {
                    DatabaseManager.makeUpdate("update chat set last_message_timestamp = " + messageTimestamp + " where chat_id = " + chatID + ";", null);
                }

                DatabaseManager.makeUpdate("insert into chat" + chatID + " values(" + messageID + ", ?, 2, " + messageTimestamp + ", ?, ?);", new boolean[]{false, false, false}, senderAccountID, messageType, message);
                if(messageType.equals("DOCUMENT"))
                {
                    DatabaseManager.makeUpdate("update mediaMessagesDetails set file_size_in_bytes = ? where chat_id = " + chatID + " and message_id = " + messageID + ";", new boolean[]{false}, documentSize);
                }

                DatabaseManager.makeUpdate("delete from uncategorizedChatMessages where chat_id = " + chatID + " and message_id = " + messageID + ";", null);
            }
        }

        return true;
    }
}

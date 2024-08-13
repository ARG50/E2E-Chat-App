package com.chat.e2e;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DatabaseManager
{
    private static String appFolderPath;
    private static String dbFolderPath;
    private static String dbEncryptedFilePath;
    private static String dbDecryptedFilePath;


    private static Timer dbUpdateTimer;
    private final static long DB_UPDATE_FREQUENCY_IN_SECONDS = 30;
    synchronized public static Timer getDbUpdateTimer()
    {
        return dbUpdateTimer;
    }



    private static Connection dbConnection = null;

    static
    {
        initializeDbUpdateTimer();
    }

    synchronized public static void initializeDB()
    {
        appFolderPath = ConfigManager.getAppFolderPath();
        dbFolderPath = appFolderPath + "\\database";
        dbEncryptedFilePath = dbFolderPath + "\\" + ConfigManager.getAccountID() + "_main.dbenc";
        dbDecryptedFilePath = dbFolderPath + "\\" + ConfigManager.getAccountID() + "_main.db";

        File appFolder = new File(appFolderPath);
        if(!appFolder.exists())
        {
            boolean success = appFolder.mkdir();
            if(!success)
                return;
        }

        File dbFolder = new File(dbFolderPath);
        if(!dbFolder.exists())
        {
            boolean success = dbFolder.mkdir();
            if(!success)
                return;
        }

        try
        {
            if(dbConnection != null)
                dbConnection.close();
            dbConnection = null;
        }
        catch(Exception e)
        {
            return;
        }

        File encryptedDB = new File(dbEncryptedFilePath);
        File decryptedDB = new File(dbDecryptedFilePath);

        if(decryptedDB.exists())
        {
            boolean success = decryptedDB.delete();
            if(!success)
                return;
        }

        if(encryptedDB.exists())
        {
            String key = EncryptionManager.findDatabaseKey(ConfigManager.getAccountID(), ConfigManager.getAccountUsername());
            String IV = EncryptionManager.findDatabaseIV(ConfigManager.getAccountID(), ConfigManager.getAccountUsername());
            //System.out.println("Decrypting with:\nKey: " + key + "\nIV: " + IV);
            boolean success = EncryptionManager.decryptFile(dbEncryptedFilePath, key, IV);
            if(!success)
                return;

            try
            {
                dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dbDecryptedFilePath);

                if(!dbConnection.getMetaData().getTables(null, null, "chat", null).next())
                {
                    makeUpdate("create table chat(chat_id number(32) primary key not null, encryption_key char(44) not null, encryption_IV char(16) not null, chat_type varchar(8) not null, chat_name varchar(64) not null, chat_participants varchar(16384) not null, last_message_timestamp number(64) not null);", null);
                }
                else
                {
                    String[][] chatList = makeQuery("select chat_id from chat;", null);
                    if(chatList != null)
                    {
                        DatabaseMetaData tableList = dbConnection.getMetaData();
                        for(int i = 0; i < chatList.length; i++)
                        {
                            if(!tableList.getTables(null, null, "chat" + chatList[i][0], null).next())
                            {
                                // TODO: IF YOU CHANGE THE SCHEMA HERE, CHANGE IT IN NETWORK MANAGER CHECK FOR NEW CHATS METHOD, CREATE NEW PERSONAL CHAT METHOD, CREATE NEW GROUP CHAT METHOD, RETRIEVE UNKNOWN CHAT INFO METHOD, AS WELL. OTHERWISE NEW CHATS WILL BE CREATED WITH OLD SCHEMA.
                                makeUpdate("create table chat" + chatList[i][0] + "(message_id number(64) primary key not null, from_account_id char(16) not null, sent_to_server integer(2) not null, message_timestamp number(64) not null, message_type varchar(16) not null, message_content varchar(1048576) not null);", null);
                            }
                        }
                    }
                }

                if(!dbConnection.getMetaData().getTables(null, null, "savedUsers", null).next())
                {
                    makeUpdate("create table savedUsers(account_id char(16) primary key not null, display_name varchar(64) not null);", null);
                }

                if(!dbConnection.getMetaData().getTables(null, null, "unsentReadReceipts", null).next())
                {
                    makeUpdate("create table unsentReadReceipts(chat_id number(32) not null, message_id number(64) not null, primary key(chat_id, message_id));", null);
                }

                if(!dbConnection.getMetaData().getTables(null, null, "unknownUsers", null).next())
                {
                    makeUpdate("create table unknownUsers(account_id char(16) not null, primary key(account_id));", null);
                }

                if(!dbConnection.getMetaData().getTables(null, null, "unknownChats", null).next())
                {
                    makeUpdate("create table unknownChats(chat_id number(32) not null, primary key(chat_id));", null);
                }

                if(!dbConnection.getMetaData().getTables(null, null, "uncategorizedChatMessages", null).next())
                {
                    makeUpdate("create table uncategorizedChatMessages(chat_id number(32) not null, message_id number(64) not null, from_account_id char(16) not null, sent_to_server integer(2) not null, message_timestamp number(64) not null, message_type varchar(16) not null, message_content varchar(1048576) not null, primary key(chat_id, message_id));", null);
                }

                if(!dbConnection.getMetaData().getTables(null, null, "mediaMessagesDetails", null).next())
                {
                    makeUpdate("create table mediaMessagesDetails(chat_id number(32) not null, message_id number(64) not null, file_size_in_bytes varchar(32) not null, primary key(chat_id, message_id));", null);
                }
            }
            catch (Exception e)
            {
                return;
            }

            //System.out.println("Encrypting with:\nKey: " + key + "\nIV: " + IV);
            success = EncryptionManager.encryptFile(dbDecryptedFilePath, key, IV);
            if(!success)
                return;
        }
        else
        {
            try
            {
                dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dbDecryptedFilePath);

                if(!dbConnection.getMetaData().getTables(null, null, "chat", null).next())
                {
                    makeUpdate("create table chat(chat_id number(32) primary key not null, encryption_key char(44) not null, encryption_IV char(16) not null, chat_type varchar(8) not null, chat_name varchar(64) not null, chat_participants varchar(16384) not null, last_message_timestamp number(64) not null);", null);
                }
                else
                {
                    String[][] chatList = makeQuery("select chat_id from chat;", null);
                    if(chatList != null)
                    {
                        DatabaseMetaData tableList = dbConnection.getMetaData();
                        for(int i = 0; i < chatList.length; i++)
                        {
                            if(!tableList.getTables(null, null, "chat" + chatList[i][0], null).next())
                            {
                                makeUpdate("create table chat" + chatList[i][0] + "(message_id number(64) primary key not null, from_account_id char(16) not null, sent_to_server integer(2) not null, message_timestamp number(64) not null, message_type varchar(16) not null, message_content varchar(1048576) not null);", null);
                            }
                        }
                    }
                }

                if(!dbConnection.getMetaData().getTables(null, null, "savedUsers", null).next())
                {
                    makeUpdate("create table savedUsers(account_id char(16) primary key not null, display_name varchar(64) not null);", null);
                }

                if(!dbConnection.getMetaData().getTables(null, null, "unsentReadReceipts", null).next())
                {
                    makeUpdate("create table unsentReadReceipts(chat_id number(32) not null, message_id number(64) not null, primary key(chat_id, message_id));", null);
                }

                if(!dbConnection.getMetaData().getTables(null, null, "unknownUsers", null).next())
                {
                    makeUpdate("create table unknownUsers(account_id char(16) not null, primary key(account_id));", null);
                }

                if(!dbConnection.getMetaData().getTables(null, null, "unknownChats", null).next())
                {
                    makeUpdate("create table unknownChats(chat_id number(32) not null, primary key(chat_id));", null);
                }

                if(!dbConnection.getMetaData().getTables(null, null, "uncategorizedChatMessages", null).next())
                {
                    makeUpdate("create table uncategorizedChatMessages(chat_id number(32) not null, message_id number(64) not null, from_account_id char(16) not null, sent_to_server integer(2) not null, message_timestamp number(64) not null, message_type varchar(16) not null, message_content varchar(1048576) not null, primary key(chat_id, message_id));", null);
                }

                if(!dbConnection.getMetaData().getTables(null, null, "mediaMessagesDetails", null).next())
                {
                    makeUpdate("create table mediaMessagesDetails(chat_id number(32) not null, message_id number(64) not null, file_size_in_bytes varchar(32) not null, primary key(chat_id, message_id));", null);
                }
            }
            catch (Exception e)
            {
                return;
            }

            String key = EncryptionManager.findDatabaseKey(ConfigManager.getAccountID(), ConfigManager.getAccountUsername());
            String IV = EncryptionManager.findDatabaseIV(ConfigManager.getAccountID(), ConfigManager.getAccountUsername());
            boolean success = EncryptionManager.encryptFile(dbDecryptedFilePath, key, IV);
            if(!success)
                return;
        }
    }

    synchronized public static void closeDB()
    {
        try
        {
            if(dbConnection != null) {
                String key = EncryptionManager.findDatabaseKey(ConfigManager.getAccountID(), ConfigManager.getAccountUsername());
                String IV = EncryptionManager.findDatabaseIV(ConfigManager.getAccountID(), ConfigManager.getAccountUsername());
                //System.out.println("Encrypting with:\nKey: " + key + "\nIV: " + IV);
                boolean success = EncryptionManager.encryptFile(dbDecryptedFilePath, key, IV);
                if(!success)
                    return;
                dbConnection.close();
                dbConnection = null;
                File decryptedFile = new File(dbDecryptedFilePath);
                success = decryptedFile.delete();
                if(!success)
                    return;
            }
        }
        catch(Exception e)
        {

        }
    }

    //TODO: Change each usage of this function to make use of the new format
    synchronized public static boolean makeUpdate(String preparedStatement, boolean[] isArgumentInIndexNumber, String ... arguments)
    {
        //System.out.println(statement);
        if(dbConnection == null)
            return false;
        try {
            PreparedStatement updateStatement = dbConnection.prepareStatement(preparedStatement);
            for(int i = 0; i < arguments.length; i++)
            {
                if(!isArgumentInIndexNumber[i])
                    updateStatement.setString(i + 1, arguments[i]);
                else
                    updateStatement.setLong(i + 1, Long.parseLong(arguments[i]));
            }
            updateStatement.setQueryTimeout(30);

            updateStatement.executeUpdate();

            updateStatement.close();
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    //TODO: Change each usage of this function to make use of the new format
    synchronized public static String[][] makeQuery(String preparedStatement, boolean[] isArgumentInIndexNumber, String ... arguments)
    {
        ResultSet queryResultSet;
        List<String[]> queryResultStringList = new ArrayList<>();
        String[][] queryResultString2dArray;
        if(dbConnection == null)
            return null;
        try
        {
            PreparedStatement queryStatement = dbConnection.prepareStatement(preparedStatement);
            for(int i = 0; i < arguments.length; i++)
            {
                if(!isArgumentInIndexNumber[i])
                    queryStatement.setString(i + 1, arguments[i]);
                else
                    queryStatement.setLong(i + 1, Long.parseLong(arguments[i]));
            }
            queryStatement.setQueryTimeout(30);

            queryResultSet = queryStatement.executeQuery();

            //queryResultSet.last();
            //int numberOfRows = queryResultSet.getRow();
            //queryResultSet.beforeFirst();
            int numberOfColumns = queryResultSet.getMetaData().getColumnCount();

            for(int i = 0; queryResultSet.next(); i++)
            {
                queryResultStringList.add(new String[numberOfColumns]);
                for(int j = 0; j < numberOfColumns; j++)
                {
                    queryResultStringList.get(i)[j] = queryResultSet.getString(j + 1);
                    //queryResultString2dArray[i][j] = queryResultSet.getString(j + 1);
                }
            }

            int numberOfRows = queryResultStringList.size();

            queryResultString2dArray = new String[numberOfRows][numberOfColumns];

            for(int i = 0; i < numberOfRows; i++)
            {
                for(int j = 0; j < numberOfColumns; j++)
                {
                    queryResultString2dArray[i][j] = queryResultStringList.get(i)[j];
                }
            }

            queryStatement.close();
        }
        catch (Exception e)
        {
            //System.out.println(e);
            return null;
        }
        return queryResultString2dArray;
    }

    synchronized public static boolean checkIfTableExists(String tableName)
    {
        try {
            if (!dbConnection.getMetaData().getTables(null, null, tableName, null).next()) {
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    synchronized public static void initializeDbUpdateTimer()
    {
        dbUpdateTimer = new Timer();
        dbUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (dbConnection == null)
                        return;
                    String key = EncryptionManager.findDatabaseKey(ConfigManager.getAccountID(), ConfigManager.getAccountUsername());
                    String IV = EncryptionManager.findDatabaseIV(ConfigManager.getAccountID(), ConfigManager.getAccountUsername());
                    boolean success = EncryptionManager.encryptFile(dbDecryptedFilePath, key, IV);
                }
                catch (Exception e)
                {

                }
            }
        }, DB_UPDATE_FREQUENCY_IN_SECONDS * 1000, DB_UPDATE_FREQUENCY_IN_SECONDS * 1000);
    }
}

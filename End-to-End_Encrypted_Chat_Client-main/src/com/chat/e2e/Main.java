package com.chat.e2e;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.*;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static AppWindowFrame mainFrame;

    public static AppWindowFrame getMainFrame()
    {
        return mainFrame;
    }

    public static void main(String[] args)
    {
        ConfigManager.initializeConfig();
        LogManager.initializeLog();
//        String key = EncryptionManager.findDatabaseKey(ConfigManager.getAccountID(), ConfigManager.getAccountUsername());
//        String IV = EncryptionManager.findDatabaseIV(ConfigManager.getAccountID(), ConfigManager.getAccountUsername());
//        String dbDecryptedFilePath = ConfigManager.getAppFolderPath() + "\\database" + "\\" + ConfigManager.getAccountID() + "_main.db";
//        boolean success = EncryptionManager.encryptFile(dbDecryptedFilePath, key, IV);
        ThemeManager.initializeTheme();
        DatabaseManager.initializeDB();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mainFrame = new AppWindowFrame();
            }
        });

//        try
//        {
//            Thread.sleep(5000);
//        }
//        catch (Exception e)
//        {
//
//        }

        //mainFrame.addToMainPanel(new ChatMainPanel(), true);
        //boolean success = DatabaseManager.makeUpdate("drop table account;");
        //System.out.println(success);


        //NetworkManager.sendToServer("hello world");
        //System.out.println(NetworkManager.receiveFromServer());

//        String key = EncryptionManager.generateSecretKey();
//        String IV = EncryptionManager.generateInitializationVector();
//        String originalMessage = "Hello world";
//        System.out.println("Original message: " + originalMessage);
//        System.out.println("Key: " + key);
//        System.out.println("IV: " + IV);
//        String encryptedMessage = EncryptionManager.encryptText(originalMessage, key, IV);
//        System.out.println("Encrypted message: " + encryptedMessage);
//        String decryptedMessage = EncryptionManager.decryptText(encryptedMessage, key, IV);
//        System.out.println("Decrypted message: " + decryptedMessage);

//        String key = EncryptionManager.generateSecretKey();
//        String IV = EncryptionManager.generateInitializationVector();
//        String fileLocation = "D:\\test\\HighResScreenShot_2021-11-28_08-00-37.bmp";
//        EncryptionManager.encryptFile(fileLocation, key, IV);
//        System.out.println("File encrypted");
//        Scanner s = new Scanner(System.in);
//        s.next();
//        EncryptionManager.decryptFile(fileLocation + "enc", key, IV);
//        System.out.println("File decrypted");

//        Scanner s = new Scanner(System.in);
//        s.next();
//        System.out.println(NetworkManager.disconnectFromServer());

//        Random random = new Random();
//        String[][] accountIDs = DatabaseManager.makeQuery("select account_id from savedUsers;");
//        String chatCreatorAccountID = accountIDs[random.nextInt(0, accountIDs.length)][0];
//        String otherParticipantAccountID = accountIDs[random.nextInt(0, accountIDs.length)][0];
//
//        String key1 = EncryptionManager.generateDeterministicKey(chatCreatorAccountID, otherParticipantAccountID);
//        String key2 = EncryptionManager.generateDeterministicKey(otherParticipantAccountID, chatCreatorAccountID);
//
//        String iv1 = EncryptionManager.generateDeterministicInitializationVector(chatCreatorAccountID, otherParticipantAccountID);
//        String iv2 = EncryptionManager.generateDeterministicInitializationVector(otherParticipantAccountID, chatCreatorAccountID);
//
//        System.out.println("Creator account ID: " + chatCreatorAccountID + "\nOther account ID: " + otherParticipantAccountID);
//        System.out.println("Key 1: " + key1 + "\nKey 2: " + key2);
//        System.out.println("IV 1: " + iv1 + "\nIV 2: " + iv2);
//
//        String originalPlainText = "Hello world!!";
//
//        System.out.println("Original plain text: " + originalPlainText);
//
//        String cipherText = EncryptionManager.encryptText(originalPlainText, key1, iv1);
//
//        System.out.println("Encrypting with Key 1 and IV 1...\nCipher text: " + cipherText);
//
//        String decryptedPlainText = EncryptionManager.decryptText(cipherText, key2, iv2);
//
//        System.out.println("Decrypting with Key 2 and IV 2...\nDecrypted plain text: " + decryptedPlainText);
//
//        String fileLocation = "D:\\test\\HighResScreenShot_2021-11-28_08-00-37.bmp";
//
//        System.out.print("Press enter to continue to encrypt file at location " + fileLocation);
//        Scanner s = new Scanner(System.in);
//        s.next();
//
//        EncryptionManager.encryptFile(fileLocation, key1, iv1);
//
//        System.out.println("File encrypted using Key 1 and IV 1...");
//        String newFileLocation = fileLocation + "enc";
//        System.out.println("Press enter to continue to decrypt file at location " + newFileLocation);
//        s.next();
//
//        EncryptionManager.decryptFile(newFileLocation, key2, iv2);
//
//        System.out.println("File decrypted using Key 2 and IV 2...");

//        String fonts[]
//                = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
//
//        for (int i = 0; i < fonts.length; i++) {
//            System.out.println(fonts[i]);
//        }

    }
}
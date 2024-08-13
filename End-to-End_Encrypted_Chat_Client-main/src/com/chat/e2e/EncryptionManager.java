package com.chat.e2e;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class EncryptionManager
{

    private static final int KEY_LENGTH = 256;
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 16;
//    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//    keyGen.init(256); // for example
//    SecretKey secretKey = keyGen.generateKey();

    synchronized public static String findDatabaseKey(String accountID, String username)
    {
        //System.out.println("Generating key with:\nID: " + accountID + "\nUsername: " + username);
        byte[] seed = new byte[KEY_LENGTH / 8];
        for(int i = 0; i < seed.length; i++)
        {
            seed[i] = (byte) ((byte)accountID.charAt(i % accountID.length()) + (byte)username.charAt(i % username.length()));
        }
        String generatedKey;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom secureRandomObject = SecureRandom.getInstance("SHA1PRNG");
            secureRandomObject.setSeed(seed);
            keyGenerator.init(KEY_LENGTH, secureRandomObject);
            SecretKey key = keyGenerator.generateKey();
            generatedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        }
        catch(Exception e)
        {
            return null;
        }
        //System.out.println("Returning key: " + generatedKey);
        return generatedKey;
    }

    synchronized public static String findDatabaseIV(String accountID, String username)
    {
        //System.out.println("Generating IV with:\nID: " + accountID + "\nUsername: " + username);
        byte[] seed = new byte[IV_LENGTH];
        for(int i = 0; i < seed.length; i++)
        {
            seed[i] = (byte) ((byte)accountID.charAt(i % accountID.length()) + (byte)username.charAt(i % username.length()));
        }
        byte[] IV = new byte[IV_LENGTH];
        try
        {
            SecureRandom secureRandomObject = SecureRandom.getInstance("SHA1PRNG");
            secureRandomObject.setSeed(seed);
            secureRandomObject.nextBytes(IV);
        }
        catch (Exception e)
        {
            return null;
        }
        String generatedIV = Base64.getEncoder().encodeToString(IV);
        //System.out.println("Returning IV: " + generatedIV);
        return generatedIV;
    }

    //TODO: The first account ID must be of the chat creator who first created the chat and sent it to server
    // The second account ID must be of the other participant in a personal chat
    // This method only works for generating keys of a personal chat
    // One possible working of such combination is as follows
    // To generate key:
    // The account IDs are converted to long and added up
    // The summed up value is passed as seed to a random object
    // The random object is used to generate as many characters as required for the key
    // Since the random object of both sender and receiver will receive the same seed, it will generate the same sequence of characters
    // To generate IV:
    // The account IDs are converted to long and the lower valued ID is subtracted from higher valued ID, that is, the difference
    // The difference value is passed as a seed to a random object
    // The random object is used to generate as many characters as required for the IV
    // Since the random object of both sender and receiver will receive the same seed, it will generate the same sequence of characters
    synchronized public static String generateDeterministicKeyForPersonalChats(String accountId1, String accountId2)
    {
        byte[] keyBytes = new byte[KEY_LENGTH / 8];
        long randomObjectSeed = Long.parseLong(accountId1) + Long.parseLong(accountId2);
        Random randomCharacterGenerator = new Random(randomObjectSeed);
        for(int i = 0; i < keyBytes.length; i++)
        {
            keyBytes[i] = (byte)randomCharacterGenerator.nextInt(33, 127);
        }
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    synchronized public static String generateDeterministicInitializationVectorForPersonalChats(String accountId1, String accountId2)
    {
        byte[] ivBytes = new byte[IV_LENGTH];
        long randomObjectSeed = Math.abs(Long.parseLong(accountId1) - Long.parseLong(accountId2));
        Random randomCharacterGenerator = new Random(randomObjectSeed);
        for(int i = 0; i < ivBytes.length; i++)
        {
            ivBytes[i] = (byte)randomCharacterGenerator.nextInt(33, 127);
        }
        return Base64.getEncoder().encodeToString(ivBytes);
    }

    synchronized public static String generateDeterministicKeyForGroupChats(String chatName, String chatID)
    {
        byte[] keyBytes = new byte[KEY_LENGTH / 8];
        long randomObjectSeed = 0;
        for(int i = 0; i < chatName.length(); i++)
        {
            randomObjectSeed = randomObjectSeed + (long)chatName.charAt(i);
        }
        randomObjectSeed = randomObjectSeed + Long.parseLong(chatID);
        Random randomCharacterGenerator = new Random(randomObjectSeed);
        for(int i = 0; i < keyBytes.length; i++)
        {
            keyBytes[i] = (byte)randomCharacterGenerator.nextInt(33, 127);
        }
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    synchronized public static String generateDeterministicInitializationVectorForGroupChats(String chatName, String chatID)
    {
        byte[] ivBytes = new byte[IV_LENGTH];
        long randomObjectSeed = 0;
        for(int i = 0; i < chatName.length(); i++)
        {
            randomObjectSeed = randomObjectSeed + (long)chatName.charAt(i);
        }
        randomObjectSeed = randomObjectSeed - Long.parseLong(chatID);
        Random randomCharacterGenerator = new Random(randomObjectSeed);
        for(int i = 0; i < ivBytes.length; i++)
        {
            ivBytes[i] = (byte)randomCharacterGenerator.nextInt(33, 127);
        }
        return Base64.getEncoder().encodeToString(ivBytes);
    }


    synchronized public static String encryptText(String message, String key, String IV)
    {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        byte[] decodedIV = Base64.getDecoder().decode(IV);
        byte[] messageBytes = message.getBytes();
        byte[] encryptedMessage = encrypt(messageBytes, secretKey, decodedIV, messageBytes.length);
        return Base64.getEncoder().encodeToString(encryptedMessage);
    }

    synchronized public static String decryptText(String message, String key, String IV)
    {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        byte[] decodedIV = Base64.getDecoder().decode(IV);
        byte[] messageBytes = Base64.getDecoder().decode(message);
        byte[] decryptedMessage = decrypt(messageBytes, secretKey, decodedIV, messageBytes.length);
        return new String(decryptedMessage);
    }

    synchronized public static boolean encryptFile(String fileLocation, String key, String IV)
    {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        byte[] decodedIV = Base64.getDecoder().decode(IV);

        File originalFile = new File(fileLocation);
        File newFile = new File(fileLocation + "enc");
        try(FileInputStream originalFileStream = new FileInputStream(originalFile); FileOutputStream newFileStream = new FileOutputStream(newFile))
        {
            byte[] inputFileBuffer = new byte[16777216];
            byte[] outputFileBuffer;
            byte[] outputEncodedFileBuffer;
            int numberOfBytesRead = originalFileStream.read(inputFileBuffer);
            while(numberOfBytesRead != -1)
            {
                outputFileBuffer = encrypt(inputFileBuffer, secretKey, decodedIV, numberOfBytesRead);
                outputEncodedFileBuffer = Base64.getEncoder().encode(outputFileBuffer);
                newFileStream.write(outputEncodedFileBuffer, 0, outputEncodedFileBuffer.length);
                numberOfBytesRead = originalFileStream.read(inputFileBuffer);
            }
        }
        catch(Exception e)
        {
            //System.out.println(e);
            return false;
        }
        return true;
    }

    synchronized public static boolean decryptFile(String fileLocation, String key, String IV)
    {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        byte[] decodedIV = Base64.getDecoder().decode(IV);

        File originalFile = new File(fileLocation);
        File newFile = new File(fileLocation.substring(0, fileLocation.length() - 3));
        try(FileInputStream originalFileStream = new FileInputStream(originalFile); FileOutputStream newFileStream = new FileOutputStream(newFile))
        {
            byte[] inputFileBuffer = new byte[22369644];
            byte[] outputFileBuffer;
            byte[] inputDecodedFileBuffer;
            int numberOfBytesRead = originalFileStream.read(inputFileBuffer);
            while(numberOfBytesRead != -1)
            {
                inputDecodedFileBuffer = Base64.getDecoder().decode(Arrays.copyOf(inputFileBuffer, numberOfBytesRead));
                outputFileBuffer = decrypt(inputDecodedFileBuffer, secretKey, decodedIV, inputDecodedFileBuffer.length);
                newFileStream.write(outputFileBuffer, 0, outputFileBuffer.length);
                numberOfBytesRead = originalFileStream.read(inputFileBuffer);
            }
        }
        catch(Exception e)
        {
            return false;
        }
        return true;
    }

    synchronized public static byte[] encrypt(byte[] unencryptedBytes, SecretKey key, byte[] IV, int lengthOfBytes)
    {
        byte[] encryptedBytes;
        try {
            Cipher encryptor = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmParameters = new GCMParameterSpec(TAG_LENGTH * 8, IV);
            encryptor.init(Cipher.ENCRYPT_MODE, key, gcmParameters);
            encryptedBytes = encryptor.doFinal(unencryptedBytes, 0, lengthOfBytes);
        }
        catch(Exception e)
        {
            return new byte[0];
        }
        return encryptedBytes;
    }

    synchronized public static byte[] decrypt(byte[] encryptedBytes, SecretKey key, byte[] IV, int lengthOfBytes)
    {
        byte[] decryptedBytes;
        try
        {
            Cipher decryptor = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmParameters = new GCMParameterSpec(TAG_LENGTH * 8, IV);
            decryptor.init(Cipher.DECRYPT_MODE, key, gcmParameters);
            decryptedBytes = decryptor.doFinal(encryptedBytes, 0, lengthOfBytes);
        }
        catch(Exception e)
        {
            System.out.println(e);
            return new byte[0];
        }
        return decryptedBytes;
    }
}

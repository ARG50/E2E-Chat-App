package com.e2e.chatServer;

import javax.swing.filechooser.FileSystemView;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class LogManager
{
    //app folder and log folder path store
    private static String appFolderPath;
    private static String logFolderPath;


    private static Timer logCleaningTimer;


    //log update listeners
    private static ArrayList<ActionListener> serverMainLogUpdatedListeners;
    private static HashMap<String, ArrayList<ActionListener>> userLogUpdatedListeners;



    //for initializing log folders on app load
    synchronized public static boolean initializeLog()
    {
        serverMainLogUpdatedListeners =  new ArrayList<>();
        userLogUpdatedListeners = new HashMap<>();

        appFolderPath = FileSystemView.getFileSystemView().getDefaultDirectory().toString() + "\\E2E Chat Server";
        logFolderPath = appFolderPath + "\\log";

        File appFolder= new File(appFolderPath);
        if(!appFolder.exists())
        {
            boolean success = appFolder.mkdir();
            if(!success)
                return false;
        }

        File logFolder = new File(logFolderPath);
        if(!logFolder.exists())
        {
            boolean success = logFolder.mkdir();
            if(!success)
            {
                return false;
            }
        }


        logCleaningTimer = new Timer();
        logCleaningTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                File[] allLogs = logFolder.listFiles();
                for(int i = 0; i < allLogs.length; i++)
                {
                    long fileSize = allLogs[i].length();
                    if(fileSize > 10000)
                    {
                        byte[] fileData;
                        try(FileInputStream logFileStream = new FileInputStream(allLogs[i]))
                        {
                            fileData = logFileStream.readAllBytes();
                        }
                        catch(Exception e)
                        {
                            return;
                        }

                        byte[] cleanedRemainingData = Arrays.copyOfRange(fileData, fileData.length - 10000, fileData.length);

                        try(FileOutputStream logFile = new FileOutputStream(allLogs[i]))
                        {
                            logFile.write(cleanedRemainingData);
                        }
                        catch(Exception e)
                        {
                            return;
                        }
                    }
                }
            }
        }, 2000, 60000);


        updateServerMainLog("Log has been successfully initialized.");
        return true;
    }

    synchronized public static void closeLog()
    {
        logCleaningTimer.cancel();
    }

    //for writing a message to the main server-wide log
    synchronized public static void updateServerMainLog(String message)
    {
        try(FileOutputStream logFile = new FileOutputStream(logFolderPath + "\\server_main.log", true))
        {
            logFile.write((getFormattedTimestamp() + " > " + message + "\n").getBytes());
        }
        catch(Exception e)
        {
            return;
        }
        fireServerMainLogUpdatedListeners();
    }

    //for writing a message to specific user log
    synchronized public static void updateUserLog(String message, String userAccountID)
    {
        try(FileOutputStream logFile = new FileOutputStream(logFolderPath + "\\user_" + userAccountID + ".log", true))
        {
            logFile.write((getFormattedTimestamp() + " > " + message + "\n").getBytes());
        }
        catch(Exception e)
        {
            return;
        }
        fireParticularUserLogUpdatedListeners(userAccountID);
    }

    //for getting formatted timestamp to put in the log alongside each message
    synchronized private static String getFormattedTimestamp()
    {
        long timestampInMilliseconds = System.currentTimeMillis();
        LocalDate currentDate = new Timestamp(timestampInMilliseconds).toLocalDateTime().toLocalDate();
        LocalTime currentTime = new Timestamp(timestampInMilliseconds).toLocalDateTime().toLocalTime();
        String formattedTimestamp = currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " + currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        return formattedTimestamp;
    }


    synchronized public static void addServerMainLogUpdatedListener(ActionListener listener)
    {
        serverMainLogUpdatedListeners.add(listener);
        updateServerMainLog("Action listener added to main server log.");
    }

    synchronized public static void removeServerMainLogUpdatedListener(ActionListener listener)
    {
        serverMainLogUpdatedListeners.remove(listener);
        updateServerMainLog("Action listener removed from main server log.");
    }

    synchronized private static void fireServerMainLogUpdatedListeners()
    {
        for(int i = 0; i < serverMainLogUpdatedListeners.size(); i++)
        {
            serverMainLogUpdatedListeners.get(i).actionPerformed(null);
        }
    }

    synchronized public static void addParticularUserLogUpdatedListener(String userAccountID, ActionListener listener)
    {
        if(userLogUpdatedListeners.containsKey(userAccountID))
        {
            userLogUpdatedListeners.get(userAccountID).add(listener);
        }
        else
        {
            userLogUpdatedListeners.put(userAccountID, new ArrayList<>());
            userLogUpdatedListeners.get(userAccountID).add(listener);
        }
        updateServerMainLog("Action listener added to user log for ID " + userAccountID + ".");
    }

    synchronized public static void removeParticularUserLogUpdatedListener(String userAccountID, ActionListener listener)
    {
        if(userLogUpdatedListeners.containsKey(userAccountID))
        {
            userLogUpdatedListeners.get(userAccountID).remove(listener);
        }
        updateServerMainLog("Action listener removed from user log for ID " + userAccountID + ".");
    }

    synchronized private static void fireParticularUserLogUpdatedListeners(String userAccountID)
    {
        if(userLogUpdatedListeners.containsKey(userAccountID))
        {
            ArrayList<ActionListener> listenerListReference = userLogUpdatedListeners.get(userAccountID);
            for(int i = 0; i < listenerListReference.size(); i++)
            {
                listenerListReference.get(i).actionPerformed(null);
            }
        }
    }
}

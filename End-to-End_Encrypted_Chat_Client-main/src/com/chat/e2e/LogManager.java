package com.chat.e2e;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class LogManager
{
    private static String appFolderPath;
    private static String logFolderPath;
    private static String logFilePath;
    //private static boolean logExists;

    static
    {
        initializeLog();
    }

    synchronized public static void initializeLog()
    {
        appFolderPath = ConfigManager.getAppFolderPath();
        logFolderPath = appFolderPath + "\\log";
        logFilePath = logFolderPath + "\\log.txt";

        File appFolder= new File(appFolderPath);
        if(!appFolder.exists())
        {
            boolean success = appFolder.mkdir();
            if(!success)
                return;
        }

        File logFolder = new File(logFolderPath);
        if(!logFolder.exists())
        {
            boolean success = logFolder.mkdir();
            if(!success)
            {
                return;
            }
        }

        File logFile = new File(logFilePath);
        if(!logFile.exists())
        {
            try{
                boolean success = logFile.createNewFile();
                if(!success)
                    return;
            }
            catch(Exception e)
            {

            }
        }
    }

    synchronized public static void updateLog(String message)
    {
        try(FileOutputStream logFile = new FileOutputStream(logFilePath, true))
        {
            logFile.write((message + "\n").getBytes());
        }
        catch(Exception e)
        {

        }
    }
}

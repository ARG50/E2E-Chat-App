package com.chat.e2e;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager
{
    private static String appFolderPath = FileSystemView.getFileSystemView().getDefaultDirectory().toString() + "\\E2E Chat Client";
    private static String configFolderPath = appFolderPath + "\\config";
    private static String configFilePath = configFolderPath + "\\config.xml";



    private static String clientVersion = "1.3.4";
    private static String accountID = "0000000000000000";
    private static String accountUsername = "anonymous";
    private static String accountDisplayName = "anonymous";
    private static String accountSessionID = "null";
    private static String allowNewPersonalChat = "unknown";
    private static String allowNewGroupChat = "unknown";
    private static String serverIpAddress = "127.0.0.1";
    private static String serverPortAddress = "3737";
    private static String clientPortAddressType = "Dynamic";
    private static String clientPortAddress = "7373";
    private static String connectionProbeTimePeriod = "10";
    private static String initialNumberOfMessagesToLoad = "50";
    private static String appDataFolder = appFolderPath;
    private static String currentTheme = "Arc Dark (Material)";
    private static String previousWindowState = "windowed,800,600";

    private static void setAppFolderPath(String appFolderPath) {
        ConfigManager.appFolderPath = appFolderPath;
    }

    private static void setConfigFolderPath(String configFolderPath) {
        ConfigManager.configFolderPath = configFolderPath;
    }

    private static void setConfigFilePath(String configFilePath) {
        ConfigManager.configFilePath = configFilePath;
    }

    synchronized public static void setClientVersion(String clientVersion) {
        ConfigManager.clientVersion = clientVersion;
        updateConfig();
    }

    synchronized public static void setAccountID(String accountID) {
        ConfigManager.accountID = accountID;
        updateConfig();
    }

    synchronized public static void setAccountUsername(String accountUsername) {
        ConfigManager.accountUsername = accountUsername;
        updateConfig();
    }

    synchronized public static void setAccountDisplayName(String accountDisplayName) {
        ConfigManager.accountDisplayName = accountDisplayName;
        updateConfig();
    }

    synchronized public static void setAccountSessionID(String sessionID)
    {
        ConfigManager.accountSessionID = sessionID;
        updateConfig();
    }

    synchronized public static void setAllowNewPersonalChat(String allowNewPersonalChat) {
        ConfigManager.allowNewPersonalChat = allowNewPersonalChat;
        updateConfig();
    }

    synchronized public static void setAllowNewGroupChat(String allowNewGroupChat) {
        ConfigManager.allowNewGroupChat = allowNewGroupChat;
        updateConfig();
    }

    synchronized public static void setServerIpAddress(String serverIpAddress) {
        ConfigManager.serverIpAddress = serverIpAddress;
        updateConfig();
    }

    synchronized public static void setServerPortAddress(String serverPortAddress) {
        ConfigManager.serverPortAddress = serverPortAddress;
        updateConfig();
    }

    synchronized public static void setClientPortAddressType(String clientPortAddressType) {
        ConfigManager.clientPortAddressType = clientPortAddressType;
        updateConfig();
    }

    synchronized public static void setClientPortAddress(String clientPortAddress) {
        ConfigManager.clientPortAddress = clientPortAddress;
        updateConfig();
    }

    synchronized public static void setConnectionProbeTimePeriod(String connectionProbeTimePeriod) {
        ConfigManager.connectionProbeTimePeriod = connectionProbeTimePeriod;
        updateConfig();
    }

    synchronized public static void setInitialNumberOfMessagesToLoad(String initialNumberOfMessagesToLoad) {
        ConfigManager.initialNumberOfMessagesToLoad = initialNumberOfMessagesToLoad;
        updateConfig();
        if(WindowManager.getCurrentPanel().equals("UserChatPanel"))
        {
            WindowManager.getChatPanelReference().updateFromDB(ChatMainPanel.KEEP_MESSAGE_LIST_AT_CURRENT_SCROLL_POSITION_WHEN_LOADING_OLD_MESSAGES);
        }
    }

    synchronized public static void setInitialNumberOfMessagesToLoad(String initialNumberOfMessagesToLoad, boolean temporaryChange) {
        ConfigManager.initialNumberOfMessagesToLoad = initialNumberOfMessagesToLoad;
    }

//    synchronized public static void setAppDataFolder(String appDataFolder) {
//        ConfigManager.appDataFolder = appDataFolder;
//        appFolderPath = appDataFolder;
//        configFolderPath = appFolderPath + "\\config";
//        configFilePath = configFolderPath + "\\config.xml";
//        //TODO: Need to move folder using different storage manager class or something else
//        // also need to inform Database Manager and Log Manager and whoever else uses the app data folder
//        // about the location change
//
//    }

    synchronized public static void setCurrentTheme(String currentTheme) {
        ConfigManager.currentTheme = currentTheme;
        updateConfig();
        ThemeManager.changeTheme(currentTheme, true);
        if(WindowManager.getCurrentPanel().equals("UserChatPanel"))
            WindowManager.getChatPanelReference().updateFromDB(ChatMainPanel.KEEP_MESSAGE_LIST_AT_CURRENT_SCROLL_POSITION_WHEN_REFRESHING_FOR_ANY_REASON);
    }

    synchronized public static void setPreviousWindowState(String newState)
    {
        //System.out.println("Window state updated");
        ConfigManager.previousWindowState = newState;
        updateConfig();
    }

    synchronized public static String getAppFolderPath() {
        return appFolderPath;
    }

    synchronized public static String getConfigFolderPath() {
        return configFolderPath;
    }

    synchronized public static String getConfigFilePath() {
        return configFilePath;
    }

    synchronized public static String getAccountUsername() {
        return accountUsername;
    }

    synchronized public static String getAccountDisplayName() {
        return accountDisplayName;
    }

    synchronized public static String getAccountSessionID()
    {
        return accountSessionID;
    }

    synchronized public static String getAllowNewPersonalChat() {
        return allowNewPersonalChat;
    }

    synchronized public static String getAllowNewGroupChat() {
        return allowNewGroupChat;
    }

    synchronized public static String getServerIpAddress() {
        return serverIpAddress;
    }

    synchronized public static String getServerPortAddress() {
        return serverPortAddress;
    }

    synchronized public static String getClientPortAddressType() {
        return clientPortAddressType;
    }

    synchronized public static String getClientPortAddress() {
        return clientPortAddress;
    }

    synchronized public static String getConnectionProbeTimePeriod() {
        return connectionProbeTimePeriod;
    }

    synchronized public static String getInitialNumberOfMessagesToLoad() {
        return initialNumberOfMessagesToLoad;
    }

    synchronized public static String getAppDataFolder() {
        return appDataFolder;
    }

    synchronized public static String getCurrentTheme() {
        return currentTheme;
    }

    synchronized public static String getClientVersion()
    {
        return clientVersion;
    }

    synchronized public static String getAccountID()
    {
        return accountID;
    }

    synchronized public static String getPreviousWindowState()
    {
        return previousWindowState;
    }

    static
    {
        initializeConfig();
    }

    synchronized public static void initializeConfig()
    {
        File configFile = new File(configFilePath);
        if(configFile.exists())
        {
            readConfig();
        }
        else
        {
            File appFolder = new File(appFolderPath);
            if(!appFolder.exists())
            {
                boolean success = appFolder.mkdir();
                if(!success)
                    return;
            }

            File configFolder = new File(configFolderPath);
            if(!configFolder.exists())
            {
                boolean success = configFolder.mkdir();
                if(!success)
                    return;
            }

            updateConfig();
        }
    }

    public static void readConfig()
    {
        Properties configData = new Properties();

        try(FileInputStream configFile = new FileInputStream(configFilePath))
        {
            configData.loadFromXML(configFile);
        }
        catch(IOException fileReadError)
        {
            return;
        }

        //clientVersion = configData.getProperty("clientVersion");
        accountID = configData.getProperty("accountID");
        accountUsername = configData.getProperty("accountUsername");
        accountDisplayName = configData.getProperty("accountDisplayName");
        accountSessionID = configData.getProperty("accountSessionID");
        allowNewPersonalChat = configData.getProperty("allowNewPersonalChat");
        allowNewGroupChat = configData.getProperty("allowNewGroupChat");
        serverIpAddress = configData.getProperty("serverIpAddress");
        serverPortAddress = configData.getProperty("serverPortAddress");
        clientPortAddressType = configData.getProperty("clientPortAddressType");
        clientPortAddress = configData.getProperty("clientPortAddress");
        connectionProbeTimePeriod = configData.getProperty("connectionProbeTimePeriod");
        initialNumberOfMessagesToLoad = configData.getProperty("initialNumberOfMessagesToLoad");
        appDataFolder = configData.getProperty("appDataFolder");
        appFolderPath = appDataFolder;
        configFolderPath = appFolderPath + "\\config";
        configFilePath = configFolderPath + "\\config.xml";
        currentTheme = configData.getProperty("currentTheme");
        previousWindowState = configData.getProperty("previousWindowState");

        if(accountSessionID.equals("null"))
        {
            accountID = "0000000000000000";
            accountUsername = "anonymous";
            accountDisplayName = "anonymous";
            allowNewPersonalChat = "unknown";
            allowNewGroupChat = "unknown";
            updateConfig();
        }
    }

    private static void updateConfig()
    {
        Properties configData = new Properties();
        configData.setProperty("clientVersion", clientVersion);
        configData.setProperty("accountID", accountID);
        configData.setProperty("accountUsername", accountUsername);
        configData.setProperty("accountDisplayName", accountDisplayName);
        configData.setProperty("accountSessionID", accountSessionID);
        configData.setProperty("allowNewPersonalChat", allowNewPersonalChat);
        configData.setProperty("allowNewGroupChat", allowNewGroupChat);
        configData.setProperty("serverIpAddress", serverIpAddress);
        configData.setProperty("serverPortAddress", serverPortAddress);
        configData.setProperty("clientPortAddressType", clientPortAddressType);
        configData.setProperty("clientPortAddress", clientPortAddress);
        configData.setProperty("connectionProbeTimePeriod", connectionProbeTimePeriod);
        configData.setProperty("initialNumberOfMessagesToLoad", initialNumberOfMessagesToLoad);
        configData.setProperty("appDataFolder", appDataFolder);
        configData.setProperty("currentTheme", currentTheme);
        configData.setProperty("previousWindowState", previousWindowState);

        try(FileOutputStream configFile = new FileOutputStream(configFilePath))
        {
            configData.storeToXML(configFile, null);
        }
        catch(IOException fileWriteError)
        {
            return;
        }
    }
}

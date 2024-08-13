# End-to-End Encrypted Chat Client
The whole application is based on a ***client-server*** architecture.

This repository is for the ***client*** version.

For the ***server*** version, [click here](https://github.com/ARG50/E2E_Chat_App/tree/main/End-to-End_Encrypted_Chat_Server-main).

## Features
- User account registration, login and session management, account recovery, account settings updating, etc.
- Personal chats between any 2 user accounts including end-to-end encryption of chat messages, read state and online/offline indicators
- Group chats between any number of user accounts also including end-to-end encryption of messages, read state indicators, coloured participant names, adding new users and leaving chat
- Dynamic network implementation with detection and recovery from network issues
- Provide a clean, customizable, and fully functional GUI with support for a huge selection of themes

## Dependencies
1. [FlatLaf](https://github.com/JFormDesigner/FlatLaf) v3.2 -
   - core
   - extras
   - intellij themes
2. [SQLite JDBC](https://github.com/xerial/sqlite-jdbc) v3.43.0.0

## Project folder structure
The 3 folders included in this project contains the following -
- `lib` - contains the JAR files of external libraries used
- `resources` - contains the icons and animations used
- `src` - contains the source java files 

## How to use

### Compile and run -
1. Make sure JDK is installed and environment variables are set properly.
2. Download the entire repository code as ZIP, extract it and go into the extracted folder containing the sub-folders `lib`, `resources` and `src`.
3. Execute the following commands individually line by line -
```
cd src
jar xvf ..\lib\flatlaf-3.2.jar
jar xvf ..\lib\flatlaf-extras-3.2.jar
jar xvf ..\lib\flatlaf-intellij-themes-3.2.jar
jar xvf ..\lib\sqlite-jdbc-3.43.0.0.jar
javac com/chat/e2e/*.java
del /s *.java
copy ..\resources\*.png .
copy ..\resources\*.gif .
jar cvfe ChatClient.jar com.chat.e2e.Main .
cd..
move src\ChatClient.jar .
```
4. Execute the following command to run the client -
```
java -jar ChatClient.jar
```

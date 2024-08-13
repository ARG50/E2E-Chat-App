/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.chat.e2e;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 *
 * @author Soubhik
 */
public class ChatMessagesListSentTextComponent extends javax.swing.JPanel {
    private String messageID;

    public String getMessageID()
    {
        return messageID;
    }



    /**
     * Creates new form ChatMessagesListSentTextComponent
     */
    public ChatMessagesListSentTextComponent(String ID) {
        initComponents();
        sentMessageTextField.setContentType("text/html");
        messageID = ID;
        sentMessageTextField.setBackground(sentMessageTextField.getBackground().brighter());
        sentMessageTextField.setOpaque(true);
    }


    private String processLinkHighlighting(String originalMessage)
    {
        String messageTextOnlyWithoutHtml = originalMessage.replaceAll("<.*?>", "");
        //System.out.println(originalMessage + "\n" + messageTextOnlyWithoutHtml);
        String[] messageParts = originalMessage.split(" ");
        boolean[] isLink = new boolean[messageParts.length];
        for(int i = 0; i < messageParts.length; i++)
        {
            try
            {
                URL url = new URL(messageParts[i]);
                isLink[i] = true;
            }
            catch(Exception e)
            {
                isLink[i] = false;
            }
        }
        StringBuilder newMessage = new StringBuilder(originalMessage);
        int currentLastEditedIndex = 0;
        for(int i = 0; i < messageParts.length; i++)
        {
            if(isLink[i])
            {
                int initialStartingIndex = newMessage.indexOf(messageParts[i], currentLastEditedIndex);
                int finalEndingIndex = initialStartingIndex + messageParts[i].length();
                String startingHtmlTag = "<a href=\"" + messageParts[i] + "\">";
                String endingHtmlTag = "</a>";
                newMessage.insert(finalEndingIndex, endingHtmlTag);
                newMessage.insert(initialStartingIndex, startingHtmlTag);
                currentLastEditedIndex = initialStartingIndex + messageParts[i].length() + startingHtmlTag.length() + endingHtmlTag.length();
            }
        }
        return newMessage.toString();
    }


    private String processTextIntoMultipleLines(String originalMessage)
    {
        // precalculate approximate width of message bubble based on the following data -
        // ratio of message bubble width          message scroll list width          message bubble width          size of window(approx)
        // to message scroll list width
        //           0.40                                      504                           203                         800*600
        //           0.39                                      665                           257
        //           0.37                                      886                           331
        //           0.37                                      1095                          400
        //           0.36                                      1341                          482
        //           0.35                                      1640                          582                 fullscreen-1920xless than 1080
        // max possible difference between window width and scroll list width = 300 (approx safe distance)
        // safe ratio for message bubble width from scroll list width = 0.35
        // results -
        // actual message scroll list width                  estimated message bubble width                 size of window(approx)
        //             504                                                175                                      800*600
        //             666                                                231
        //             885                                                308
        //             1099                                               383
        //             1340                                               467
        //             1640                                               572                            fullscreen-1920xless than 1080
        // estimation gets closer to actual width as scroll list width increases
        // I could consider querying the scroll list width and then setting the width instead of using 0.35 everytime
        // but I think it is unnecessary as it looks decent in the UI and does what it needs to do
        // the main point of precalculating the width is to set the text as the message list is being created, and not after it is created and rendered
        // to get the actual width, rendering is required first, so pre-calculation is needed here to do the text setting before rendering
        int defaultWidthOfMessageField = (int)((Main.getMainFrame().getWidth() - 300) * 0.35); //sentMessageTextField.getWidth();
        //System.out.println(defaultWidthOfMessageField);

        FontMetrics fontMetrics = getFontMetrics(getFont());
        String messageTextOnlyWithoutHtml = originalMessage.replaceAll("<.*?>", "");
        int messageWidth = fontMetrics.stringWidth(messageTextOnlyWithoutHtml);

        if(messageWidth <= defaultWidthOfMessageField)
        {
            return originalMessage;
        }
        else {
            StringBuilder newMessage = new StringBuilder();
            StringBuilder currentLineText = new StringBuilder();
            //System.out.println(originalMessage.length());
            for (int i = 0; i < originalMessage.length(); i++) {
                //System.out.println(i);
                if(originalMessage.charAt(i) == '<')
                {
                    while(originalMessage.charAt(i) != '>')
                    {
                        newMessage.append(originalMessage.charAt(i));
                        i++;
                    }
                    newMessage.append(originalMessage.charAt(i));
                    if(newMessage.length() >= 4 && newMessage.lastIndexOf("<br>") != -1 && newMessage.lastIndexOf("<br>") == newMessage.length() - 4)
                    {
                        currentLineText = new StringBuilder();
                    }
                }
                else
                {
                    currentLineText.append(originalMessage.charAt(i));
                    if (fontMetrics.stringWidth(currentLineText.toString()) > defaultWidthOfMessageField) {
                        i--;
                        newMessage.append("<br>");
                        currentLineText = new StringBuilder();
                    }
                    else {
                        newMessage.append(originalMessage.charAt(i));
                    }
                }
            }
            return newMessage.toString();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        emptySideLabel1 = new javax.swing.JLabel();
        emptySideLabel2 = new javax.swing.JLabel();
        sentMessageTextField = new javax.swing.JTextPane();
        timestampBottomLabel = new javax.swing.JLabel();
        statusIconLabel = new javax.swing.JLabel();

        sentMessageTextField.setEditable(false);
        sentMessageTextField.setFocusable(false);
        sentMessageTextField.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        sentMessageTextField.setText("placeholder Sent Text ");
        sentMessageTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sentMessageTextFieldMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sentMessageTextFieldMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                sentMessageTextFieldMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                sentMessageTextFieldMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                sentMessageTextFieldMouseReleased(evt);
            }
        });

        timestampBottomLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        timestampBottomLabel.setText("placeholder Timestamp");
        timestampBottomLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                timestampBottomLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                timestampBottomLabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                timestampBottomLabelMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                timestampBottomLabelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                timestampBottomLabelMouseReleased(evt);
            }
        });
        timestampBottomLabel.addPropertyChangeListener("text", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                timestampBottomLabel.removePropertyChangeListener("text", this);
                formatTimestamp();
            }
        });

        statusIconLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        statusIconLabel.setIcon(new javax.swing.ImageIcon(Objects.requireNonNull(ChatMessagesListSentTextComponent.class.getClassLoader().getResource("time-left.png")))); // NOI18N
        statusIconLabel.setToolTipText("The icons mean the following:\nwaiting clock -> message yet to sent to server\nsingle red tick -> message sent to server, but not yet delivered to receiver\ndouble red tick -> message sent to server, and delivered to receiver, but not yet seen by receiver\ndouble green tick -> message sent to server, and delivered to receiver, and seen by receiver");
        statusIconLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                statusIconLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                statusIconLabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                statusIconLabelMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                statusIconLabelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                statusIconLabelMouseReleased(evt);
            }
        });

        sentMessageTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e))
                {
                    showMoreOptionsPopup(e);
                }
            }
        });
        sentMessageTextField.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    try
                    {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    }
                    catch(Exception ex)
                    {
                        //System.out.println(ex);
                    }
                }
            }
        });
//        sentMessageTextField.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                if(((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0) && e.getKeyCode() == KeyEvent.VK_C)
//                {
//                    if(sentMessageTextField.getSelectedText() != null) {
////                        System.out.println("test " + messageID);
////                        int selectedTextStartingIndex = sentMessageTextField.getSelectionStart();
////                        int selectedTextEndingIndex = sentMessageTextField.getSelectionEnd();
////                        HTMLDocument doc = (HTMLDocument) sentMessageTextField.getDocument();
////                        sentMessageTextField.setSelectionStart(doc.getCharacterElement(sentMessageTextField.getCaretPosition()).getParentElement().getStartOffset());
////                        sentMessageTextField.setSelectionEnd(doc.getCharacterElement(sentMessageTextField.getCaretPosition()).getParentElement().getEndOffset());
//                        String selectedMessage = sentMessageTextField.getSelectedText();
//                        String originalMessage = sentMessageTextField.getText();
//                        String[] selectedMessageParts = selectedMessage.split(" ");
//                        int startingIndexInOriginalMessage = originalMessage.indexOf(selectedMessageParts[0]);
//                        int endingIndexInOriginalMessage = originalMessage.indexOf(selectedMessageParts[selectedMessageParts.length - 1]) + selectedMessageParts[selectedMessageParts.length - 1].length();
//                        String originalSelectedMessage = originalMessage.substring(startingIndexInOriginalMessage, endingIndexInOriginalMessage);
//                        originalSelectedMessage = originalSelectedMessage.replaceAll("<.*?>", "");
//                        System.out.println(originalSelectedMessage);
//                    }
//                }
//            }
//        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(emptySideLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(emptySideLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                                                .addGap(63, 63, 63)
                                                .addComponent(sentMessageTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(300, 300, 300)
                                                .addComponent(timestampBottomLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(statusIconLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(2, 2, 2)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(emptySideLabel1)
                                                        .addComponent(emptySideLabel2)))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(sentMessageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(timestampBottomLabel)
                                                        .addComponent(statusIconLabel))))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>

    private void formatTimestamp()
    {
        LocalDate currentDate = LocalDate.now();
        LocalDate givenDate = (new Timestamp(Long.parseLong(timestampBottomLabel.getText()))).toLocalDateTime().toLocalDate();
        LocalTime givenTime = (new Timestamp(Long.parseLong(timestampBottomLabel.getText()))).toLocalDateTime().toLocalTime();
        if(givenDate.getYear() == currentDate.getYear() && givenDate.getMonthValue() == currentDate.getMonthValue() && givenDate.getDayOfMonth() == currentDate.getDayOfMonth())
        {
            timestampBottomLabel.setText(givenTime.format(DateTimeFormatter.ofPattern("hh:mm a")).toUpperCase());
        }
        else
        {
            timestampBottomLabel.setText(givenTime.format(DateTimeFormatter.ofPattern("hh:mm a")).toUpperCase() + " on " + givenDate.format(DateTimeFormatter.ofPattern("dd/MM/yy")));
        }
        timestampBottomLabel.setToolTipText(givenTime.format(DateTimeFormatter.ofPattern("hh:mm a")).toUpperCase() + " on " + givenDate.format(DateTimeFormatter.ofPattern("dd/MM/yy")));
    }

    private void sentMessageTextFieldMouseClicked(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void sentMessageTextFieldMouseEntered(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void sentMessageTextFieldMouseExited(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void sentMessageTextFieldMousePressed(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void sentMessageTextFieldMouseReleased(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void timestampBottomLabelMouseClicked(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void timestampBottomLabelMouseEntered(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void timestampBottomLabelMouseExited(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void timestampBottomLabelMousePressed(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void timestampBottomLabelMouseReleased(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void statusIconLabelMouseClicked(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void statusIconLabelMouseEntered(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void statusIconLabelMouseExited(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void statusIconLabelMousePressed(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void statusIconLabelMouseReleased(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void showMoreOptionsPopup(MouseEvent e)
    {
        moreOptionsPopup = new JPopupMenu();

        JMenuItem copyOption = new JMenuItem("Copy");
        copyOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sentMessageTextField.getText().replaceAll("<.*?>", "")), null);

                ChatMainPanel parent = WindowManager.getChatPanelReference();
                if(parent != null && parent.getActiveChatComponentReference() != null)
                {
                    String chatID = parent.getActiveChatComponentReference().getChatID();
                    String[][] message = DatabaseManager.makeQuery("select message_content from chat" + chatID + " where message_id = " + messageID + ";", null);
                    if(message != null && message.length > 0)
                    {
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(message[0][0]), null);
                    }
                }
            }
        });
        moreOptionsPopup.add(copyOption);

        JMenuItem deleteOption = new JMenuItem("Delete");
        deleteOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(Main.getMainFrame(), "Are you sure you want to delete this message?\nIf already sent, then it will only be deleted for you.\nOthers will still be able to see this message.", "Delete message", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if(response != JOptionPane.YES_OPTION)
                    return;
                ChatMainPanel chatPanelReference = WindowManager.getChatPanelReference();
                if(chatPanelReference != null)
                {
                    chatPanelReference.deleteParticularMessageInActiveChat(messageID);
                }
            }
        });
        moreOptionsPopup.add(deleteOption);

        moreOptionsPopup.show(sentMessageTextField, e.getX(), e.getY());
    }


    public String getMessage()
    {
        return sentMessageTextField.getText();
    }

    public void setMessage(String message)
    {
        sentMessageTextField.setText(processTextIntoMultipleLines(processLinkHighlighting(message)));
    }

    public JLabel getMessageTimestampLabel()
    {
        return timestampBottomLabel;
    }

    public JLabel getStatusIconLabel()
    {
        return statusIconLabel;
    }

    // Variables declaration - do not modify
    private javax.swing.JLabel emptySideLabel1;
    private javax.swing.JLabel emptySideLabel2;
    private javax.swing.JTextPane sentMessageTextField;
    private javax.swing.JLabel statusIconLabel;
    private javax.swing.JLabel timestampBottomLabel;
    private JPopupMenu moreOptionsPopup;
    // End of variables declaration
}

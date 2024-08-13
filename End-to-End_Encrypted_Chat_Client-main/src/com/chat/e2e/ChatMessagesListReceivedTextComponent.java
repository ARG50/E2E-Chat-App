/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.chat.e2e;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Soubhik
 */
public class ChatMessagesListReceivedTextComponent extends javax.swing.JPanel {

    private String messageID;

    public String getMessageID()
    {
        return messageID;
    }

    /**
     * Creates new form ChatMessagesListReceivedTestComponent
     */
    public ChatMessagesListReceivedTextComponent(String ID) {
        initComponents();
        receivedMessageTextLabel.setContentType("text/html");
        messageID = ID;
        //receivedTextLabel.setText("<html>placeholderReceivedText");
        //receivedTextLabel.setPreferredSize(new java.awt.Dimension(135, 15));
        //receivedTextLabel.setSize(receivedTextLabel.getPreferredSize());
        receivedMessageTextLabel.setBackground(receivedMessageTextLabel.getBackground().brighter());
        receivedMessageTextLabel.setOpaque(true);
//        timestampBottomLabel.setBackground(timestampBottomLabel.getBackground().brighter());
//        timestampBottomLabel.setOpaque(true);
    }

    private String processLinkHighlighting(String originalMessage)
    {
        String messageTextOnlyWithoutHtml = originalMessage.replaceAll("<.*?>", "");
        //System.out.println(originalMessage + "\n" + messageTextOnlyWithoutHtml);
        String[] messageParts = messageTextOnlyWithoutHtml.split(" ");
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
        //           0.42                                      504                           210                         800*600
        //           0.40                                      666                           264
        //           0.38                                      886                           337
        //           0.37                                      1093                          406
        //           0.36                                      1339                          488
        //           0.36                                      1640                          588                 fullscreen-1920xless than 1080
        // max possible difference between window width and scroll list width = 300 (approx safe distance)
        // safe ratio for message bubble width from scroll list width = 0.36
        // results -
        // actual message scroll list width                  estimated message bubble width                 size of window(approx)
        //             504                                                180                                      800*600
        //             665                                                237
        //             883                                                316
        //             1092                                               391
        //             1339                                               480
        //             1640                                               588                            fullscreen-1920xless than 1080
        // estimation gets closer to actual width as scroll list width increases
        // I could consider querying the scroll list width and then setting the width instead of using 0.36 everytime
        // but I think it is unnecessary as it looks decent in the UI and does what it needs to do
        // the main point of precalculating the width is to set the text as the message list is being created, and not after it is created and rendered
        // to get the actual width, rendering is required first, so pre-calculation is needed here to do the text setting before rendering
        int defaultWidthOfMessageField = (int)((Main.getMainFrame().getWidth() - 300) * 0.36); //receivedMessageTextLabel.getWidth();
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

        receivedMessageTextLabel = new javax.swing.JTextPane();
        emptySideLabel2 = new javax.swing.JLabel();
        emptySideLabel1 = new javax.swing.JLabel();
        timestampBottomLabel = new javax.swing.JLabel();

        receivedMessageTextLabel.setEditable(false);
        receivedMessageTextLabel.setFocusable(false);
        receivedMessageTextLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        receivedMessageTextLabel.setText("placeholder Received Text ");
        receivedMessageTextLabel.setAutoscrolls(false);
        receivedMessageTextLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        receivedMessageTextLabel.setMargin(new java.awt.Insets(2, 2, 2, 2));
        receivedMessageTextLabel.setOpaque(false);
        receivedMessageTextLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                receivedMessageTextLabelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                receivedMessageTextLabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                receivedMessageTextLabelMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                receivedMessageTextLabelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                receivedMessageTextLabelMouseReleased(evt);
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

        receivedMessageTextLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e))
                {
                    showMoreOptionsPopup(e);
                }
            }
        });
        receivedMessageTextLabel.addHyperlinkListener(new HyperlinkListener() {
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(receivedMessageTextLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(75, 75, 75)
                                                .addComponent(timestampBottomLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(28, 28, 28)
                                .addComponent(emptySideLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emptySideLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(receivedMessageTextLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(timestampBottomLabel)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(emptySideLabel2)
                                        .addComponent(emptySideLabel1))
                                .addGap(0, 0, Short.MAX_VALUE))
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

    private void receivedMessageTextLabelMouseClicked(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void receivedMessageTextLabelMouseEntered(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void receivedMessageTextLabelMouseExited(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void receivedMessageTextLabelMousePressed(java.awt.event.MouseEvent evt) {
        // TODO add your handling code here:
    }

    private void receivedMessageTextLabelMouseReleased(java.awt.event.MouseEvent evt) {
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

    private void showMoreOptionsPopup(MouseEvent e)
    {
        moreOptionsPopup = new JPopupMenu();

        JMenuItem copyOption = new JMenuItem("Copy");
        copyOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(receivedMessageTextLabel.getText().replaceAll("<.*?>", "")), null);

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
                int response = JOptionPane.showConfirmDialog(Main.getMainFrame(), "Are you sure you want to delete this message?\nIt will only be deleted for you.\nOthers will still be able to see this message.", "Delete message", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
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

        moreOptionsPopup.show(receivedMessageTextLabel, e.getX(), e.getY());
    }

    public String getMessage()
    {
        return receivedMessageTextLabel.getText();
    }

    public void setMessage(String message)
    {
        receivedMessageTextLabel.setText(processTextIntoMultipleLines(processLinkHighlighting(message)));
    }

    public StyledDocument getMessagePaneStyledDocument()
    {
        return receivedMessageTextLabel.getStyledDocument();
    }

//    public void processSetMessageThroughStyledDocument()
//    {
//        System.out.println(receivedMessageTextLabel.getText());
//        setMessage(receivedMessageTextLabel.getText());
//        System.out.println(receivedMessageTextLabel.getText());
//    }

    public JLabel getMessageTimestampLabel()
    {
        return timestampBottomLabel;
    }

    // Variables declaration - do not modify
    private javax.swing.JLabel emptySideLabel1;
    private javax.swing.JLabel emptySideLabel2;
    private javax.swing.JTextPane receivedMessageTextLabel;
    private javax.swing.JLabel timestampBottomLabel;
    private JPopupMenu moreOptionsPopup;
    // End of variables declaration
}

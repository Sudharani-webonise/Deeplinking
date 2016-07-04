package com.webonise.deeplinking.listener;


import java.io.IOException;
import java.util.Properties;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MailListener is includes the listener
 * is creates the session by setting up a properties and creates the thread
 * and makes the thread listen the inbox folder.
 *
 */
public class MailListener {
    private static final Logger logger = LoggerFactory.getLogger(MailListener.class);
    public static final String IMAPS = "imaps";
    public static final String HOST = "imap.gmail.com";
    public static final String PORT = "993";
    public static final String TIMEOUT = "60000";
    public static final String INBOX = "INBOX";

    private static String username = "pibappauthentication@gmail.com"; //change accordingly
    private static String password = "pibapp123#";  //change accordingly


    public static void main(String[] args) {

        MailListener mailListener = new MailListener();

        Session session = mailListener.getSession();
        IMAPStore store = null;
        Folder inbox = null;

        try {
            store = (IMAPStore) session.getStore(IMAPS);
            store.connect(username, password);

            // get from INBOX folder
            inbox = (IMAPFolder) store.getFolder(INBOX);

            // adding listener to the inbox i,e which listen continuously and logs the message details when messaged added to inbox
            inbox.addMessageCountListener(new MessageCountAdapter()  {
                @Override
                public void messagesAdded(MessageCountEvent event) {
                    Message[] messages = event.getMessages();
                    for (Message message : messages) {
                        try {
                            logger.info("Email sent on : {}", message.getSentDate() );
                            logger.info("Subject : {} ",  message.getSubject());
                            logger.info("From : {} ", message.getFrom()[0]);
                            logger.info("Text : {} ", message.getContent());
                        } catch (MessagingException | IOException ex) {
                           logger.error("ERROR: in reading message {} ", ex);
                        }
                    }
                }
            });

            // creates the thread
            MailThread idleThread = new MailThread(inbox);

            // thread is made demon because the demon thread always terminates when the jvm terminates
            idleThread.setDaemon(false);

            // started the thread which invokes the run method in MailThread class
            idleThread.start();

            // made to waits for another thread to complete
            idleThread.join();

        }catch (InterruptedException | MessagingException ex) {
            logger.error("Thread is interrupted or error during messaging : ",ex);
        }  finally {
            logger.debug("Close the folder and IMAPStore");
            mailListener.closeFolderAndStore(inbox,store);
        }
    }


    /**
     * getSession method used to set the properties of imap host port prtocal and timeout
     * set the properties accordingly
     *
     * @return Session
     *
     */

    private Session getSession() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", IMAPS);
        properties.put("mail.imaps.host", HOST);
        properties.put("mail.imaps.port", PORT);
        properties.put("mail.imaps.timeout", TIMEOUT);
        return Session.getInstance(properties);
    }



    /**
     * this method used to close the folder and store
     * when thread is killed or stopped
     *
     * @param folder store
     *
     */
    public void closeFolderAndStore(final Folder folder, final Store store) {
        try {
            if (folder != null && folder.isOpen()) {
                folder.close(false);
                if (store != null && store.isConnected()) {
                    store.close();
                }
            }
        } catch (Exception ex) {
            logger.error("ERROR: while closing inbox folder ow IMAP Store : ",ex);
        }

    }

    /***
     * this method always ensured the inbox folder is opened or not
     * if closed then it will open the inbox folder to read the message in READ_ONLY option
     * */

    public void ensureFolderOpen(final Folder folder) throws MessagingException {
        if (folder != null) {
            Store store = folder.getStore();
            if (store != null && !store.isConnected()) {
                store.connect(username, password);
            }
        } else {
            throw new MessagingException("ERROR : invalid folder");
        }

        if (folder.exists() && !folder.isOpen() && (folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
            logger.debug("open folder ", folder.getFullName());
            folder.open(Folder.READ_ONLY);
            if (!folder.isOpen())
                throw new MessagingException("ERROR: Unable to open folder " + folder.getFullName());
        }

    }
}
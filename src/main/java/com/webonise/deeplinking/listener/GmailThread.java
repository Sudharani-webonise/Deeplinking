package com.webonise.deeplinking.listener;

import com.sun.mail.imap.IMAPFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Folder;

/**
 * GmailThread is thread class which is used for create run kill the thread
 */
class GmailThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(GmailThread.class);
    private final Folder folder;
    private volatile boolean running = true;
    private GmailIncomingMailListener mailListener = new GmailIncomingMailListener();

    /**
     * This parametrised constructor used to create thread
     *
     * @param folder
     */
    public GmailThread(Folder folder) {
        super();
        this.folder = folder;
    }

    /**
     * This method invokes automatically when thread is started
     * if in case the folder is closed then it always ensures the it has to be opened
     */
    @Override
    public void run() {
        while (running) {
            try {
                mailListener.ensureFolderOpen(folder);
                logger.info("mail listener is in idle state");
                ((IMAPFolder) folder).idle();
            } catch (Exception e) {
                logger.error("Exception during onpening folder ", e);
            }

        }
    }

}

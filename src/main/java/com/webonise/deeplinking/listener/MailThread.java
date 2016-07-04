package com.webonise.deeplinking.listener;

import com.sun.mail.imap.IMAPFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Folder;

/**
 * MailThread is thread class which is used for create run kill the thread
 */
class MailThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(MailThread.class);
    private final Folder folder;
    private volatile boolean running = true;
    private MailListener mailListener = new MailListener();

    /**
     * This parametrised constructor used to create thread
     *
     * @param folder
     */
    public MailThread(Folder folder) {
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
                logger.error("Exception during opening folder : ", e);
            }

        }
    }

}

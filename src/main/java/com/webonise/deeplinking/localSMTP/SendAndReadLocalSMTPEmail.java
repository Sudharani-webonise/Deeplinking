package com.webonise.deeplinking.localSMTP;

import com.sun.xml.internal.bind.v2.TODO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendAndReadLocalSMTPEmail {

    private static final Logger logger = LoggerFactory.getLogger(SendAndReadLocalSMTPEmail.class);

    public static void main(String[] args) throws AddressException, MessagingException, InterruptedException {

        SendAndReadLocalSMTPEmail mail = new SendAndReadLocalSMTPEmail();
        String emailId = ""; // email id
        try {
            mail.sendMail(emailId);
            logger.info("mail sent to {}", emailId);
        } catch (Exception e) {
            logger.error("Error in sending mail {} ", e);
        }
        try {
            mail.readMail();
        } catch (Exception e) {
            logger.error("Error in reading mail {} ", e);
        }
    }


    /***
     * this method is refers that mail is received locally
     * it works only when we send mail from local system
     */
    private void readMail() throws InterruptedException {

        /***
         * TODO:
         * set up the server as per the requirement using mail severs which can read the mail from inbox
         * then use the {@link}  GmailIncomingMailListener.class
         * to configure in Net magic domain mail server
         */

        String[] command = {"/bin/bash", "-c", "echo password | sudo -S mail -u user"}; //password and user change accordingly
        Process process;
        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line = "";
            System.out.println(process.getInputStream());
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * this method used to send mail from local system
     *
     * @param recipientEmailId is mail id
     */
    void sendMail(String recipientEmailId) throws AddressException,
            MessagingException, InterruptedException {
        Properties properties = System.getProperties();
        Session session = Session.getDefaultInstance(properties, null);
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("no-reply@localnm.com"));
        message.setSubject("local smtp testing");
        message.setContent("CONTENT : hey", "text/html");

        Address[] replyTo = {new InternetAddress("uniquid@localnm.com")};
        message.setReplyTo(replyTo);
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                recipientEmailId));
        Transport.send(message);
    }
}
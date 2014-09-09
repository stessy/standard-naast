// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:22
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Email.java
package standardNaast.utils;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;

public class Email {

    class ByteArrayDataSource
            implements DataSource {

        public String getContentType() {
            return contentType;
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(bytes, 0, bytes.length);
        }

        public String getName() {
            return name;
        }

        public OutputStream getOutputStream()
                throws IOException {
            throw new FileNotFoundException();
        }
        byte bytes[];
        String contentType;
        String name;
        final Email this$0;

        ByteArrayDataSource(byte bytes[], String contentType, String name) {
            this$0 = Email.this;
            //super();
            this.bytes = bytes;
            if (contentType == null) {
                this.contentType = "application/octet-stream";
            } else {
                this.contentType = contentType;
            }
            this.name = name;
        }
    }

    private class SMTPAuthenticator extends Authenticator {

        public PasswordAuthentication getPasswordAuthentication() {
            String username_auth = Email.ra$username(Email.this);
            String password_auth = Email.ra$password(Email.this);
            return new PasswordAuthentication(username_auth, password_auth);
        }
        final Email this$0;

        private SMTPAuthenticator() {
            this$0 = Email.this;
            //super();
        }
    }

    private void jbInit() {
        session = null;
        message = null;
        multipartContent = null;
        mailServer = null;
        username = null;
        password = null;
    }

    public Email(String mailServerIP, String user, String pass) {
        jbInit();
        Properties props = System.getProperties();
        props.put("mail.smtp.host", mailServerIP);
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.port", "25");
        props.put("mail.transport.protocol", "smtp");
        Authenticator auth = new SMTPAuthenticator();
        session = Session.getInstance(props, auth);
        message = new MimeMessage(session);
        mailServer = mailServerIP;
        username = user;
        password = pass;
        multipartContent = new MimeMultipart();
        try {
            message.setHeader("X-Mailer", "miniSMTP (JavaMAIL) 0.2.031210 (c) Intrasoft International SA");
        } catch (MessagingException e) {
        }
        session.setDebug(true);
    }

    public synchronized void send(String senderName, String sender, String subject, String body)
            throws Exception {
        message.setFrom(new InternetAddress(sender, senderName));
        message.setSubject(subject, "UTF-8");
        message.setText(body, "UTF-8");
        message.setSentDate(new Date());
        message.setHeader("X-Priority", "1 (Highest)");
        message.setHeader("X-MSMail-Priority", "High");
        message.setHeader("Importance", "High");
        try {
            Transport.send(message, message.getAllRecipients());
        } catch (SendFailedException e) {
            e.printStackTrace();
            throw new Exception((new StringBuilder()).append("Message SEND failed:\nInvalid Address(es): ").append(InternetAddress.toString(e.getInvalidAddresses())).append(" ").append("\nValid/Sent Address(es): ").append(InternetAddress.toString(e.getValidSentAddresses())).append(" ").append("\nValid/Unsent Addresses(es): ").append(InternetAddress.toString(e.getValidUnsentAddresses())).toString());
        }
    }

    public synchronized void sendWithAuthentification(String senderName, String sender, String subject, String body)
            throws Exception {
        message.setFrom(new InternetAddress(sender, senderName));
        message.setSubject(subject, "UTF-8");
        BodyPart messageBodyPart = new MimeBodyPart();
        ((MimeBodyPart) messageBodyPart).setText(body, "UTF-8");
        messageBodyPart.setHeader("Content-Transfer-Encoding", "base64");
        multipartContent.addBodyPart(messageBodyPart, 0);
        message.setContent(multipartContent);
        message.setSentDate(new Date());
        message.setHeader("X-Priority", "1 (Highest)");
        message.setHeader("X-MSMail-Priority", "High");
        message.setHeader("Importance", "High");
        Transport tr = null;
        try {
            tr = session.getTransport("smtp");
            tr.connect();
            message.saveChanges();
            Transport.send(message, message.getAllRecipients());
            tr.close();

        } catch (SendFailedException e) {
            if (tr != null) {
                tr.close();
            }
//        	e.printStackTrace();
//        	
//            throw new Exception((new StringBuilder()).append("Message SEND failed:\nInvalid Address(es): ").append(InternetAddress.toString(e.getInvalidAddresses())).append(" ").append("\nValid/Sent Address(es): ").append(InternetAddress.toString(e.getValidSentAddresses())).append(" ").append("\nValid/Unsent Addresses(es): ").append(InternetAddress.toString(e.getValidUnsentAddresses())).toString());
        } catch (MessagingException me) {

            if (tr != null) {
                tr.close();
            }
            me.printStackTrace();
        }
    }

    public void addReceipientTo(String name, String address)
            throws Exception {
        message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(address, name));
    }

    public void addReceipientCc(String name, String address)
            throws Exception {
        message.addRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(address, name));
    }

    public void addReceipientBcc(String name, String address)
            throws Exception {
        message.addRecipient(javax.mail.Message.RecipientType.BCC, new InternetAddress(address, name));
    }

    public static void main(String args[]) {
        try {
            String server = "poincare";
            String fromEmail = "thanos.agelatos@intrasoft-intl.com";
            System.out.println(" email <param1:host> <param2:fromaddress>");
            if (args.length >= 1) {
                if (args.length > 1) {
                    fromEmail = args[1];
                }
                server = args[0];
            } else {
                System.out.println((new StringBuilder()).append("warning! using default ").append(server).toString());
            }
            Email email = new Email(server, null, null);
            email.addReceipientTo("Thanos Agelatos", "thanos.agelatos@intrasoft-intl.com");
            email.send("Thanos Agelatos (Sender)", fromEmail, (new StringBuilder()).append("Test email please ignore (").append(new Date()).append(")").toString(), "Please tell me when you receive this!\n\nthanos\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addAttachment(String fileName, byte fileContent[])
            throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new ByteArrayDataSource(fileContent, "application/octet-stream", fileName);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(fileName);
        multipartContent.addBodyPart(messageBodyPart);
    }

    static String ra$username(Email email) {
        return email.username;
    }

    static String ra$password(Email email) {
        return email.password;
    }
    protected static final String VERSION_HEADER = "miniSMTP (JavaMAIL) 0.2.031210 (c) Intrasoft International SA";
    private Session session;
    private MimeMessage message;
    private Multipart multipartContent;
    private String mailServer;
    private String username;
    private String password;
    private static final String SMTP_PORT = "25";
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
}
// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:22
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Email.java
package standardNaast.utils;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Email {

	class ByteArrayDataSource implements DataSource {

		@Override
		public String getContentType() {
			return this.contentType;
		}

		@Override
		public InputStream getInputStream() {
			return new ByteArrayInputStream(this.bytes, 0, this.bytes.length);
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public OutputStream getOutputStream() throws IOException {
			throw new FileNotFoundException();
		}

		byte bytes[];

		String contentType;

		String name;

		final Email this$0;

		ByteArrayDataSource(final byte bytes[], final String contentType, final String name) {
			this.this$0 = Email.this;
			// super();
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

		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			final String username_auth = Email.ra$username(Email.this);
			final String password_auth = Email.ra$password(Email.this);
			return new PasswordAuthentication(username_auth, password_auth);
		}

		final Email this$0;

		private SMTPAuthenticator() {
			this.this$0 = Email.this;
			// super();
		}
	}

	private void jbInit() {
		this.session = null;
		this.message = null;
		this.multipartContent = null;
		this.mailServer = null;
		this.username = null;
		this.password = null;
	}

	public Email(final String mailServerIP, final String user, final String pass) {
		this.jbInit();
		final Properties props = System.getProperties();
		props.put("mail.smtp.host", mailServerIP);
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.smtp.port", "25");
		props.put("mail.transport.protocol", "smtp");
		final Authenticator auth = new SMTPAuthenticator();
		this.session = Session.getInstance(props, auth);
		this.message = new MimeMessage(this.session);
		this.mailServer = mailServerIP;
		this.username = user;
		this.password = pass;
		this.multipartContent = new MimeMultipart();
		try {
			this.message.setHeader("X-Mailer", "miniSMTP (JavaMAIL) 0.2.031210 (c) Intrasoft International SA");
		} catch (final MessagingException e) {
		}
		this.session.setDebug(true);
	}

	public synchronized void send(final String senderName, final String sender, final String subject, final String body)
			throws Exception {
		this.message.setFrom(new InternetAddress(sender, senderName));
		this.message.setSubject(subject, "UTF-8");
		this.message.setText(body, "UTF-8");
		this.message.setSentDate(new Date());
		this.message.setHeader("X-Priority", "1 (Highest)");
		this.message.setHeader("X-MSMail-Priority", "High");
		this.message.setHeader("Importance", "High");
		try {
			Transport.send(this.message, this.message.getAllRecipients());
		} catch (final SendFailedException e) {
			e.printStackTrace();
			throw new Exception((new StringBuilder()).append("Message SEND failed:\nInvalid Address(es): ")
					.append(InternetAddress.toString(e.getInvalidAddresses())).append(" ")
					.append("\nValid/Sent Address(es): ").append(InternetAddress.toString(e.getValidSentAddresses()))
					.append(" ").append("\nValid/Unsent Addresses(es): ")
					.append(InternetAddress.toString(e.getValidUnsentAddresses())).toString());
		}
	}

	public synchronized void sendWithAuthentification(final String senderName, final String sender,
			final String subject, final String body) throws Exception {
		this.message.setFrom(new InternetAddress(sender, senderName));
		this.message.setSubject(subject, "UTF-8");
		final BodyPart messageBodyPart = new MimeBodyPart();
		((MimeBodyPart) messageBodyPart).setText(body, "UTF-8");
		messageBodyPart.setHeader("Content-Transfer-Encoding", "base64");
		this.multipartContent.addBodyPart(messageBodyPart, 0);
		this.message.setContent(this.multipartContent);
		this.message.setSentDate(new Date());
		this.message.setHeader("X-Priority", "1 (Highest)");
		this.message.setHeader("X-MSMail-Priority", "High");
		this.message.setHeader("Importance", "High");
		Transport tr = null;
		try {
			tr = this.session.getTransport("smtp");
			tr.connect();
			this.message.saveChanges();
			Transport.send(this.message, this.message.getAllRecipients());
			tr.close();

		} catch (final SendFailedException e) {
			if (tr != null) {
				tr.close();
			}
			// e.printStackTrace();
			//
			// throw new Exception((new
			// StringBuilder()).append("Message SEND failed:\nInvalid Address(es): ").append(InternetAddress.toString(e.getInvalidAddresses())).append(" ").append("\nValid/Sent Address(es): ").append(InternetAddress.toString(e.getValidSentAddresses())).append(" ").append("\nValid/Unsent Addresses(es): ").append(InternetAddress.toString(e.getValidUnsentAddresses())).toString());
		} catch (final MessagingException me) {

			if (tr != null) {
				tr.close();
			}
			me.printStackTrace();
		}
	}

	public void addReceipientTo(final String name, final String address) throws Exception {
		this.message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(address, name));
	}

	public void addReceipientCc(final String name, final String address) throws Exception {
		this.message.addRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(address, name));
	}

	public void addReceipientBcc(final String name, final String address) throws Exception {
		this.message.addRecipient(javax.mail.Message.RecipientType.BCC, new InternetAddress(address, name));
	}

	public static void main(final String args[]) {
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
			final Email email = new Email(server, null, null);
			email.addReceipientTo("Thanos Agelatos", "thanos.agelatos@intrasoft-intl.com");
			email.send("Thanos Agelatos (Sender)", fromEmail, (new StringBuilder())
					.append("Test email please ignore (").append(new Date()).append(")").toString(),
					"Please tell me when you receive this!\n\nthanos\n");
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void addAttachment(final String fileName, final byte fileContent[]) throws Exception {
		final BodyPart messageBodyPart = new MimeBodyPart();
		final DataSource source = new ByteArrayDataSource(fileContent, "application/octet-stream", fileName);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(fileName);
		this.multipartContent.addBodyPart(messageBodyPart);
	}

	static String ra$username(final Email email) {
		return email.username;
	}

	static String ra$password(final Email email) {
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
/**
 * 
 */
package standardNaast.utils;

/**
 * @author stessy
 *
 */
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import standardNaast.entities.Personne;

public class SendMailTLS {

	private Properties emailProperties;

	private Properties generalProperties;

	private final String username;

	private final String password;

	private final String smtpHost;

	private final String smtpPort;

	private final String mailFrom;

	private final String nameFrom;

	private Session session;

	private Multipart multipartContent;

	private final String emailTitle;

	private final String emailBody;

	private final List<Personne> recipients;

	private Message message;


	public SendMailTLS(final String emailTitle, final String emailBody, final List<Personne> recipients) {
		File f = new File("config.txt");
		try {
			this.generalProperties.load(new FileInputStream(f.getAbsolutePath()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.emailTitle = emailTitle;
		this.emailBody = emailBody;
		this.recipients = recipients;
		this.username = this.generalProperties.getProperty("mail_username");
		this.password = this.generalProperties.getProperty("mail_password");
		this.smtpHost = this.generalProperties.getProperty("smtpMailServer");
		this.smtpPort = this.generalProperties.getProperty("smtpMailPort");
		this.mailFrom = this.generalProperties.getProperty("mail_from");
		this.nameFrom = this.generalProperties.getProperty("name_from");
		this.loadProperties();
		this.getSessionInstance();
		this.sendEmail();
	}


	private void sendEmail() {
		try {
			this.message = new MimeMessage(this.session);
			this.message.setFrom(new InternetAddress(this.mailFrom, this.nameFrom));
			int counter = 0;
			for (Personne recipient : this.recipients) {
				this.addReceipientTo(recipient.getFirstname() + " " + recipient.getName(), recipient.getEmail());
				counter++;
				if (counter % 98 == 0) {
					Transport transport = this.session.getTransport();
					transport.connect();
					BodyPart messageBodyPart = new MimeBodyPart();
					((MimeBodyPart) messageBodyPart).setText(this.emailBody, "UTF-8");
					messageBodyPart.setHeader("Content-Transfer-Encoding", "base64");
					this.multipartContent.addBodyPart(messageBodyPart, 0);
					this.message.setContent(this.multipartContent);
					this.message.setSubject(this.emailTitle);
					Transport.send(this.message);
					transport.close();
					this.message = new MimeMessage(this.session);
					this.message.setFrom(new InternetAddress(this.mailFrom));
				}
			}

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

	}


	private void loadProperties() {
		this.emailProperties = new Properties();
		this.emailProperties.put("mail.smtp.auth", "true");
		this.emailProperties.put("mail.smtp.starttls.enable", "true");
		this.emailProperties.put("mail.smtp.host", this.smtpHost);
		this.emailProperties.put("mail.smtp.port", this.smtpPort);
	}


	private void getSessionInstance() {
		this.session = Session.getInstance(this.emailProperties, new javax.mail.Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(SendMailTLS.this.username, SendMailTLS.this.password);
			}
		});
	}


	public static void main(final String[] args) {

	}


	private byte[] readCompleteFile(final File aFile) {
		RandomAccessFile raf = null;
		byte buffer[] = null;
		if (!aFile.exists()) {
			System.out.println((new StringBuilder()).append("readFile failure:").append(aFile.getAbsolutePath())
					.append(" does not exist or cannot read!").toString());
		} else {
			try {
				raf = new RandomAccessFile(aFile, "r");
				int len = (int) raf.length();
				buffer = new byte[len];
				raf.readFully(buffer);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (raf != null) {
				try {
					raf.close();
				} catch (Exception exception1) {
				}
			}
		}
		return buffer;
	}


	public void addAttachment(final String fileName, final byte fileContent[]) throws Exception {
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new ByteArrayDataSource(fileContent, "application/octet-stream", fileName);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(fileName);
		this.multipartContent.addBodyPart(messageBodyPart);
	}


	public void addReceipientTo(final String name, final String address) throws UnsupportedEncodingException,
			MessagingException {
		this.message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(address, name));
	}


	public void addReceipientCc(final String name, final String address) throws Exception {
		this.message.addRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(address, name));
	}


	public void addReceipientBcc(final String name, final String address) throws Exception {
		this.message.addRecipient(javax.mail.Message.RecipientType.BCC, new InternetAddress(address, name));
	}

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


		ByteArrayDataSource(final byte bytes[], final String contentType, final String name) {
			this.bytes = bytes;
			if (contentType == null) {
				this.contentType = "application/octet-stream";
			} else {
				this.contentType = contentType;
			}
			this.name = name;
		}
	}
}
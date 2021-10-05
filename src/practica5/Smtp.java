package practica5;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Smtp extends Thread {
	
	private String user;
	private String password;
	private InformationService iS;
	// contraseña : correoppc
	
	public Smtp(InformationService iS) {
		this.user = "jaimeortizappc@gmail.com";
		this.password = "correoppc";
		this.iS = iS;
	}
	
	public void sendMessage(String from) {
		try {
			
			Properties props = new Properties();
			props.put("mail.smtp.host","smtp.gmail.com");
			props.setProperty("mail.smtp.user", user);
			props.setProperty("mail.smtp.clave", password);
			props.setProperty("mail.smtp.auth", "true");
			props.setProperty("mail.smtp.starttls.enable", "true");
			props.setProperty("mail.smtp.port", "587");
			
			Session session = Session.getInstance(props, null);
			
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(user));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(from));
			message.setSubject("Data");
			
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(iS.getLastData(), "text/plain");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);
			message.setContent(multipart);
			
			if (iS.getLastMessage() != null) {
				File file = null;
				String stringMsg = new String();
				TranslatorBroadcast tBroadcast = new TranslatorBroadcast();
				if (iS.getFormat().equals("XML")) {
					file = new File("xmlMessage.xml");
					stringMsg = tBroadcast.javaToXML(iS.getLastMessage());
				}
				else if (iS.getFormat().equals("JSON")) {
					file = new File("jsonMessage.json");
					stringMsg = tBroadcast.javaToJson(iS.getLastMessage());
				}
				
				BufferedWriter bWriter = new BufferedWriter(new FileWriter(file));
				bWriter.write(stringMsg);
				bWriter.close();

				MimeBodyPart attachedFile = new MimeBodyPart();
				attachedFile.attachFile(file);
				multipart.addBodyPart(attachedFile);
				
			}
			
			message.saveChanges();
			Transport transport = session.getTransport("smtps");
			transport.connect("smtp.gmail.com", user, password);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			
			//Transport.send(message);
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		while (true) {
			
			Properties props = new Properties();
			props.setProperty("mail.pop3.starttls.enable", "false");
			props.setProperty("mail.pop3.socketFactory.class","javax.net.ssl.SSLSocketFactory" );
			props.setProperty("mail.pop3.socketFactory.fallback", "false");
			props.setProperty("mail.pop3.port","995");
			props.setProperty("mail.pop3.socketFactory.port", "995");
			
			Session session = Session.getDefaultInstance(props);
			
			Store store = null;
			try {
				store = session.getStore("pop3");
			} catch (NoSuchProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				store.connect("pop.gmail.com", user, password);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Folder folder = null;
			try {
				folder = store.getFolder("INBOX");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				folder.open(Folder.READ_WRITE);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Message[] messages = null;
			try {
				messages = folder.getMessages();
				//messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (int i = 0; i < messages.length; i++) {
				
				String from = new String();
				try {
					from = ((InternetAddress)messages[i].getFrom()[0]).getAddress();
				} catch (MessagingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					if (messages[i].getSubject().equals("Request")) {
						this.sendMessage(from);
						messages[i].setFlag(Flags.Flag.DELETED, true);
					}
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				folder.close();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				store.close();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
}


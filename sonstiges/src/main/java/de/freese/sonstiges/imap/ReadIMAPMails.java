/**
 * Created: 03.10.2011
 */

package de.freese.sonstiges.imap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.ReadOnlyFolderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.StoreClosedException;
import javax.mail.internet.InternetAddress;

/**
 * @author Thomas Freese
 */
public class ReadIMAPMails
{
	/**
	 * @author Thomas Freese
	 */
	private abstract class AbstractTextPart
	{
		/**
		 * 
		 */
		private final String text;

		/**
		 * Erstellt ein neues {@link AbstractTextPart} Object.
		 * 
		 * @param text String
		 */
		private AbstractTextPart(final String text)
		{
			super();

			this.text = text;
		}

		/**
		 * @return String
		 */
		public String getText()
		{
			return this.text;
		}
	}

	/**
	 * @author Thomas Freese
	 */
	private class HTMLTextPart extends AbstractTextPart
	{
		/**
		 * Erstellt ein neues {@link HTMLTextPart} Object.
		 * 
		 * @param text String
		 */
		private HTMLTextPart(final String text)
		{
			super(text);
		}
	}

	/**
	 * @author Thomas Freese
	 */
	private class PlainTextPart extends AbstractTextPart
	{
		/**
		 * Erstellt ein neues {@link PlainTextPart} Object.
		 * 
		 * @param text String
		 */
		private PlainTextPart(final String text)
		{
			super(text);
		}
	}

	/**
	 * Main Function for The readEmail Class
	 * 
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		// Creating new readEmail Object
		ReadIMAPMails readMail = new ReadIMAPMails();

		// Calling processMail Function to read from IMAP Account
		readMail.processMail("imap.1und1.de", "thomas@freese-home.de", "...", "INBOX");
	}

	/**
	 * Erstellt ein neues {@link ReadIMAPMails} Object.
	 */
	public ReadIMAPMails()
	{
		super();
	}

	/**
	 * Return the primary text content of the message.
	 * 
	 * @param part {@link Part}
	 * @return String
	 * @throws MessagingException Falls was schief geht.
	 * @throws IOException Falls was schief geht.
	 */
	private String getText(final Part part) throws MessagingException, IOException
	{
		// String disposition = part.getDisposition();
		//
		// if ((disposition != null) &&
		// ((disposition.equals(Part.ATTACHMENT) ||
		// (disposition.equals(Part.INLINE))) {
		// saveFile(part.getFileName(), part.getInputStream());
		// }

		if (part.isMimeType("text/*"))
		{
			String s = (String) part.getContent();
			// textIsHtml = p.isMimeType("text/html");
			return s;
		}

		if (part.isMimeType("multipart/alternative"))
		{
			// prefer html text over plain text
			Multipart mp = (Multipart) part.getContent();
			String text = null;

			for (int i = 0; i < mp.getCount(); i++)
			{
				Part bp = mp.getBodyPart(i);

				if (bp.isMimeType("text/plain"))
				{
					if (text == null)
					{
						text = getText(bp);
					}

					continue;
				}
				else if (bp.isMimeType("text/html"))
				{
					String s = getText(bp);

					if (s != null)
					{
						return s;
					}
				}
				else
				{
					return getText(bp);
				}
			}

			return text;
		}
		else if (part.isMimeType("multipart/*"))
		{
			Multipart mp = (Multipart) part.getContent();

			for (int i = 0; i < mp.getCount(); i++)
			{
				String s = getText(mp.getBodyPart(i));

				if (s != null)
				{
					return s;
				}
			}
		}

		return null;
	}

	/**
	 * Liefert alle vorhandenen Text Parts einer {@link Message}.
	 * 
	 * @param part {@link Part}, @see {@link Message}
	 * @return {@link List}
	 * @throws IOException Falls was schief geht.
	 * @throws MessagingException Falls was schief geht.
	 */
	private List<AbstractTextPart> getTextParts(final Part part)
		throws MessagingException, IOException
	{
		List<AbstractTextPart> textParts = new ArrayList<>();

		if (part.isMimeType("text/*"))
		{
			String text = (String) part.getContent();

			if (part.isMimeType("text/plain"))
			{
				textParts.add(new PlainTextPart(text));
			}
			else if (part.isMimeType("text/html"))
			{
				textParts.add(new HTMLTextPart(text));
			}
		}
		else if (part.isMimeType("multipart/*"))
		{
			Multipart mp = (Multipart) part.getContent();

			for (int i = 0; i < mp.getCount(); i++)
			{
				Part bp = mp.getBodyPart(i);

				textParts.addAll(getTextParts(bp));
			}
		}

		return textParts;
	}

	/**
	 * Responsible for printing Data to Console
	 * 
	 * @param data String
	 */
	private void printData(final String data)
	{
		System.out.println(data);
	}

	/**
	 * @param host String
	 * @param username String
	 * @param password String
	 * @param folderName String
	 */
	@SuppressWarnings("boxing")
	public void processMail(final String host, final String username, final String password,
							final String folderName)
	{
		Session session = null;
		Store store = null;
		Folder folder = null;
		Message[] messages = null;
		// Object messagecontentObject = null;
		// Multipart multipart = null;
		// Part part = null;
		// ContentType contentType = null;

		try
		{
			printData("--------------processing mails started-----------------");
			Properties props = new Properties(System.getProperties());
			props.putAll(System.getProperties());

			// Mail-Server properties: Session verlangt die Informationen über Host, User, Passwd
			// etc.
			props.put("mail.imap.host", host);
			props.put("mail.imap.auth", "true");

			// Initialisierung der Auth-Klasse zur Mail-Account-Authentisierung; in Session benutzt
			Authenticator auth = new MailAuthenticator(username, password);
			// session = Session.getDefaultInstance(props, auth);
			session = Session.getInstance(props, auth);

			// Gibt in der Console Debug-Meldungen zum Verlauf aus
			session.setDebug(false);

			printData("getting the session for accessing email.");
			store = session.getStore("imap");

			store.connect();
			printData("Connection established with IMAP server.");

			// Get a handle on the default folder
			folder = store.getDefaultFolder();

			printData("Getting the " + folderName + " folder.");

			// Retrieve the "Inbox"
			folder = folder.getFolder(folderName);

			// Reading the Email Index in Read / Write Mode
			folder.open(Folder.READ_ONLY);

			// Retrieve the messages
			messages = folder.getMessages();

			// Loop over all of the messages
			for (Message message : messages)
			{
				printData("");

				// Enumeration<String> headers = message.getAllHeaders();
				String messageID = message.getHeader("Message-ID")[0];
				int messageNumber = message.getMessageNumber();
				Date receivedDate = message.getReceivedDate();
				String subject = message.getSubject();
				String from = ((InternetAddress) message.getFrom()[0]).getAddress();
				// String from = ((InternetAddress) message.getFrom()[0]).getAddress();
				printData(String.format("%02d \t %s \t %tc \t %s \t %s", messageNumber, messageID,
						receivedDate, subject, from));

				// String text = getText(message);
				// printData(text);
				List<AbstractTextPart> textParts = getTextParts(message);

				for (AbstractTextPart textPart : textParts)
				{
					printData(textPart.getText());
				}

				// // Retrieve the next message to be read
				// message = message2;
				//
				// // Retrieve the message content
				// messagecontentObject = message.getContent();
				//
				// // Determine email type
				// if (messagecontentObject instanceof Multipart)
				// {
				// printData("Found Email with Attachment");
				// sender = ((InternetAddress) message.getFrom()[0]).getPersonal();
				//
				// // If the "personal" information has no entry, check the address for the sender
				// // information
				// //
				// printData("If the personal information has no entry, check the address for the sender information.");
				//
				// if (sender == null)
				// {
				// sender = ((InternetAddress) message.getFrom()[0]).getAddress();
				// printData("sender in NULL. Printing Address:" + sender);
				// }
				// else
				// {
				// printData("Sender -." + sender);
				// }
				//
				// // Get the subject information
				// subject = message.getSubject();
				// printData("subject=" + subject);
				//
				// printData("date=" + message.getReceivedDate());
				//
				// // Retrieve the Multipart object from the message
				// multipart = (Multipart) messagecontentObject;
				//
				// printData("Retrieve the Multipart object from the message");
				//
				// // Loop over the parts of the email
				// for (int i = 0; i < multipart.getCount(); i++)
				// {
				// // Retrieve the next part
				// part = multipart.getBodyPart(i);
				//
				// // Get the content type
				// contentType = new ContentType(part.getContentType());
				//
				// // Display the content type
				// printData("ContentType: " + contentType);
				// // printData("ContentType: " + new ContentType(contentType));
				//
				// if (contentType.getBaseType().toLowerCase().startsWith("text/plain"))
				// {
				// printData("---------reading content type text/plain  mail -------------");
				//
				// // Object content = part.getContent();
				// // printData(content.toString());
				//
				// String charset = contentType.getParameter("charset");
				// BufferedReader reader = null;
				//
				// if (charset == null)
				// {
				// reader =
				// new BufferedReader(new StringReader(part.getContent()
				// .toString()));
				// }
				// else
				// {
				// reader =
				// new BufferedReader(new InputStreamReader(
				// part.getInputStream(), Charset.forName(charset)));
				// }
				//
				// for (String line; (line = reader.readLine()) != null;)
				// {
				// System.out.println(line);
				// }
				//
				// reader.close();
				// }
				// else
				// {
				// // Retrieve the file name
				// String fileName = part.getFileName();
				// printData("retrive the fileName=" + fileName);
				// }
				// }
				// }
				// else
				// {
				// printData("Found Mail Without Attachment");
				// sender = ((InternetAddress) message.getFrom()[0]).getPersonal();
				//
				// // If the "personal" information has no entry, check the address for the sender
				// // information
				// //
				// printData("If the personal information has no entry, check the address for the sender information.");
				//
				// if (sender == null)
				// {
				// sender = ((InternetAddress) message.getFrom()[0]).getAddress();
				// printData("sender in NULL. Printing Address:" + sender);
				// }
				//
				// // Get the subject information
				// subject = message.getSubject();
				// printData("subject=" + subject);
				//
				// // printData("Content: " + messagecontentObject.toString());
				// }
			}

			// Close the folder
			folder.close(false);

			// Close the message store
			store.close();
		}
		catch (AuthenticationFailedException e)
		{
			printData("Not able to process the mail reading.");
			e.printStackTrace();
		}
		catch (FolderClosedException e)
		{
			printData("Not able to process the mail reading.");
			e.printStackTrace();
		}
		catch (FolderNotFoundException e)
		{
			printData("Not able to process the mail reading.");
			e.printStackTrace();
		}
		catch (NoSuchProviderException e)
		{
			printData("Not able to process the mail reading.");
			e.printStackTrace();
		}
		catch (ReadOnlyFolderException e)
		{
			printData("Not able to process the mail reading.");
			e.printStackTrace();
		}
		catch (StoreClosedException e)
		{
			printData("Not able to process the mail reading.");
			e.printStackTrace();
		}
		catch (Exception e)
		{
			printData("Not able to process the mail reading.");
			e.printStackTrace();
		}
	}
}

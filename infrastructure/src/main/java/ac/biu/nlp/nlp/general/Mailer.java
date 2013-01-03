package ac.biu.nlp.nlp.general;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import ac.biu.nlp.nlp.general.configuration.ConfigurationFile;
import ac.biu.nlp.nlp.general.configuration.ConfigurationParams;
import ac.biu.nlp.nlp.general.file.ZipFile;

/**
 * The class basically sends mail using javax.mail
 * The init() gets the necessary parameters in a ConfigurationParams object
 * <p>
 * See main() example below 
 * 
 * @author nlp lab legacy code
 *
 */
public class Mailer
{	
	/**
	 * Ctor
	 * @param host 
	 * @param user 
	 * @param port 
	 * @param password 
	 * @param tempDir 
	 * @param tempZipFilename 
	 */
	public Mailer(String host, String user, int port, String password, File tempDir, String tempZipFilename)
	{
		m_host = host;//iParams.get("smtp-server");
		m_user = user; //iParams.get("smtp-server-user");
		m_password = password; //iParams.get("smtp-server-password");
		
		if(port > 0) //iParams.containsKey("smtp-server-port"))
			m_port = port; //iParams.getInt("smtp-server-port");
		else
			m_port = -1; // requesting the default port
		
		m_tempDir = tempDir; //iParams.getDirectory("tmp-dir");
		if(m_tempDir != null)
			m_tempDir.mkdirs();

		String tempFilename = tempZipFilename; //iParams.get("tmp-zip-file"); 
		m_tempFile = new File(tempFilename);
		if(m_tempFile == null && m_tempDir != null)
			m_tempFile = new File(m_tempDir, "Mailer.tmp_zip_file." + Long.toString(new Date().getTime()) + "." + this + ".zip");
	}
	
	/**
	 * Send a mail
	 * 
	 * @param iFromAddress
	 * @param iToAddresses
	 * @param iSubject
	 * @param iText
	 * @param iAttachment
	 * @return
	 * @throws IOException in case there's a problem with the attachment
	 * @throws MessagingException if there's any problem with the operation of havax.mail on the given parameters
	 */
	public void mail(Address iFromAddress, Address[] iToAddresses, String iSubject, String iText, File iAttachment) 
		throws MessagingException, IOException
	{
		// create and initialize a Session and Message
	    Session session = Session.getInstance(System.getProperties());
	    Message msg = new MimeMessage(session);
	    
	    msg.setHeader(HEADER_ARG, this.getClass().getName());
	    msg.setSentDate(new Date());
	    msg.setFrom(iFromAddress);
	    msg.setRecipients(RecipientType.TO, iToAddresses);
	    msg.setSubject(iSubject);
	    
	    // if there's an attachment, fill the mail with a MimeMultipart, otherwise use plain text 
	    if (iAttachment != null){
			MimeBodyPart mbpText = new MimeBodyPart();
			mbpText.setText(iText);
			MimeBodyPart mbpAttachemnt = new MimeBodyPart();
			mbpAttachemnt.attachFile(iAttachment);
			MimeMultipart mp = new MimeMultipart();
			mp.addBodyPart(mbpText);
			mp.addBodyPart(mbpAttachemnt);
			msg.setContent(mp);

		}
	 
	    // send mail
	    Transport transport = session.getTransport(TRANSPORT);
	    transport.connect(m_host, m_port, m_user, m_password);
	    transport.sendMessage(msg, iToAddresses);
	    transport.close();	  
	}
	
	/**
	 * Send a mail with the attachments zipped into the zip file specified
	 * 
	 * @param iFromAddress
	 * @param iToAddress
	 * @param iSubject
	 * @param iText
	 * @param iAttachments
	 * @param iTmpZipFile
	 * @throws IOException in case there's a problem with the attachment
	 * @throws MessagingException if there's any problem with the operation of havax.mail on the given parameters
	 */
	public void mail(Address iFromAddress, Address[] iToAddress, String iSubject, String iText, Collection<File> iAttachments, File iTmpZipFile) 
		throws IOException, MessagingException 		
	{
		ZipFile zf = new ZipFile(iTmpZipFile);		
		zf.compress(iAttachments);
		
		mail(iFromAddress, iToAddress, iSubject, iText, iTmpZipFile);
		
		iTmpZipFile.delete();		
	}
	
	/**
	 * Send a mail, no attachments
	 * 
	 * @param iFromAddress
	 * @param iToAddress
	 * @param iSubject
	 * @param iText
	 * @throws IOException in case there's a problem with the attachment
	 * @throws MessagingException if there's any problem with the operation of havax.mail on the given parameters
	 */
	public void mail(Address iFromAddress, Address[] iToAddress, String iSubject, String iText) throws MessagingException, IOException 
	{
		mail(iFromAddress, iToAddress, iSubject, iText, (File) null);
	}
	
	/**
	 * Send a mail with some attachments zipeed
	 * 
	 * @param iFromAddress
	 * @param iToAddress
	 * @param iSubject
	 * @param iText
	 * @param iAttachments
	 * @throws MessagingException 
	 * @throws IOException in case there's a problem with the attachment
	 * @throws MessagingException if there's any problem with the operation of havax.mail on the given parameters
	 */
	public void mail(Address iFromAddress, Address[] iToAddress, String iSubject, String iText, Collection<File> iAttachments) 
		throws IOException, MessagingException   
	{
		mail(iFromAddress, iToAddress, iSubject, iText, iAttachments, m_tempFile);
	}
		
	/**
	 * Send a mail with some attachments zipped in the given zip file name
	 * 
	 * @param iFromAddress
	 * @param iToAddress
	 * @param iSubject
	 * @param iText
	 * @param iAttachments
	 * @param iTmpZipFile
	 * @throws IOException in case there's a problem with the attachment
	 * @throws MessagingException if there's any problem with the operation of havax.mail on the given parameters
	 */
	public void mail(Address iFromAddress, Address[] iToAddress, String iSubject, String iText, Collection<File> iAttachments, String iTmpZipFile) 
		throws IOException, MessagingException 	
	{
		mail(iFromAddress, iToAddress, iSubject, iText, iAttachments, new File(m_tempDir, iTmpZipFile));
	}

	/**
	 * A main method for testing
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception 
	{				
		ConfigurationFile conf = new ConfigurationFile(args[0]);
		ConfigurationParams iParams = conf.getParams();

		Mailer mailer = new Mailer(
				iParams.get("smtp-server"),
				iParams.get("smtp-server-user"),
				iParams.getInt("smtp-server-port"),
				iParams.get("smtp-server-password"),
				iParams.getDirectory("tmp-dir"),
				iParams.get("tmp-zip-file")
				);
		
		if(args.length > 3)
		{
			List<File> attachments = new ArrayList<File>();	
			attachments.add(new File(args[3]));
			
			mailer.mail(new InternetAddress(args[1]),
					new InternetAddress[]{new InternetAddress(args[2])},
					"testing mailer", "this is a test", 
					attachments);
		}
		else{
			mailer.mail(new InternetAddress(args[1]),
					new InternetAddress[]{new InternetAddress(args[2])},
					"testing mailer", "this is a test");
		}
	}
	

	////////////////////////////////////////////////// private fields ///////////////////////////////////////////////////////////

	private static final String HEADER_ARG = "X-Mailer";
	private static final String TRANSPORT = "smtp";
	
	private String m_host;
	private int m_port;
	private String m_user;
	private String m_password;
	private File m_tempFile;
	private File m_tempDir;
}

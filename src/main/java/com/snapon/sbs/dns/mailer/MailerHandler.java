/*
 Copyright (c) 2013 Snap-on Business Solutions
 All rights reserved.

 COPYRIGHT
 Copyright subsists on this and all Snap-on Business Solutions products.
 Any unauthorised reproduction, distribution or use constitutes an
 infringement and any persons so doing are liable to prosecution.

 WARRANTY & LIABILITY
 No warranty regarding the accuracy of information or operation of
 this software is made by Snap-on Business Solutions or by
 representatives of Snap-on Business Solutions.

 Therefore no liability can be assumed by Snap-on Business Solutions
 for any damages or apparent damages resulting from the
 use of or intended use of this software.
*/

package com.snapon.sbs.dns.mailer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import com.alisonassociates.npd.framework.base.Environment;
import com.alisonassociates.npd.framework.sql.SqlTools;
import com.alisonassociates.npd.framework.util.LogUtil;
import com.alisonassociates.npd.framework.util.NxdLog;
import com.alisonassociates.npd.workflowmanager.Job;
import com.alisonassociates.npd.workflowmanager.JobHandler;
import com.alisonassociates.npd.workflowmanager.JobStatus;
import com.alisonassociates.npd.workflowmanager.ServiceException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.snapon.sbs.dns.email.EmailRequest;
import com.snapon.sbs.dns.emailsettings.EmailJob;
import com.snapon.sbs.dns.emailsettings.EmailLog;
import com.snapon.sbs.dns.emailsettings.EmailMessageType;
import com.snapon.sbs.dns.emailsettings.EmailProperty;
import com.snapon.sbs.dns.emailsettings.EmailRecipient;
import com.snapon.sbs.dns.emailsettings.EmailUser;
import com.sun.mail.smtp.SMTPMessage;

/**
 * Mailer handler that handles any email job that is processed
 * by the Mailer service class
 * @author Martin Whittington
 */
class MailerHandler extends JobHandler {

	private static NxdLog logger = LogUtil.make();
	private Session session;
	private Properties properties;
	private String mailerUrl;
	private String smtpServer;
	private MailerHandlerStatus currentStatus = MailerHandlerStatus.HANDLER_UNINITALIZED;
	private EmailJob currentJob = new EmailJob();
	private Set<String> allowedDomains = null;
	private static final int INTERVAL = 30000;
	
	
	@Override
	protected void initHandler()
		throws ServiceException {
		this.properties = System.getProperties();
		this.setAllowedDomains();
		this.smtpServer = Environment.getInstance().getProperty ("smtp.server");
		this.mailerUrl = Environment.getInstance().getProperty ("localURL");
//		this.mailerUrl = "localhost:8080/DNSMailer";
		if (smtpServer == null) {
			logger.error("initHandler(): NXC_PROPERTY smtp.server needs to be configured.");
		}
		if (this.mailerUrl == null) {
			logger.error("initHandler(): NXC_PROPERTY localURL needs to be configured.");
		}
		logger.debug("Using " + this.mailerUrl);
		this.properties.put("mail.smtp.host", smtpServer);
		this.session = Session.getInstance(this.properties, null);
		this.currentStatus = MailerHandlerStatus.HANDLER_READY;
	}
	
	/**
	 * Gets a comma seperated list of allowed domains from the nxc_properties table and adds
	 * them to a list. This is only used for DEV and TEST to prevent unsolicited emails from
	 * being sent.
	 */
	private void setAllowedDomains() {
		String domains = Environment.getInstance().getProperty("allowedDomains");
		String[] domainsArray = domains.split(",");
		this.allowedDomains = new HashSet<String>();
		if (domainsArray.length > 0) {
			for (String domain : domainsArray) {
				this.allowedDomains.add(domain);
			}
		}
		logger.debug("Allowed Domains: ", this.allowedDomains);
	}

	/**
	 * @param list set a list of allowed domains (USED FOR TEST)
	 */
	void setAllowedDomainList(Set<String> domainsList) {
		if (this.allowedDomains == null) {
			this.allowedDomains = domainsList;
		}
	}
	
	/**
	 * @return the url this handler is using
	 */
	public String getMailerUrl() {
		return this.mailerUrl;
	}
	
	/**
	 * @param status the current status to change
	 */
	void setMailerStatus(MailerHandlerStatus status) {
		this.currentStatus = status;
	}
	
	/**
	 * @return if the mailer has finished processing a job
	 */
	public boolean finished() {
		return (this.currentStatus == MailerHandlerStatus.HANDLER_FINISHED);
	}
	
	/**
	 * @return if the mailer is still busy processing a job
	 */
	public boolean busy() {
		return (this.currentStatus == MailerHandlerStatus.HANDLER_BUSY);
	}
	
	/**
	 * Processes a new job, by receiving the job from the service and assigning it locally
	 * @param currentJob The job to be processed
	 * @param mailServer The mail server to create a socket to
	 * @throws IOException if the socket cannt be closed properly
	 */
	final void processJob(EmailJob currentJob) throws IOException {
		this.currentStatus = MailerHandlerStatus.HANDLER_BUSY;
		logger.debug("processJob(): " + currentJob);
		this.currentJob = currentJob;
		Socket socket = null;
		try {
			// create a socket to the SMTP server
			socket = new Socket(this.smtpServer, 25);
			int tries = 0;
			int localInterval = INTERVAL;
			
			// if we cannot connect to the server after 10 tries we set the jobstatus to TIMEOUT
			while (!socket.isConnected() && socket.isClosed() && tries < 10) {
				logger.error("(Cannot connect to " + this.smtpServer + ") Connected: " + socket.isConnected() + " Closed: " + socket.isClosed());
				Thread.sleep(localInterval); // first sleep for 30 seconds, then 1 minute, then 2 etc
				tries++;
				localInterval *= 2;
			}
			
			if (tries == 10) {
				// we need to set the job to timed out
				this.currentJob.setJobState(JobStatus.TIMEOUT);
			}
		} catch (UnknownHostException e) {
			logger.error("Cannot connect to SMTPServer:", e);
		} catch (IOException e) {
			logger.error("Cannot connect to SMTPServer:", e);
		} catch (InterruptedException e) {
			logger.error("Sleeping thread was interrupted: ", e);
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		this.process();
	}

	@Override
	public void process() {
		logger.debug("process()");
		Gson gson = new Gson();
		Connection conn = SqlTools.getConnection();
		
		// if we timed out in processJob() update a log entry and reset the job status to NOT STARTED so we can try again
		if (JobStatus.TIMEOUT.equals(this.currentJob.getJobState())) {
			try {
				MailerDAO.insertLog(new EmailLog(this.currentJob.getJobId(), new Date(0), "This job timed out and could not connect to the SMTP server.",
						EmailMessageType.ERROR, this.currentJob.getSender()), conn);
				this.currentJob.setJobState(JobStatus.NOTSTARTED);
				this.updateJob(conn);
			} catch (ServiceException e) {
				logger.error("Error inserting log message for timed out job: " + this.currentJob.getJobId(), e);
			} finally {
				SqlTools.free(conn);		// free the connection here, end of one trip to the db
			}
			this.currentStatus = MailerHandlerStatus.HANDLER_FINISHED;
			return;
		}
		
		// Now we create an Email request instance from the json in the job
		try {
			EmailRequest request = gson.fromJson(this.currentJob.getJSONValue(), EmailRequest.class);
			// Get a list of blacklisted email addresses
			List<Map<String, String>> blackList = MailerDAO.getBlackList(this.currentJob.getSchema(), conn);
			try {
				this.processEmail(blackList, request, conn);
				this.updateJob(conn);
			} catch (ServiceException e) {
				logger.error("Error updating job: ", e);
			} finally {
				SqlTools.free(conn);		// free the connection here, end of one trip to the db
			}
			
			// if the currentjob finished ok then we can perform the rest of the db upkeep
			if (JobStatus.FINISHEDOK.equals(this.currentJob.getJobState())) {
				// before adding any entries into the recipients table, we need to add entries into the user table
				conn = SqlTools.getConnection();
				MailerDAO.insertEmailUser(request.getEmailUsers(), conn);
						
				// now we create a list of email recipients from the job
				List<EmailRecipient> recipientList = this.createRecipients(request, this.currentJob.getJobId());
				if (recipientList != null && recipientList.size() > 0) {
					MailerDAO.insertEmailRecipient(recipientList, conn);
				}
				
				// create an entry in the EMAIL_PROPERTY table
				EmailProperty currentProperties = this.createProperty(request.getEmailUsers());
				if (!currentProperties.getNameValuePair().isEmpty() && currentProperties.getJobId() != 0) {
					MailerDAO.insertJobProperties(currentProperties, conn);
				}
			}
		} catch (JsonSyntaxException e) {
			// if the syntax of the json coming in was bad....
			logger.error("Job ID ", this.currentJob.getJobId(), " contains Illegal JSON: ", this.currentJob.getJSONValue());
			// this job needs to be parked for this reason so it is not processed again
			this.currentJob.setJobState(JobStatus.FAILED);
			try {
				this.updateJob(conn, false);
			} catch (ServiceException e1) {
				logger.error("Error updating job after JsonSyntaxException: ", e1);
			}
		} finally {
			SqlTools.free(conn);
		}
		this.currentStatus = MailerHandlerStatus.HANDLER_FINISHED;
	}
	
	/**
	 * Sends an email linked to the current job. Also replaces the tags within the template
	 * that will be consistent throughout an email campaign e.g. subject line, bodytext etc
	 * @param blackList the list of email addresses that shouldnt receive emails
	 * @param request the current email request object
	 * @param conn the current connection
	 * @throws MessagingException if the email could not be sent
	 */
	final void processEmail(List<Map<String, String>> blackList, EmailRequest request, Connection conn){
		if (this.currentJob == null) {
			logger.error("could not send email, the job is null");
			return;
		}
		logger.debug("processEmail: ", request);
		// first get the template this request is using
		String currentTemplate = MailerDAO.getTemplateFile(request.getTemplateId(), request.getSchema(), conn);
		if (currentTemplate == null || currentTemplate.length() < 1) {
			// if the database returned a null then set the template to be the body text
			// as this should have been populated with valid tags ready for replacement
			currentTemplate = request.getBodyText();
			logger.debug("template was null, using: " + request.getBodyText());
			// if the current template is still null this means the bodytext was null also
			// as we need either one to not be null log it but the email can still be sent
			if (currentTemplate == null || currentTemplate.length() < 1) {
				logger.warn("Job ID: " + this.currentJob.getJobId() + " has no body text but will still be mailed out");
			}
		}
		
		try {
			int count = 0;
			for (EmailUser user : request.getEmailUsers()) {
				Map<String, Object> tags = new HashMap<String, Object>();
				if (!request.getEmailTags().isEmpty()) {
					tags = request.getEmailTags().get(count);
				}
				
				if (this.emailCanBeSent(user.getEmailAddress(), request.getEmailType(), blackList)) {
					this.sendEmail(user, currentTemplate, tags, request);
				}
				count++;
			}		
		} catch (MessagingException e) {
			logger.error("Email could not be sent: ", e);
			this.currentJob.setJobState(JobStatus.FAILED);
			return;
		}
		
		// the messages must be sent so lets update the email status
		this.currentJob.setJobState(JobStatus.FINISHEDOK);
	}
	
	/**
	 * Updates a job in the database with the jobs current status
	 * @param conn the current connection for a single transaction
	 */
	private void updateJob(Connection conn)
			throws ServiceException {
		this.updateJob(conn, true);
	}
	
	/**
	 * Updates a job in the database with the jobs current status
	 * @param conn the current connection for a single transaction
	 * @param reset should we reset the job status to retry the job?
	 */
	private void updateJob(Connection conn, boolean reset)
		throws ServiceException {
		if (this.currentJob == null) {
			logger.error("currentJob is null. Returning to main loop");
			return;
		}
		
		// if the state of the job is failure, set it back to not started so it is polled for again
		if (JobStatus.FAILED.equals(this.currentJob.getJobState())) {
			MailerDAO.insertLog(new EmailLog(this.currentJob.getJobId(), new Date(0), this.currentJob.getJobState().desc(),
					EmailMessageType.ERROR, this.currentJob.getSender()), conn);
			if (reset) {
				this.currentJob.setJobState(JobStatus.NOTSTARTED);
			}
		} else if (JobStatus.FINISHEDOK.equals(this.currentJob.getJobState())) {
			MailerDAO.insertLog(new EmailLog(this.currentJob.getJobId(), new Date(0), this.currentJob.getJobState().desc(), 
					EmailMessageType.INFORMATION, this.currentJob.getSender()), conn);
		}
		
		try {
			MailerDAO.updateJob(this.currentJob, conn);
		} catch (ServiceException e) {
			logger.error(this.currentJob.getJobId() + " could not be updated: ", e);
		}
	}

	/**
	 * This method creates a list of recipients from the current job id and original request that was made.
	 * This size of the list depends on the quantity of recipients so the entries in the EMAIL_RECIPIENT
	 * table will have one per recipient tied to the same job
	 * @param request the request containing the recipients
	 * @param jobId the job id the recipients received their emails with
	 * @return A list of recipients to go into the EMAIL_RECIPIENT table
	 */
	List<EmailRecipient> createRecipients(EmailRequest request, long jobId) {
		if (request == null) {
			logger.error("createRecipients() cannot create recipient list.");
			return null;
		}
		
		List<EmailRecipient> recipientList = new ArrayList<EmailRecipient>();
		// for every recipient that was submitted with the request
		if (request.getEmailUsers().size() > 0) {
			for (EmailUser user : request.getEmailUsers()) {
				EmailRecipient recipient = new EmailRecipient();
				recipient.setJobId(jobId);
				recipient.setEmailAddress(user.getEmailAddress());
				recipient.setUuid(user.getUuid());
				// we dont set the date yet not until we know if the user opened the mail, currently set to 0
				recipientList.add(recipient);
			}
		}
		return recipientList;
	}

	/**
	 * This method creates an email property object from the current job. The job holds the
	 * json string that we parse into a map here that we use for the name/value pairs.
	 * @param userList the list of users who's email addresses we need
	 * @return an email property ready to insert into the db
	 */
	EmailProperty createProperty(Set<EmailUser> userList) {
		EmailProperty prop = new EmailProperty();
		if (userList.size() > 0 && this.currentJob != null) {
			prop.setJobId(this.currentJob.getJobId());
			
			// Lets get the json string into a map we need
			Gson gson = new Gson();
			Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
			Map<String, Object> jsonMap = gson.fromJson(this.currentJob.getJSONValue(), mapType);
			StringBuilder recipientList = new StringBuilder();
			int i = 0;
			for (EmailUser user : userList) {
				i++;
				recipientList.append(user.getEmailAddress());
				if (i+1 < userList.size()) {
					recipientList.append(",");
				}
			}
			
			// we replace the recipients entry with a simple list of email addresses from the users
			jsonMap.put("recipients", recipientList.toString());
			prop.setNameValuePair(jsonMap);
		}
		return prop;
	}
	
	/**
	 * Instructs the mailer to stop after processing the current job
	 */
	final void stopMailer() {
		if (!(MailerHandlerStatus.HANDLER_KILLED.equals(this.currentStatus))) {
			this.currentStatus = MailerHandlerStatus.HANDLER_KILLED;
		}
		return;
	}
	
	/**
	 * Clears the handler making it ready to accept the next job
	 */
	@Override
	protected void cleanup() {
		logger.debug("Clearing the handler");
		if (MailerHandlerStatus.HANDLER_FINISHED.equals(this.currentStatus) ||
			MailerHandlerStatus.HANDLER_UNINITALIZED.equals(this.currentStatus)) {
			logger.debug("Reseting the handler back to ready.");
			this.currentJob = null;
			this.currentStatus = MailerHandlerStatus.HANDLER_READY;
		}
	}
	
	/**
	 * This method checks whether the email address and email type are in the blacklist
	 * @param emailAddress the email address to check
	 * @param emailType the email type to check
	 * @param blackList the list of blacklisted email addresses and types
	 * @return true if both match, false otherwise
	 */
	boolean emailCanBeSent(String emailAddress, String emailType, List<Map<String, String>> blackList) {
		// first check that the emailAddress is on the allowedDomainslist
		logger.debug("emailCanBeSent(): " + emailAddress + " " + emailType);
		if (this.isValidDomain(emailAddress)) {
			// if the user is not on the blacklist
			if (!blackList.isEmpty()) {
				for (Map<String, String> pair : blackList) {
					if (pair.get("email_address").equalsIgnoreCase(emailAddress)
							&& pair.get("email_type").equalsIgnoreCase(emailType)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * This method checks if the email address contains one of the allowed domains i.e. <code>@snapon.com</code>
	 * @param emailAddress the email address to check
	 * @return 
	 */
	boolean isValidDomain(String emailAddress) {
		logger.debug("isOnAllowedDomainList(): ", emailAddress);
		if (emailAddress != null) {
			String domainToCheck = null;
			for (int i = 0; i < emailAddress.length(); i++) {
				if (emailAddress.charAt(i) == '@') {
					domainToCheck = emailAddress.substring(i);
				}
			}
			if (this.allowedDomains != null && this.allowedDomains.size() > 0) {
				if (this.allowedDomains.contains(domainToCheck)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * This method replaces any tags that are within the html with the values retrieved
	 * from the database.
	 * @param text the text to parse
	 * @param tag the tags that need replacing
	 * @return the substituted string
	 */
	String substitute(String text, Map<String, Object> tag) {
		logger.debug("substitute() text: " + text + " with tags: " + tag);
		if (tag != null) {
			for (Map.Entry<String, Object> pair : tag.entrySet()) {
				if (text.contains("{" + pair.getKey() + "}") && pair.getKey().contains("TEXT:")) {
						// we are replacing text
						logger.debug("Replacing text: " + pair.getKey() + " with " + pair.getValue().toString());
						text = text.replaceAll("\\{" + pair.getKey() + "\\}", this.safeRegexReplacement(pair.getValue().toString()));
				} else if (text.contains("{" + pair.getKey() + "}") && pair.getKey().contains("IMAGE:")) {
					// we are replacing an image
					text = text.replaceAll("\\{" + pair.getKey() + "\\}", "<img src=\"" + this.mailerUrl + 
							"/emailImage?name=" + pair.getKey() + "\" alt=\"" + pair.getKey() + "\">");
				}
			}
		}
		return text;
	}

	/**
	 * Replace any \ or $ characters in the replacement string with an escaped \\
	 * or \$.
	 * 
	 * @param replacement the regular expression replacement text
	 * @return null if replacement is null or the result of escaping all \ and $ chars
	 */
	String safeRegexReplacement(String replacement) {
		if (replacement == null) {
			return replacement;
		}
		return replacement.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\$", "\\\\\\$");
	}
	
	/**
	 * Returns an SMTP message. Also replaces specific tags within the email that change with
	 * each email e.g. salutation, recipient name etc.
	 * @param user the current user holding the recipient details
	 * @param currentTemplate the template to send
	 * @param tags the tags that need replacing in the template
	 * @param request the current request
	 * @throws MessagingException if an error occurs with the messaging
	 */
	private void sendEmail(EmailUser user, String currentTemplate, Map<String, Object> tags, EmailRequest request)
			throws MessagingException {
		logger.debug("sendEmail");
		// build the message part
		SMTPMessage msg = new SMTPMessage(this.session);
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		Multipart multipart = new MimeMultipart();
		String trackingImage = this.createTrackingImageLink(user.getUuid());
		
		// parse the current template and replace the tags with the relevant information
		String templateClone = currentTemplate;
		templateClone = this.substitute(templateClone, tags);
		
		// build what the email should contain from the above strings and the email format
		String unSubscriptionLink = null;
		if (request.hasAddUnSubLink()) {
			unSubscriptionLink = this.createAllEmailFooter(user.getUuid(), request.getUnSubscriptionText());
		}
		
		String emailContents = templateClone + (request.hasAddUnSubLink() ? unSubscriptionLink + trackingImage : trackingImage);
		messageBodyPart.setContent(emailContents, "text/html;charset=\"utf-8\"");
		multipart.addBodyPart(messageBodyPart);
		
		// set the email details such as from, to, subject and content
		msg.setFrom(new InternetAddress(request.getEmailFrom()));
		msg.setRecipients(Message.RecipientType.TO, user.getEmailAddress());
		msg.setSubject(request.getSubjectText());
		msg.setContent(multipart);
		
		// Finally send the message out
		Transport.send(msg);
	}
	
	/**
	 * This method returns a long concatenated string that forms the link to unsubscribe
	 * at the bottom of the email.
	 * @param uuid the unique id of the user
	 * @param unSubText the text to display for the unsubscription link
	 * @return the string footer to append
	 */
	String createAllEmailFooter(long uuid, String unSubText) {
		return "<div style=\"text-align: center;color: rgb(192,192,192);font-family: Verdana; font-size: xx-small;\">"
				+ "<a href=\"" + this.mailerUrl + "/removeFromAll.html?uuid="	+ uuid + "\">" + unSubText + "</a>.</div>";	
	}

	/**
	 * This method builds the tracking image link related to the user who is receiving it
	 * @param uuid the unique id relating to the email recipient
	 * @return the url of the image to be appended to the email
	 */
	String createTrackingImageLink(long uuid) {
		return "<img src=\"" + this.mailerUrl + "/tracking?id=" + uuid + "\"/>";
	}
	
	@Override
	public boolean accept(Job potentialJob) {
		// currently based on the mailer handler status
		if (potentialJob != null) {
			if (this.currentStatus.equals(MailerHandlerStatus.HANDLER_READY)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return the currentStatus
	 */
	public MailerHandlerStatus getCurrentStatus() {
		return this.currentStatus;
	}
	
	/**
	 * @return the current job this handler is processing
	 */
	public EmailJob getCurrentJob() {
		return this.currentJob;
	}
}

package com.snapon.sbs.dns.emailsettings;



import java.util.Date;

import com.alisonassociates.npd.framework.util.DateUtils;
import com.alisonassociates.npd.framework.util.LogUtil;
import com.alisonassociates.npd.framework.util.NxdLog;

/**
 * This class represents an entry to be made into the EMAIL_LOG table.
 * The log will simply keep track of each job.
 * 
 * @author Martin Whittington
 */
public class EmailLog {
	private static NxdLog logger = LogUtil.make();
	private long jobId;
	private long dateStamp;
	private String message;
	private EmailMessageType messageType;
	private String userId;
	
	/**
	 * Default constructor.
	 */
	public EmailLog() {
		this(Long.valueOf(0).longValue(), new Date(System.currentTimeMillis()), null, EmailMessageType.INFORMATION, null);
		logger.trace("EmailLog() created.");
	}
	
	/**
	 * Overloaded constructor
	 * @param job_id the job id
	 * @param stamp the current date and time
	 * @param message the message
	 * @param type the message type (I, E or W)
	 * @param userId the user who created the job
	 */
	public EmailLog(long job_id, Date stamp, String message, EmailMessageType type, String userId) {
		this.setJobId(job_id);
		this.setDateStamp(stamp);
		this.setMessage(message);
		this.setMessageType(type);
		this.setUserId(userId);
	}
	
	/**
	 * Sets the job Id
	 * @param job_id the job id
	 */
	public void setJobId(long job_id) {
		this.jobId = job_id;
	}
	
	/**
	 * Gets the Job Id
	 * @return the job id
	 */
	public long getJobId() {
		return this.jobId;
	}
	
	/**
	 * Sets the date an entry was made into the log
	 * @param stamp the date
	 */
	public void setDateStamp(Date stamp) {
		this.dateStamp = DateUtils.fromDate(stamp);
	}
	
	/**
	 * Gets the date stamp
	 * @return the date stamp
	 */
	public Date getDateStamp() {
		return DateUtils.fromLong(this.dateStamp);
	}
	
	/**
	 * Sets the message the log will tally with the message type
	 * @param message the message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Gets the message the log holds
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * Sets the type of message this entry will be.
	 * @param type the type of message
	 */
	public void setMessageType(EmailMessageType type) {
		this.messageType = type;
	}
	
	/**
	 * Gets the type of message this entry was added with
	 * @return the type of message
	 */
	public EmailMessageType getMessageType() {
		return this.messageType;
	}
	
	/**
	 * Sets the user id the entry refers to
	 * @param id
	 */
	public void setUserId(String id) {
		this.userId = id;
	}
	
	/**
	 * Gets the user id
	 * @return the user id
	 */
	public String getUserId() {
		return this.userId;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("EmailLog: [" + this.jobId + "], [" + DateUtils.fromLong(this.dateStamp) + "]");
		ret.append(", [" + this.message + "], [" + this.messageType.getDesc() +"], [" + this.userId + "]");
		return ret.toString();
	}
}

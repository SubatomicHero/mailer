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

package com.snapon.sbs.dns.emailsettings;


import java.util.Date;

import com.alisonassociates.npd.framework.util.DateUtils;
import com.alisonassociates.npd.framework.util.LogUtil;
import com.alisonassociates.npd.framework.util.NxdLog;

/**
 * This class represents a single or list of email recipients.
 * 
 * @author Martin Whittington
 */
public class EmailRecipient {
	private static NxdLog logger = LogUtil.make();
	private long jobId;
	private String emailAddress;
	private long dateOpened;
	private long uuid;
	
	/**
	 * Default constructor for an email recipient.
	 */
	public EmailRecipient() {
		this(0, null, new Date(0), 0);
		logger.trace("EmailRecipient created.");
	}

	/**
	 * Overloaded constructor.
	 * @param job_id
	 * @param email_address
	 * @param opened
	 * @param uuid
	 */
	public EmailRecipient(long job_id, String email_address, Date opened, long uuid) {
		this.setJobId(job_id);
		this.setEmailAddress(email_address);
		this.setDateOpened(opened);
		this.setUuid(uuid);
	}
	
	/**
	 * Get the job id, if needed
	 * @return the job_id
	 */
	public long getJobId() {
		return jobId;
	}

	/**
	 * Set the job id
	 * @param job_id the job_id to set
	 */
	public void setJobId(long job_id) {
		this.jobId = job_id;
	}

	/**
	 * Get the email address, if needed
	 * @return the email_address
	 */
	public String getEmailAddress() {
		return this.emailAddress;
	}

	/**
	 * Set the email address
	 * @param email_address the email_address to set
	 */
	public void setEmailAddress(String email_address) {
		this.emailAddress = email_address;
	}

	/**
	 * Get the date an email was opened, if needed
	 * @return the date_opened
	 */
	public Date getDateOpened() {
		return DateUtils.fromLong(this.dateOpened);
	}

	/**
	 * Set the date an email was opened
	 * @param date the date to set
	 */
	public void setDateOpened(Date date) {
		this.dateOpened = DateUtils.fromDate(date);
	}

	/**
	 * Get the unique user id, if needed
	 * @return the uuid
	 */
	public long getUuid() {
		return this.uuid;
	}

	/**
	 * Set the unique user idea
	 * @param uuid the uuid to set
	 */
	public void setUuid(long uuid) {
		this.uuid = uuid;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("EmailRecipient: [" + this.jobId + "], [" + this.emailAddress + "]");
		ret.append(", [" + DateUtils.fromLong(this.dateOpened) + "], [" + this.uuid + "]");
		return ret.toString();
	}
}

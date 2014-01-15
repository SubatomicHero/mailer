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
import com.alisonassociates.npd.workflowmanager.Job;
import com.alisonassociates.npd.workflowmanager.JobStatus;

/**
 * This class represents an entry made into the EMAIL_JOB table.
 * There should be only one job per email request sent to the
 * mailer so it will be the mailers job to ensure a job entry
 * is correctly added.
 * 
 * @author Martin Whittington
 */
public class EmailJob implements Job {
	private long jobId;
	private long dateCreated;
	private String schema;
	private String createdBy;
	private String emailType;
	private int templateId;
	private JobStatus jobStatus;
	private EmailPriority jobPriority;
	private String sender;
	private String jsonData;
	private String systemName;
	
	/**
	 * Default constructor.
	 */
	public EmailJob() {
		this(0, new Date(0), null, null, null, 0, JobStatus.NOTSTARTED, EmailPriority.MEDIUM, null, null);
	}
	
	/**
	 * Overloaded constructor.
	 * @param job_id the job_id
	 * @param sent the date sent
	 * @param schema the schema
	 * @param created who created the entry
	 * @param type the type of email
	 * @param template_id the template id
	 * @param status the status of the email
	 * @param priority the urgency of the request
	 * @param sender who sent the email
	 */
	public EmailJob(long job_id, Date sent, String schema, String created, String type, int template_id, JobStatus status,
			 EmailPriority priority, String sender, String data) {
		this.jobId = job_id;
		this.setDateCreated(sent);
		this.schema = schema;
		this.createdBy = created;
		this.emailType = type;
		this.templateId = template_id;
		this.jobStatus = status;
		this.jobPriority = priority;
		this.sender = sender;
		this.jsonData = data;
	}
	
	/**
	 * Sets the job id
	 * @param id the id to set
	 */
	public void setJobId(long id) {
		this.jobId = id;
	}
	
	/**
	 * Gets the job id
	 * @return the job id
	 */
	public long getJobId() {
		return this.jobId;
	}
	
	/**
	 * Sets the date the email this job relates to
	 * was sent out.
	 * @param date the date sent
	 */
	public void setDateCreated(Date date) {
		this.dateCreated = DateUtils.fromDate(date);
	}
	
	/**
	 * Gets the date sent
	 * @return the date sent
	 */
	public Date getDateCreated() {
		return DateUtils.fromLong(this.dateCreated);
	}
	
	/**
	 * Sets the schema the email job was created by
	 * @param schema the schema
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	/**
	 * Gets the schema the email job was created by
	 * @return the schema
	 */
	public String getSchema() {
		return this.schema;
	}
	
	/**
	 * Sets the type of email that was sent.
	 * e.g. Marketing, reminder etc
	 * @param type the type of email
	 */
	public void setEmailType(String type) {
		this.emailType = type;
	}
	
	/**
	 * Gets the email type.
	 * @return the type of email
	 */
	public String getEmailType() {
		return this.emailType;
	}
	
	/**
	 * Sets the template id that this job used.
	 * @param id the template id
	 */
	public void setTemplateId(int id) {
		this.templateId = id;
	}
	
	/**
	 * Gets the template id
	 * @return the template id
	 */
	public int getTemplateId() {
		return this.templateId;
	}
	
	/**
	 * Sets the priority of the current job
	 * @param priority the priority to set
	 */
	public void setJobPriority(EmailPriority priority) {
		this.jobPriority = priority;
	}
	
	/**
	 * Gets the priority of the current job
	 * @return the priority
	 */
	public EmailPriority getJobPriority() {
		return this.jobPriority;
	}
	
	/**
	 * Sets the sender of the current email that
	 * this job relates to
	 * @param sender the name of the sender
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	/**
	 * Gets the sender of the current email that
	 * this job relates to
	 * @return the name of the sender
	 */
	public String getSender() {
		return this.sender;
	}
	
	/**
	 * Sets a the json data string
	 * @param data the data to set
	 */
	public void setJSONData(String data) {
		this.jsonData = data;
	}
	
	/**
	 * Gets the json data string 
	 */
	public String getJSONValue() {
		return this.jsonData;
	}
	
	/**
	 * @return the systemName
	 */
	public synchronized String getSystemName() {
		return systemName;
	}

	/**
	 * @param systemName the systemName to set
	 */
	public synchronized void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	@Override
	public int compareTo(Job that) {
		// Sort jobs based on their priority
		if (this.getPriority() == that.getPriority()) {
			return 0;
		} else if (this.getPriority() < that.getPriority()) {
			return -1;
		} else {
			return 1;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (jobId ^ (jobId >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EmailJob other = (EmailJob) obj;
		if (this.jobId != other.jobId) {
			return false;
		}
		return true;
	}

	@Override
	public String getJobType() {
		// NOT USED FOR THE MAILER YET
		return null;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setRequesterId(String id) {
		this.createdBy = id;
	}
	
	@Override
	public String getRequesterId() {
		return this.createdBy;
	}

	@Override
	public JobStatus getJobState() {
		return this.jobStatus;
	}

	@Override
	public String getCurrentTask() {
		// NOT USED FOR THE MAILER YET
		return null;
	}

	@Override
	public JobStatus getTaskState() {
		// NOT USED FOR THE MAILER YET
		return null;
	}

	@Override
	public String getParameters() {
		// NOT USED FOR THE MAILER YET
		return null;
	}

	@Override
	public String getJobDefinition() {
		// NOT USED FOR THE MAILER YET
		return null;
	}

	@Override
	public long getTimeout() {
		// NOT USED FOR THE MAILER YET
		return 0;
	}

	@Override
	public int getPriority() {
		return this.jobPriority.getOrder();
	}

	@Override
	public void setJobState(JobStatus newJobState) {
		this.jobStatus = newJobState;
	}

	@Override
	public void setCurrentTask(String newCurrentTask) {
		// NOT USED FOR THE MAILER YET
	}

	@Override
	public void setTaskState(JobStatus newTaskState) {
		// NOT USED FOR THE MAILER YET
	}

	@Override
	public void setTimeout(long newTimeout) {
		// NOT USED FOR THE MAILER YET
	}

	@Override
	public Long getPk() {
		// NOT USED FOR THE MAILER YET
		return null;
	}

	@Override
	public String getService() {
		// NOT USED FOR THE MAILER YET
		return null;
	}

	@Override
	public void setPriority(int priority) {
		this.jobPriority = EmailPriority.valueOf(String.valueOf(priority));
	}

	@Override
	public long getParentJobId() {
		// NOT USED FOR THE MAILER YET
		return 0;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("EmailJob: [" + this.jobId + "], [" + DateUtils.fromLong(this.dateCreated) + "], [" + this.schema + "]");
		ret.append(", [" + this.createdBy + "], [" + this.emailType + "], [" + this.templateId + "]");
		ret.append(", [" + this.jobStatus.desc() + "], [" + this.jobPriority.getDesc() + "]");
		ret.append(", [" + this.sender + "], [" + this.jsonData + "]");
		return ret.toString();
	}
}

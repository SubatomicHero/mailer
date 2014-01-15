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

import java.util.HashMap;
import java.util.Map;

import com.alisonassociates.npd.framework.util.LogUtil;
import com.alisonassociates.npd.framework.util.NxdLog;

/**
 * This class represents properties of email that can be
 * used by configuring name value pairs.
 * 
 * @author Martin Whittington
 */
public class EmailProperty {
	private static NxdLog logger = LogUtil.make();
	private long jobId;
	private Map<String ,Object> nameValuePair;
	
	/**
	 * Default constructor.
	 */
	public EmailProperty() {
		this(0);
		logger.trace("EmailProperty() created");
	}

	/**
	 * Overloaded constructor.
	 * @param job_id the job id
	 */
	public EmailProperty(long job_id) {
		this.setJobId(job_id);
		this.setNameValuePair(new HashMap<String, Object>());
	}
	/**
	 * Get the job id, if needed
	 * @return the job_id
	 */
	public long getJobId() {
		return this.jobId;
	}

	/**
	 * Set the job id
	 * @param job_id the job_id to set
	 */
	public void setJobId(long job_id) {
		this.jobId = job_id;
	}

	/**
	 * @return the nameValuePair
	 */
	public Map<String, Object> getNameValuePair() {
		return nameValuePair;
	}

	/**
	 * @param nameValuePair the nameValuePair to set
	 */
	public void setNameValuePair(Map<String, Object> nameValuePair) {
		this.nameValuePair = nameValuePair;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("job id: " + this.jobId + ", ");
		ret.append("[");
		int i = 0;
		for(Map.Entry<String, Object> pair : this.nameValuePair.entrySet()) {
			ret.append("{" + pair.getKey() + "=" + pair.getValue() + "}");
			if ((i + 1) < this.nameValuePair.size()) {
				ret.append(",");
			}
		}
		ret.append("]");
		return ret.toString();
	}
}

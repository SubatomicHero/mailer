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

package com.snapon.sbs.dns.webservice;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.alisonassociates.npd.framework.util.LogUtil;
import com.alisonassociates.npd.framework.util.NxdLog;
import com.alisonassociates.npd.workflowmanager.JobStatus;
import com.google.gson.Gson;
import com.snapon.sbs.dns.email.EmailRequest;
import com.snapon.sbs.dns.emailsettings.EmailJob;
import com.snapon.sbs.dns.emailsettings.EmailPriority;

/**
 * This class holds the methods that the web service will need to use that
 * are not web service methods. (Parsing JSON, creating jobs etc).
 * @author Martin Whittington
 */
public class MailerWebServiceHelper {
	private static NxdLog logger = LogUtil.make();
	
	/**
	 * This method creates a new email job and returns it
	 * @param request the email request holding that will be serialized
	 * @return a new email job ready for adding to the db
	 */
	public static EmailJob createNewJob(EmailRequest request) {
		if (request == null) {
			logger.error("Email Request is empty or null. Cannot create the job");
			return null;
		}
		logger.debug("createNewJob()");
		// the job id is sequentially created and set during the job creation
		Gson json = new Gson();
		EmailJob job = new EmailJob();
		job.setSchema(request.getSchema());									// This needs to come from the request
		job.setRequesterId(request.getCreatedBy());							// need to get the username of the sender, either email form or not
		job.setEmailType(request.getEmailType());								// This comes from the application, dependant on the action
		job.setTemplateId(request.getTemplateId());							// This comes from the users choice in the specific application
		job.setJobState(JobStatus.NOTSTARTED);								// This email has not been sent yet
		job.setJobPriority(EmailPriority.valueOf(request.getPriority()));	// This is taken from the request
		job.setSender(request.getEmailFrom());
		job.setJSONData(json.toJson(request));
		return job;
	}
	
	/**
	 * This method will take a map of string object pairing and return
	 * a string built up as valid JSON
	 * @param json the map containing the pairs
	 * @return a single string representation of the map
	 */
	public static String parseJSON(Map<String, Object> json) {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		int i = json.size();
		for (Map.Entry<String, Object> pair : json.entrySet()) {
			--i;
			builder.append("\"" + pair.getKey() + "\":");
			builder.append((isCollectionType(pair.getValue()) ? pair.getValue() : "\"" + pair.getValue() + "\""));
			if (i > 0) {
				builder.append(",");
			}
		}
		builder.append("}");
		return builder.toString();
	}
	
	/**
	 * This method checks whether the JSON being parse is a container class. This
	 * prevents malforming the returning JSON string.
	 * @param obj the object to check what type it is
	 * @return true if it is a container class, false otherwise
	 */
	static boolean isCollectionType(Object obj) {
		if (obj instanceof List<?>) {
			return true;
		} else if (obj instanceof Collection<?>) {
			return true;
		} // add other statements here
		return false;
	}
}

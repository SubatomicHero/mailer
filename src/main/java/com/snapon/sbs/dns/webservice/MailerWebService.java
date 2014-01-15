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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.alisonassociates.npd.framework.i18n.TextFormatter;
import com.alisonassociates.npd.framework.i18n.TextLocale;
import com.alisonassociates.npd.framework.i18n.TextManager;
import com.alisonassociates.npd.framework.util.LogUtil;
import com.alisonassociates.npd.framework.util.NxdLog;
import com.alisonassociates.npd.workflowmanager.ServiceException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.snapon.sbs.dns.email.EmailRequest;
import com.snapon.sbs.dns.mailer.MailerDAO;

/**
 * This class represents the Web Service that potentially any
 * application can interface with and send email.
 * 
 * @author Martin Whittington
 */
@Path("/mailer")
public class MailerWebService {
	private static NxdLog logger = LogUtil.make();
	private Gson localJson = new Gson();
	private static Map<String, Object> test = new HashMap<String, Object>();
	/**
	 * A method that is called from the calling application to check this mailer is ready for jobs
	 * @param json the json the application will send
	 * @return "OK" if alive
	 */
	@POST
	@Path("/testconnection")
	@Consumes(MediaType.APPLICATION_JSON)
	public Map<String, Object> testConnection(String json) {
		// change this logging to debug to see what the mailer is receiving
		logger.debug("testConnection() received: " + json);
		test.clear();
		test.put("result", "OK");
		return test;
	}
	
	/**
	 * This method adds a job into the EMAIL_JOB table ready for polling.
	 * @param json the json string that we parse into an email request object.
	 * @return the output message the we can use for job status feedback.
	 * @throws ServiceException if an error occurs with the database.
	 * @throws IOException if an error occurs with the clob creation.
	 */
	@POST
	@Path("/addjob")
	@Consumes(MediaType.APPLICATION_JSON)
	public Map<String, Object> addJob(String json)
			throws ServiceException, IOException {
		logger.trace("addJob(): ", json);
		Map <String, Object> response = new HashMap<String, Object>();
		try {
			EmailRequest request = this.localJson.fromJson(json, EmailRequest.class);
			logger.debug("addJob(): ", request.toString());
			long status = MailerDAO.insertJob(MailerWebServiceHelper.createNewJob(request));
			if (status != MailerDAO.ENTRY_FAILED) {
				// if the job entry did not fail, we need to tell the tag table what tags this job is using
				// status should at this point hold a reference to the job id
				if (!request.getEmailTags().isEmpty()) {
					MailerDAO.insertEmailTags(status, request.getEmailTags());
				}
			}

			response.put("jobStatus", status);
			response.put("jobMessage", (status == MailerDAO.ENTRY_FAILED ? "Job failed." : "Job submitted successfully."));
		} catch (JsonSyntaxException e) {
			// if the JSON was malformed...
			logger.error("addJob(): malformed JSON: ", e);
			// it was never inserted so return a message
			response.put("jobStatus", MailerDAO.ENTRY_FAILED);
			response.put("jobMessage", "Job failed. Malformed JSON");
		}
		return response;
	}
	
	/**
	 * This method will remove all entries from the blacklist table relating to
	 * the given email address in the json
	 * @param json the json containing the email address
	 * @return output the message reporting the job status
	 */
	@POST
	@Path("/subscribe")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String subscribe(String json) {
		/* 
		 * the json string needs to have four properties as a minimum
		 * emailAddress, emailType, schema and language. The language is set from the browser locale.
		 */
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		Map<String, Object> whiteList = this.localJson.fromJson(json, mapType);
		logger.debug("subscribing: ", json);
		TextLocale tl = TextLocale.getTextLocale(whiteList.get("language").toString(), null, "*", "*");
		TextFormatter tf = TextManager.getInstance().getTextFormatter(tl);
		int status = MailerDAO.removeFromBlackList(whiteList);
		StringBuilder output = new StringBuilder();
		output.append("{\"Message\":\"");
		
		switch (status) {
		case MailerDAO.ENTRY_FAILED:
			// error message
			output.append(tf.format("CANT_REMOVE_ADDRESS") + "\"}");
			break;
		case 0:
			// no rows were updates
			output.append(tf.format("ALREADY_BEEN_REMOVED") + "\"}");
			break;
		default:
			// we fall here as the status could return a varying amount of records updated/deleted
			output.append(status + tf.format("RECORDS_REMOVED") + "\"}");
			break;
		}
		return output.toString();
	}

	/**
	 * This method will unsubscribe an email address from a type of email or any email
	 * This will use the language passed from setMessage, which loads once the user has loaded the page.
	 * @param json the json containing the emails
	 * @return output the message reporting the success of the job
	 */
	@POST
	@Path("/unsubscribe")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String unsubscribe(String json) {
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		Map<String, Object> blackList = this.localJson.fromJson(json, mapType);
		logger.debug("unsubscribe(): ", blackList);
		
		TextLocale tl = TextLocale.getTextLocale(blackList.get("language").toString(), null, "*", "*");
		TextFormatter tf = TextManager.getInstance().getTextFormatter(tl);
		long uuid = Long.parseLong(blackList.get("uuid").toString());
		int status = MailerDAO.insertBlackList(uuid);
		StringBuilder output = new StringBuilder();
		output.append("{\"Message\":\"");
		
		switch (status) {
		case MailerDAO.ENTRY_PRESENT:
			// record has already been removed
			output.append(tf.format("ALREADY_BEEN_REMOVED"));
			break;
		case MailerDAO.ENTRY_FAILED:
			// error removing address
			output.append(tf.format("CANT_REMOVE_ADDRESS"));
			break;
		default:
			// n number of rows were added
			output.append(tf.format("HAS_BEEN_REMOVED"));
			break;
		}
		output.append("\"}");
		return output.toString();
	}
	
	/**
	 * This method returns a list of templates the current user is allowed to see
	 * based on their profile.
	 * @param json the json string containing the security classes
	 * @return a json map of allowed templates
	 */
	@POST
	@Path("/gettemplates")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, Object>> getTemplates(String json) {
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		Map<String, Object> securityClasses = this.localJson.fromJson(json, mapType);
		logger.debug("getTemplates(): ", securityClasses);
		return MailerDAO.getTemplates(securityClasses);
	}
	
	/**
	 * This method returns a single template file if the configuration of the workflow has jumped
	 * the user to the last stage of the mailer panel. The template is retrieved based on the template ID
	 * in the mail list provider and the user's schema
	 * @param json the json containing the template information
	 * @return either a template file in html format or an empty string
	 */
	@POST
	@Path("/gettemplatebyid")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getTemplateById(String json) {
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		Map<String, Object> jsonMap = this.localJson.fromJson(json, mapType);
		logger.debug("getTemplateById(): ", jsonMap);
		if (jsonMap.isEmpty()) {
			return "";
		}
		
		return MailerDAO.getTemplateFile(Integer.parseInt(jsonMap.get("templateId").toString()),
				jsonMap.get("schema").toString());
	}
	
	/**
	 * This method will allow a json string containing new template information to be
	 * added to the database.
	 * @param json the json string containing the template information
	 * @return Message + -999 if the insert failed, or the number of rows added
	 */
	@POST
	@Path("/addtemplate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> addTemplate(String json) {
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		Map<String, Object> templateDetails = this.localJson.fromJson(json, mapType);
		logger.debug("addTemplate(): ", templateDetails);
		long status = MailerDAO.insertTemplate(templateDetails);
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("message", status);
		return message;
	}
}

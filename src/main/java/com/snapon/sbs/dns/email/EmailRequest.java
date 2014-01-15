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

package com.snapon.sbs.dns.email;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alisonassociates.npd.framework.base.Environment;
import com.google.gson.annotations.SerializedName;
import com.snapon.sbs.dns.emailsettings.EmailUser;

/**
 * This class is the representation of an email request made by any application. This class
 * is then serialized by GSON and passed around where needed. If more class members are needed
 * please remember to add the SerializedName annotation.
 * @author Martin Whittington
 */
public class EmailRequest {
	
	@SerializedName("emailFrom")
	private String emailFrom;
	
	@SerializedName("text:subjectText")
	private String subjectText;
	
	@SerializedName("text:bodyText")
	private String bodyText;
	
	@SerializedName("systemName")
	private String schema;
	
	@SerializedName("emailType")
	private String emailType;
	
	@SerializedName("createdBy")
	private String createdBy;

	@SerializedName("recipients")
	private Set<EmailUser> emailUsers;	// a set is used to ensure no duplicates emails are used
	
	@SerializedName("templateId")
	private int templateId;
	
	@SerializedName("priority")
	private String priority;
	
	@SerializedName("emailTags")
	private List<Map<String, Object>> emailTags;
	
	@SerializedName("addUnSubLink")
	private boolean addUnSubLink;
	
	@SerializedName("unSubscriptionText")
	private String unSubscriptionText;
	
	/**
	 * Default constructor
	 */
	public EmailRequest() {
		this(null, null, null, null, null, null, 0, null, true, null);
	}
	
	/**
	 * Overridden constructor
	 * @param emailFrom who is sending the email
	 * @param subjectText what is the subject line of the email
	 * @param bodyText what is in the body of the email
	 * @param systemName the system the request came from
	 * @param emailType what type of email is being sent
	 * @param createdBy who created the email (username/email)
	 * @param templateId the template ID the request is using
	 * @param priority how urgent is the request
	 * @param addUnSubLink if this request needs to add on the unsub link
	 * @param unSubText the text to display the unsubscription details
	 */
	public EmailRequest(String emailFrom, String subjectText, String bodyText, String systemName, String emailType,
			String createdBy, int templateId, String priority, boolean addUnSubLink, String unSubText) {
		this.emailFrom = emailFrom;
		this.subjectText = subjectText;
		this.bodyText = bodyText;
		this.schema = systemName;
		this.emailType = emailType;
		this.createdBy = createdBy;
		this.templateId = templateId;
		this.priority = priority;
		this.addUnSubLink = addUnSubLink;
		this.unSubscriptionText = unSubText;
		this.emailUsers = new HashSet<EmailUser>();
		this.emailTags = new ArrayList<Map<String, Object>>();
		this.emailUsers.clear();
		this.emailTags.clear();
	}
	
	/**
	 * @return the emailFrom
	 */
	public String getEmailFrom() {
		// this wasnt set by the sender, we use a default
		if (this.emailFrom == null || this.emailFrom.length() < 1) {
			this.emailFrom = Environment.getInstance().getProperty("alert.smtp.sender");
		}
		return this.emailFrom;
	}

	/**
	 * @param emailFrom the emailFrom to set
	 */
	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	/**
	 * @return the subjectText
	 */
	public String getSubjectText() {
		// one parse to make sure that the subject text does not contain line breaks or the email will fail
		return this.subjectText.replaceAll("\n", ". ");
	}

	/**
	 * @param subjectText the subjectText to set
	 */
	public void setSubjectText(String subjectText) {
		this.subjectText = subjectText;
	}

	/**
	 * @return the bodyText
	 */
	public String getBodyText() {
		return bodyText;
	}

	/**
	 * @param bodyText the bodyText to set
	 */
	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
	}

	/**
	 * @return the systemName
	 */
	public String getSchema() {
		return this.schema;
	}

	/**
	 * @param systemName the systemName to set
	 */
	public void setSchema(String systemName) {
		this.schema = systemName;
	}

	/**
	 * @return the emailType
	 */
	public String getEmailType() {
		return emailType;
	}

	/**
	 * @param emailType the emailType to set
	 */
	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}

	/**
	 * @return the emailUsers
	 */
	public Set<EmailUser> getEmailUsers() {
		return emailUsers;
	}

	/**
	 * @param emailUsers the emailUsers to set
	 */
	public void setEmailUsers(Set<EmailUser> emailUsers) {
		this.emailUsers = emailUsers;
	}
	
	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the templateId
	 */
	public int getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}

	/**
	 * @return the emailTags
	 */
	public List<Map<String, Object>> getEmailTags() {
		return emailTags;
	}

	/**
	 * @param emailTags the emailTags to set
	 */
	public void setEmailTags(List<Map<String, Object>> emailTags) {
		this.emailTags = emailTags;
	}

	/**
	 * @return the addUnSubLink
	 */
	public boolean hasAddUnSubLink() {
		return addUnSubLink;
	}

	/**
	 * @param addUnSubLink the addUnSubLink to set
	 */
	public void setAddUnSubLink(boolean addUnSubLink) {
		this.addUnSubLink = addUnSubLink;
	}

	/**
	 * @return the unSubscriptionText
	 */
	public String getUnSubscriptionText() {
		if (this.unSubscriptionText == null) {
			this.unSubscriptionText = "Click here to unsubscribe";
		}
		return this.unSubscriptionText;
	}

	/**
	 * @param unSubscriptionText the unSubscriptionText to set
	 */
	public void setUnSubscriptionText(String unSubscriptionText) {
		this.unSubscriptionText = unSubscriptionText;
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("emailFrom: " + this.emailFrom + ", subjectText: " + this.subjectText + ", bodyText: " + this.bodyText);
		ret.append(", emailType: "+ this.emailType + ", emailUser: " + this.emailUsers + ", schema: " + this.schema);
		ret.append(", createdBy: " + this.createdBy + ", templateId: " + this.templateId + ", priority: " + this.priority);
		ret.append(", emailTags: " + this.emailTags + ", addUnSubLink: " + this.addUnSubLink + ", UnSubText: " + this.unSubscriptionText);
		return ret.toString();
	}
}

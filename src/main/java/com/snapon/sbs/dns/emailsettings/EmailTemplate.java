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

import com.alisonassociates.npd.framework.util.LogUtil;
import com.alisonassociates.npd.framework.util.NxdLog;

/**
 * This class represents en email template. Each template can only
 * be specifically used depending on the security class of the user.
 * @author Martin Whittington
 */
public class EmailTemplate {
	private static NxdLog logger = LogUtil.make();
	private int template_id;
	private String schema;
	private String clob;
	private String description;
	private EmailTemplateFormat format;
	private String template_type;
	private String security_class;
	private String country;
	private String language;
	private String subject;
	
	/**
	 * Default constructor.
	 */
	public EmailTemplate() {
		this(0, null, null, null, EmailTemplateFormat.PLAIN, null, null, null, null, null);
		logger.trace("EmailTemplate() created.");
	}
	
	/**
	 * Overloaded constructor
	 * @param id the template id
	 * @param schema the schema that can see the template
	 * @param clob the information
	 * @param description the description of the template
	 * @param format the format it is written in
	 * @param type the type of template it is
	 * @param security_class who can see this template
	 * @param country the country used for locale
	 * @param language the language used for locale
	 * @param subject the subject of the email this template used
	 */
	public EmailTemplate(int id, String schema, String clob, String description, EmailTemplateFormat format, String type,
			String security_class, String country, String language, String subject) {
		this.setTemplateId(id);
		this.setSchema(schema);
		this.setClob(clob);
		this.setDescription(description);
		this.setFormat(format);
		this.setTemplateType(type);
		this.setSecurityClass(security_class);
		this.setCountry(country);
		this.setLanguage(language);
		this.setSubject(subject);
	}
	
	/**
	 * Sets the current template id
	 * @param id the template id to set
	 */
	public void setTemplateId(int id) {
		this.template_id = id;
	}
	
	/**
	 * Gets the current template id
	 * @return the template id
	 */
	public int getTemplateId() {
		return this.template_id;
	}
	
	/**
	 * Sets the current schema
	 * @param schema the schema to set
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	/**
	 * Gets the current schema
	 * @return the current schema
	 */
	public String getSchema() {
		return this.schema;
	}

	/**
	 * @return the clob
	 */
	public String getClob() {
		return clob;
	}

	/**
	 * @param clob the clob to set
	 */
	public void setClob(String clob) {
		this.clob = clob;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the format
	 */
	public EmailTemplateFormat getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(EmailTemplateFormat format) {
		this.format = format;
	}

	/**
	 * @return the template_type
	 */
	public String getTemplateType() {
		return template_type;
	}

	/**
	 * @param template_type the template_type to set
	 */
	public void setTemplateType(String template_type) {
		this.template_type = template_type;
	}

	/**
	 * @return the security_class
	 */
	public String getSecurityClass() {
		return security_class;
	}

	/**
	 * @param security_class the security_class to set
	 */
	public void setSecurityClass(String security_class) {
		this.security_class = security_class;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	@Override
	public String toString() {
		// The clob member was not included in this string intentionally
		// it potentially could be quite large
		StringBuilder ret = new StringBuilder();
		ret.append("EmailTemplate: [" + this.template_id + "], [" + this.schema + "], [" + this.description + "], [" + this.format.getDesc() + "]");
		ret.append(", [" + this.template_type + "], [" + this.security_class + "], [" + this.country + "], [" + this.language + "]");
		return ret.toString();
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
}

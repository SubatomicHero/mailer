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
 * This class represents the email blacklist table. The table
 * will contain the email addresses that are not to be
 * emailed anymore, the type of email (marketing, reminders etc),
 * the date the email address was added to the table, the date the
 * email address was removed from the table and the schema
 * the request came from.
 * 
 * @author Martin Whittington
 *
 */
public class EmailBlacklist {
	private static NxdLog logger = LogUtil.make();
	private String emailAddress;
	private String emailType;
	private long dateAdded;
	private String schema;
	
	/**
	 * An entry that is to be added into
	 * the email_blacklist table. A default set of
	 * variables.
	 */
	public EmailBlacklist() {
		this(null, null, new Date(System.currentTimeMillis()), null);
		logger.trace("EmailBlacklist() created");
	}
	
	/**
	 * An entry that is to be added into 
	 * the email_blacklist table.
	 * @param address the email address
	 * @param type the email type
	 * @param added the date the entry is added
	 * @param schema the schema the request came from
	 */
	public EmailBlacklist(String address, String type, Date added, String schema) {
		this.setEmailAddress(address);
		this.setEmailType(type);
		this.setDateAdded(added);
		this.setSchema(schema);
	}
	
	/**
	 * Sets the email address.
	 * @param address the address to set
	 */
	public void setEmailAddress(String address) {
		this.emailAddress = address;
	}
	
	/**
	 * Gets the email address, if needed.
	 * @return the email address
	 */
	public String getEmailAddress() {
		return this.emailAddress;
	}
	
	/**
	 * Sets the email type.
	 * @param eType the type to set
	 */
	public void setEmailType(String eType) {
		this.emailType = eType;
	}
	
	/**
	 * Gets the email type, if needed.
	 * @return the email type
	 */
	public String getEmailType() {
		return this.emailType;
	}
	
	/**
	 * Sets the date added.
	 * @param date the date in long format (Epoch) to add
	 */
	public void setDateAdded(Date date) {
		this.dateAdded = DateUtils.fromDate(date);
	}
	
	/**
	 * Gets the date added, if needed
	 * @return the date added
	 */
	public Date getDateAdded() {
		return DateUtils.fromLong(this.dateAdded);
	}
	
	/**
	 * Sets the schema the entry in the table refers to
	 * @param schema
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	/**
	 * Gets the schema, if needed
	 * @return the current schema
	 */
	public String getSchema() {
		return this.schema;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("EmailBlackList: [" + this.emailAddress + "], [" + this.emailType + "]");
		ret.append(", [" + DateUtils.fromLong(this.dateAdded) + "], " + ", [" + this.schema + "]");
		return ret.toString();
	}
}

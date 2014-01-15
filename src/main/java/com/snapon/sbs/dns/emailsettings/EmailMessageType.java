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

/**
 * This enum represents the message type that the EMAIL_LOG
 * table will hold for information purposes.
 * The types can be INFORMATION, WARNING or ERROR.
 * 
 * @author Martin Whittington
 */
public enum EmailMessageType {
	/** An information log entry */
	INFORMATION("I", "Information"),
	
	/** A warning log entry */
	WARNING("W", "Warning"),
	
	/** An error log entry */
	ERROR("E", "Error");
	
	private final String type;
	private final String desc;
	
	/**
	 * Private constructor
	 * @param type the type of log message
	 * @param desc the description of the message type
	 */
	private EmailMessageType(String type, String desc) {
		this.type = type;
		this.desc = desc;
	}
	
	/**
	 * Gets the message type
	 * @return the message type
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * Gets the message type description
	 * @return the type description
	 */
	public String getDesc() {
		return this.desc;
	}
}

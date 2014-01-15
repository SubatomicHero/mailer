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

package com.snapon.sbs.dns.mailer;

/**
 * Defines the various handler statuses
 * @author Martin Whittington
 */
public enum MailerHandlerStatus {

	HANDLER_UNINITALIZED (0, "Uninitialised"),
	
	HANDLER_READY (1, "Ready"),
	
	HANDLER_BUSY (2, "Busy"),
	
	HANDLER_FINISHED (3, "Finished"),
	
	HANDLER_KILLED (4, "Killed");
	
	private final int value;
	private final String desc;
	
	/**
	 * Creates an instance
	 * @param value The value that should be in the DB
	 * @param desc The description of the status
	 */
	private MailerHandlerStatus (int value, String desc) {
		this.value = value;
		this.desc = desc;
	}
	
	/**
	 * @return the int value of the status
	 */
	public int getValue() {
		return this.value;
	}
	
	/**
	 * @return the description of the status
	 */
	public String getDesc() {
		return this.desc;
	}
}

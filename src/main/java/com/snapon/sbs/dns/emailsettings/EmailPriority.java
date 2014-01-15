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
 * This enum represents the priority an email job will have.
 * Depending on certain email types some jobs will be marked as
 * having a higher priority than others and will be moved in
 * the queue to ensure they will be sent first.
 * @author Martin Whittington
 */
public enum EmailPriority {
	/** High priority */
	HIGH('H', "High", 0),
	
	/** Medium priority */
	MEDIUM('M', "Medium", 1),
	
	/** Low priority */
	LOW('L', "Low", 2);
	
	private final char priority;
	private final String priorityDesc;
	private final int priorityOrder;
	/**
	 * A private constructor.
	 * @param priority the priority as created
	 * @param desc the description of the priority
	 */
	private EmailPriority(char priority, String desc, int order) {
		this.priority = priority;
		this.priorityDesc = desc;
		this.priorityOrder = order;
	}
	
	/**
	 * Gets the char representation of the priority.
	 * @return the priority as a char
	 */
	public char getPriority() {
		return this.priority;
	}
	
	/**
	 * Gets the description of the current priority
	 * @return the description
	 */
	public String getDesc() {
		return this.priorityDesc;
	}
	
	/**
	 * Gets the order of importance as a number
	 * @return the priority as a number
	 */
	public int getOrder() {
		return this.priorityOrder;
	}
}

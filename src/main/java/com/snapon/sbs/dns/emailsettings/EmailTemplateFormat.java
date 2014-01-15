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
 * This enum represents the format a template could be saved in.
 * This could XML, RTF, plain text etc. This enum will grow when
 * more formats are realised.
 * 
 * @author Martin Whittington
 *
 */
public enum EmailTemplateFormat {
	HTML("HTML"),
	XML("XML"),
	RTF("RTF"),
	PLAIN("PLAIN_TEXT");
	
	private final String format;
	
	/**
	 * Private constructor.
	 * @param format
	 */
	private EmailTemplateFormat(String format) {
		this.format = format;
	}
	
	/**
	 * Gets the current template format as a string
	 * @return the format
	 */
	public String getDesc() {
		return this.format;
	}
}

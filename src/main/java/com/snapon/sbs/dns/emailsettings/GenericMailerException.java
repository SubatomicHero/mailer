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

import com.alisonassociates.npd.framework.exception.DNSException;

public class GenericMailerException extends DNSException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new instance with the given error message.
	 * @param msg The error message.
	 */
	public GenericMailerException(String msg) {
		super(msg);
	}
	
	/**
	 * Creates a new instance with the given error message and cause of exception
	 * @param msg The error message
	 * @param cause The cause of this exception
	 */
	public GenericMailerException(String msg, Throwable cause) {
		super(msg, cause);
	}
}

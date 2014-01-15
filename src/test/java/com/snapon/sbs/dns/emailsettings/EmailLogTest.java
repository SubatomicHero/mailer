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

import static org.junit.Assert.*;

import java.sql.Date;

import org.junit.Before;
import org.junit.Test;

public class EmailLogTest {

	private EmailLog emaillog;
	
	@Before
	public void setUp() throws Exception {
		emaillog = new EmailLog(100, new Date(1386840240), "The log has been created", EmailMessageType.INFORMATION, "username");
	}
		
	@Test
	public final void testEmailLog() {
		assertEquals (100, emaillog.getJobId());
		assertEquals (new Date(1386840240), emaillog.getDateStamp());
		assertEquals ("The log has been created", emaillog.getMessage());
		assertEquals (EmailMessageType.INFORMATION, emaillog.getMessageType());
		assertEquals ("username", emaillog.getUserId());
	}
	
	@Test
	public final void testEmailLogToString() {
		assertEquals("EmailLog: [100], [Sat Jan 17 02:14:00 GMT 1970], [The log has been created], [Information], [username]", this.emaillog.toString());
	}
}

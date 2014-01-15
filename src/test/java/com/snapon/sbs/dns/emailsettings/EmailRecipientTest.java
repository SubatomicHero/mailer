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

public class EmailRecipientTest {
	
	private EmailRecipient recipient;
	
	@Before
	public void setUp() throws Exception {
		recipient = new EmailRecipient(100, "bill.gates@microsoft.com", new Date(1386840240), Long.valueOf(6654654));
	}
	
	@Test
	public final void testEmailRecipient() {
		assertEquals (100, recipient.getJobId());
		assertEquals ("bill.gates@microsoft.com", recipient.getEmailAddress());
		assertEquals (new Date(1386840240), recipient.getDateOpened());
		assertEquals (6654654, recipient.getUuid());
	}
	
	@Test
	public final void testEmailRecipientToString() {
		assertEquals("EmailRecipient: [100], [bill.gates@microsoft.com], [Sat Jan 17 02:14:00 GMT 1970], [6654654]", this.recipient.toString());
	}
}

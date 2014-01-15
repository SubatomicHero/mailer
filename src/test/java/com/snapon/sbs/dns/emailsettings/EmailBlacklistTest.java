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

public class EmailBlacklistTest {

	private EmailBlacklist blacklist;
	
	@Before
	public void setUp() throws Exception {
		blacklist = new EmailBlacklist("bill.gates@snapon.com", "marketing", new Date(1386840240), "Nissan");
	}
	
	@Test
	public final void testEmailBlackList() {
		assertEquals ("bill.gates@snapon.com", blacklist.getEmailAddress());
		assertEquals (new Date(1386840240), blacklist.getDateAdded());
		assertEquals ("Nissan", blacklist.getSchema());
	}
	
	@Test
	public final void testEmailBlackListToString() {
		assertEquals ("EmailBlackList: [bill.gates@snapon.com], [marketing], [Sat Jan 17 02:14:00 GMT 1970], , [Nissan]", this.blacklist.toString());
	}
}

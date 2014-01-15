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

import org.junit.Before;
import org.junit.Test;

public class EmailMessageTypeTest {
	private EmailMessageType messageType;
	
	@Before
	public void setUp() {
		this.messageType = EmailMessageType.ERROR;
	}
	
	@Test
	public final void testEmailMessageTypeGetDescription() {
		assertEquals ("E", this.messageType.getType());
	}
	
	@Test
	public final void testEmailMessageTypeGetDesc() {
		assertEquals ("Error", this.messageType.getDesc());
	}
}

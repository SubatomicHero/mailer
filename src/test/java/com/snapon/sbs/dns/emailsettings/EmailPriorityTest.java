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

public class EmailPriorityTest {
	private EmailPriority priority;
	
	@Before
	public void setUp () {
		this.priority = EmailPriority.LOW;
	}
	
	@Test
	public final void testEmailPriorityGetPriority() {
		assertEquals ('L', this.priority.getPriority());
	}
	
	@Test
	public final void testEmailPriorityGetDesc() {
		assertEquals("Low", this.priority.getDesc());
	}
	
	@Test
	public final void testEmailPriorityGetOrder() {
		assertEquals(2, this.priority.getOrder());
	}
}

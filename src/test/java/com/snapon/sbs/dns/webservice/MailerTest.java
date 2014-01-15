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

package com.snapon.sbs.dns.webservice;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

import org.junit.Test;

public class MailerTest {
	
	@Test
	public final void testParseJSON() {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("emailAddress", "martin.whittington@snapon.com");
		assertEquals ("{\"emailAddress\":\"martin.whittington@snapon.com\"}", MailerWebServiceHelper.parseJSON(json));
		json.clear();
		
		json.put("amIFat", true);
		assertEquals ("{\"amIFat\":\"true\"}", MailerWebServiceHelper.parseJSON(json));
		json.clear();
		
		json.put("age", 34);
		assertEquals ("{\"age\":\"34\"}", MailerWebServiceHelper.parseJSON(json));
		json.clear();
	}
	
	@Test
	public final void testIsCollectionType() {
		assertTrue (MailerWebServiceHelper.isCollectionType(new ArrayList<>()));
		assertFalse (MailerWebServiceHelper.isCollectionType(new HashMap<>()));
		assertFalse (MailerWebServiceHelper.isCollectionType(""));
		assertFalse (MailerWebServiceHelper.isCollectionType(3));
		assertTrue (MailerWebServiceHelper.isCollectionType(new HashSet<>()));
		assertTrue (MailerWebServiceHelper.isCollectionType(new Stack<>()));
		assertTrue (MailerWebServiceHelper.isCollectionType(new LinkedList<>()));
	}
	
	@Test
	public final void testWebServiceTestConnection() {
		MailerWebService service = new MailerWebService();
		Map<String, Object> test = service.testConnection("{\"name\":\"martin\",\"age\":34}");
		assertEquals (test.get("result"), "OK");		
	}
}

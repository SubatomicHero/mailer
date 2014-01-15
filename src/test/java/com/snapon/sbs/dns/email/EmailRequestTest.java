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

package com.snapon.sbs.dns.email;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.snapon.sbs.dns.emailsettings.EmailUser;
import com.snapon.sbs.dns.emailsettings.InvalidEmailAddressException;

public class EmailRequestTest {
	private EmailRequest request;
	
	@Before
	public void setUp() {
		this.request = new EmailRequest();
	}
	
	// test the setters/getters
	@Test
	public final void testEmailRequestSetFrom() {
		this.request.setEmailFrom("martin.whittington@snapon.com");
		assertEquals ("martin.whittington@snapon.com", this.request.getEmailFrom());
	}
	
	@Test
	public final void testEmailRequestSetSubject() {
		this.request.setSubjectText("TEST");
		assertEquals ("TEST", this.request.getSubjectText());
	}
	
	@Test
	public final void testEmailRequestSetBodyText() {
		assertEquals (null, this.request.getBodyText());
		this.request.setBodyText("Welcome");
		assertEquals ("Welcome", this.request.getBodyText());
	}
	
	@Test
	public final void testEmailRequestSetSchema() {
		assertEquals (null, this.request.getSchema());
		this.request.setSchema("FIATEU");
		assertEquals ("FIATEU", this.request.getSchema());
	}
	
	@Test
	public final void testEmailRequestSetType() {
		assertEquals (null, this.request.getEmailType());
		this.request.setEmailType("marketing");
		assertEquals ("marketing", this.request.getEmailType());
	}
	
	@Test
	public final void testEmailRequestSetCreatedBy() {
		assertEquals (null, this.request.getCreatedBy());
		this.request.setCreatedBy("NSCFR");
		assertEquals ("NSCFR", this.request.getCreatedBy());		
	}
	
	@Test
	public final void testEmailRequestSetTemplateId() {
		assertEquals (0, this.request.getTemplateId());
		this.request.setTemplateId(44);
		assertEquals (44, this.request.getTemplateId());
	}
	
	@Test
	public final void testEmailRequestSetPriority() {
		assertEquals (null, this.request.getPriority());
		this.request.setPriority("LOW");
		assertEquals ("LOW", this.request.getPriority());
	}
	
	@Test
	public final void testEmailRequestSetUnsubLink() {
		assertTrue(this.request.hasAddUnSubLink());
		this.request.setAddUnSubLink(false);
		assertFalse(this.request.hasAddUnSubLink());
	}
	
	@Test
	public final void testEmailRequestEmailUsers() throws InvalidEmailAddressException {
		assertTrue(this.request.getEmailUsers().isEmpty());
		Set<EmailUser> users = new HashSet<EmailUser>();
		EmailUser user = new EmailUser();
		users.add(user);
		this.request.setEmailUsers(users);
		assertFalse(this.request.getEmailUsers().isEmpty());
	}
	
	@Test
	public final void testEmailRequestEmailTags() {
		assertTrue(this.request.getEmailTags().isEmpty());
		List<Map<String, Object>> tags = new ArrayList<Map<String, Object>>();
		Map<String, Object> tag = new HashMap<String, Object>();
		tag.put("TEXT:SALUTATION", "Mr");
		tag.put("TEXT:COUNTRY_ID", 150);
		tags.add(tag);
		this.request.setEmailTags(tags);
		assertFalse(this.request.getEmailTags().isEmpty());
	}
	
	@Test
	public final void testEmailRequestToString() {
		assertEquals ("emailFrom: null, subjectText: null, bodyText: null, emailType: null, emailUser: [], schema: null, createdBy: null, templateId: 0, priority: null,"
				+ " emailTags: [], addUnSubLink: true, UnSubText: null",	this.request.toString());
	}
}

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

package com.snapon.sbs.dns.mailer;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.alisonassociates.npd.framework.schedule.TaskException;
import com.google.gson.Gson;
import com.snapon.sbs.dns.email.EmailRequest;
import com.snapon.sbs.dns.emailsettings.EmailUser;
import com.snapon.sbs.dns.emailsettings.InvalidEmailAddressException;

public class MailerServiceTest {

	private String json;
	private EmailUser dummyUser;
	private EmailRequest dummyRequest;
	private MailerService service;
	
	@Before
	public void setUp() throws Exception {
		Gson gson = new Gson();
		this.dummyUser = this.createDummyUser();
		this.json = "{recipients:" + dummyUser + ", \"emailType\":\"standard\", \"systemName\":\"NISSAN\", \"text:bodyText\":\"This is another test\"" +
					  ",\"text:subjectText\":\"This is a test\"}";
		this.dummyRequest = gson.fromJson(this.json, EmailRequest.class);
		this.dummyRequest.setEmailFrom("do-not-reply@snapon,com");
		this.dummyRequest.getEmailUsers().clear();
		this.dummyRequest.getEmailUsers().add(this.dummyUser);
		this.service = new MailerService();
	}
	
	@Test
	public final void testMailerServiceCreateEmailRequest() {
		this.dummyRequest.getEmailUsers().clear();
		this.dummyRequest.getEmailUsers().add(this.dummyUser);		
		assertEquals ("This is a test", this.dummyRequest.getSubjectText());
		assertEquals ("This is another test", this.dummyRequest.getBodyText());
		assertEquals ("standard", this.dummyRequest.getEmailType());
		assertEquals ("NISSAN", this.dummyRequest.getSchema());
	}
	
	@Test
	public final void testMailerServiceGetName() {
		assertEquals ("MailerService", this.service.getName());
	}
	
	@Test
	public final void testMailerServiceGetStatus() {
		assertEquals (1, this.service.getStatus());
	}
	
	@Test
	public final void testMailerServiceGetSummary() {
		assertEquals ("MailerService is in state 1", this.service.getSummary());
	}
	
	@Test
	public final void testMailerServiceGetProperties() {
		assertEquals (null, this.service.getProperties()); // only intialised once the service starts
	}
	
	@Test
	public final void testMailerServiceLoopIsRunning() {
		assertTrue(this.service.getLoopIsRunning());
	}
	
	@Test
	public final void testMailerServiceStop()
			throws TaskException {
		this.service.stop();
		assertFalse(this.service.getLoopIsRunning());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public final void testMailerServiceGetCurrentJobs() {
		assertEquals (null, this.service.getCurrentJobs());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public final void testMailerServiceGetCurrentMailers() {
		assertEquals (null, this.service.getCurrentMailers());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public final void testMailerServiceGetCurrentThreads() {
		assertEquals (null, this.service.getCurrentThreads());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public final void testMailerServiceCleanUp() {
		this.service.cleanUp();
		assertEquals (null, this.service.getCurrentJobs());
		assertEquals (null, this.service.getCurrentMailers());
		assertEquals (null, this.service.getCurrentThreads());
	}
	
	
	
	/**
	 * A quick method for creating a user
	 * @return a new email user
	 * @throws InvalidEmailAddressException if the email address is invalid
	 */
	private EmailUser createDummyUser()
			throws InvalidEmailAddressException {
		EmailUser user = new EmailUser();
		user.setEmailAddress("martin.whittington@snapon.com");
		user.setSalutation("Mr");
		user.setFirstName("Martin");
		user.setSurName("Whittington");
		user.setAddress1("10 Downing street");
		user.setAddress2(null);
		user.setAddress3("London");
		user.setCountry(74);
		user.setLanguageId(25);
		user.setPostCode("W1 1PP");
		user.setTelephoneNumber("01818118181");
		return user;
	}
}

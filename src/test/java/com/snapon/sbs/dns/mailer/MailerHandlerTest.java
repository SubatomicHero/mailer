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

import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.alisonassociates.npd.workflowmanager.JobStatus;
import com.google.gson.Gson;
import com.snapon.sbs.dns.email.EmailRequest;
import com.snapon.sbs.dns.emailsettings.EmailJob;
import com.snapon.sbs.dns.emailsettings.EmailPriority;
import com.snapon.sbs.dns.emailsettings.EmailRecipient;
import com.snapon.sbs.dns.emailsettings.EmailUser;
import com.snapon.sbs.dns.emailsettings.InvalidEmailAddressException;

public class MailerHandlerTest {

	private MailerHandler mailer;
	private String json;
	private EmailUser dummyUser;
	private EmailRequest dummyRequest;
	private EmailJob dummyJob;
	
	@Before
	public void setUp() throws Exception {
		Gson gson = new Gson();
		this.dummyUser = this.createDummyUser();
		this.mailer = new MailerHandler();
		this.json = "{recipients:" + dummyUser + ", \"emailType\":\"standard\", \"systemName\":\"NISSAN\", \"bodyText\":\"This is another test\"" +
					  ",\"subjectText\":\"This is a test\"}";
		this.dummyJob =  new EmailJob(100, new Date(System.currentTimeMillis()), "NISSAN", "MartinW",
						"Reminder", 1, JobStatus.FINISHEDOK, EmailPriority.HIGH, "Phil", this.json);
		this.dummyRequest = gson.fromJson(this.dummyJob.getJSONValue(), EmailRequest.class);
		this.dummyRequest.setEmailFrom("do-not-reply@snapon,com");
		this.dummyRequest.getEmailUsers().clear();
		this.dummyRequest.getEmailUsers().add(this.dummyUser);
	}

	@Test
	public final void testMailerServiceCreateRecipient() {
		List<EmailRecipient> testList = this.mailer.createRecipients(this.dummyRequest, this.dummyJob.getJobId());
		assertEquals (this.dummyJob.getJobId(), testList.get(0).getJobId());
		assertEquals (this.dummyUser.getEmailAddress(), testList.get(0).getEmailAddress());
		assertEquals (this.dummyUser.getUuid(), testList.get(0).getUuid());
		assertEquals (null, testList.get(0).getDateOpened());	// as when we create a recipient we havent opened the email yet
	}
	
	@Test
	public final void testMailerAccept() {
		assertEquals (MailerHandlerStatus.HANDLER_UNINITALIZED, this.mailer.getCurrentStatus());
		boolean test = this.mailer.accept(this.dummyJob);
		assertFalse (test);
		assertFalse (this.mailer.accept(null));
	}
	
	@Test
	public final void testMailerSafeRegexReplacement() {
		String test = null;
		assertEquals (null, this.mailer.safeRegexReplacement(test));
		test = "TEST";
		assertEquals ("TEST", this.mailer.safeRegexReplacement(test));
	}
	
	@Test
	public final void testMailerSubstitute() {
		String text = null;
		Map<String, Object> tags = new HashMap<String, Object>();
		assertEquals (null, this.mailer.substitute(text, tags));
		
		text = "gdfgdf";
		assertEquals (text, this.mailer.substitute(text, tags));
		
		text = null;
		tags = new HashMap<String, Object>();
		assertEquals (null, this.mailer.substitute(text, tags));
		
		text = "{IMAGE:HEADER}{TEXT:BODY}{TEXT:SUBJECT}{TEXT:SIGNATURE}{TEXT:FOOTER}";
		Map<String, Object> test = new HashMap<String, Object>();
		test.put("TEXT:BODY", "YOLO!");
		test.put("TEXT:SUBJECT", "This is a unit test");
		assertEquals ("{IMAGE:HEADER}YOLO!This is a unit test{TEXT:SIGNATURE}{TEXT:FOOTER}", this.mailer.substitute(text, test));
	}
	
	@Test
	public final void testMailerCleanUp() {
		this.mailer.cleanup();
		assertEquals (MailerHandlerStatus.HANDLER_READY, this.mailer.getCurrentStatus());
	}
	
	@Test
	public final void testMailerStopMailer() {
		this.mailer.stopMailer();
		assertEquals (MailerHandlerStatus.HANDLER_KILLED, this.mailer.getCurrentStatus());
	}
	
	@Test
	public final void testMailerFinished() {
		this.mailer.setMailerStatus(MailerHandlerStatus.HANDLER_FINISHED);
		assertTrue (this.mailer.finished());
		this.mailer.setMailerStatus(MailerHandlerStatus.HANDLER_READY);
		assertFalse (this.mailer.finished());
	}
	
	@Test
	public final void testMailerBusy() {
		this.mailer.setMailerStatus(MailerHandlerStatus.HANDLER_BUSY);
		assertTrue (this.mailer.busy());
		this.mailer.setMailerStatus(MailerHandlerStatus.HANDLER_FINISHED);
		assertFalse (this.mailer.busy());
	}
	
	@Test
	public final void testMailerisOnAllowedDomainList() {
		assertFalse (this.mailer.isValidDomain("martin.whittington@snapon.com"));
		// false because the allowed domains are retrieved from the environment properties
		Set<String> testSet = new HashSet<String>();
		String[] test = "@snapon.com,@gmail.com".split(",");
		if (test.length > 0) {
			for (String domain : test) {
				testSet.add(domain);
			}
		}
		this.mailer.setAllowedDomainList(testSet);
		assertTrue (this.mailer.isValidDomain("martin.whittington@snapon.com"));
		assertFalse (this.mailer.isValidDomain("martin.whittington@microsoft.com"));
	}
	
	@Test
	public final void testMailerSetMailerStatus() {
		this.mailer.setMailerStatus(MailerHandlerStatus.HANDLER_BUSY);
		assertTrue(this.mailer.getCurrentStatus() == MailerHandlerStatus.HANDLER_BUSY);
		
		this.mailer.setMailerStatus(MailerHandlerStatus.HANDLER_FINISHED);
		assertTrue(this.mailer.getCurrentStatus() == MailerHandlerStatus.HANDLER_FINISHED);
		
		this.mailer.setMailerStatus(MailerHandlerStatus.HANDLER_KILLED);
		assertTrue(this.mailer.getCurrentStatus() == MailerHandlerStatus.HANDLER_KILLED);
		
		this.mailer.setMailerStatus(MailerHandlerStatus.HANDLER_READY);
		assertTrue(this.mailer.getCurrentStatus() == MailerHandlerStatus.HANDLER_READY);
		
		this.mailer.setMailerStatus(MailerHandlerStatus.HANDLER_UNINITALIZED);
		assertTrue(this.mailer.getCurrentStatus() == MailerHandlerStatus.HANDLER_UNINITALIZED);
	}
	
	@Test
	public final void testMailerHasUnSubLink() {
		assertTrue(this.dummyRequest.hasAddUnSubLink()); // set as default when a request is created
		
		this.dummyRequest.setAddUnSubLink(false);
		assertFalse(this.dummyRequest.hasAddUnSubLink());
	}
	
	@Test
	public final void testMailerHandlerStatusGetValue() {
		this.mailer.setMailerStatus(MailerHandlerStatus.HANDLER_READY);
		assertEquals(1, MailerHandlerStatus.HANDLER_READY.getValue());
	}
	
	@Test
	public final void testMailerHandlerStatusGetDesc() {
		this.mailer.setMailerStatus(MailerHandlerStatus.HANDLER_FINISHED);
		assertEquals("Finished", MailerHandlerStatus.HANDLER_FINISHED.getDesc());
	}
	
	@Test
	public final void testMailerHandlerCreateTrackingImageLink() {
		assertEquals("<img src=\"null/tracking?id=7777\"/>", this.mailer.createTrackingImageLink(7777));
	}
	
	@Test
	public final void testMailerHandlerGetMailerUrl() {
		assertEquals(null, this.mailer.getMailerUrl());
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

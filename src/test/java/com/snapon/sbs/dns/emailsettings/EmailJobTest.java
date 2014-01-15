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

import com.alisonassociates.npd.workflowmanager.JobStatus;

public class EmailJobTest {

	private EmailJob emailjob1;
	private EmailJob emailJob2;
	
	@Before
	public void setUp() throws Exception {
		emailjob1 = new EmailJob(200, new Date(1386840240), "Nissan", "Phil", "Standard", 1,
				JobStatus.NOTSTARTED, EmailPriority.MEDIUM, "username", "{\"test\":\"this is a test\"}");
		
		emailJob2 = new EmailJob(201, new Date(1386840240), "Nissan", "Phil", "Standard", 1,
				JobStatus.NOTSTARTED, EmailPriority.MEDIUM, "username", "{\"test\":\"this is a test\"}");
	}

	@Test
	public final void testEmailJob() {
		assertEquals (200, emailjob1.getJobId());
		assertEquals (new Date(1386840240), emailjob1.getDateCreated());
		assertEquals ("Nissan", emailjob1.getSchema());
		assertEquals ("Phil", emailjob1.getRequesterId());
		assertEquals ("Standard", emailjob1.getEmailType());
		assertEquals (1, emailjob1.getTemplateId());
		assertEquals (JobStatus.NOTSTARTED, emailjob1.getJobState());
		assertEquals (EmailPriority.MEDIUM, emailjob1.getJobPriority());
		assertEquals ("username", emailjob1.getSender());
		assertEquals ("{\"test\":\"this is a test\"}", emailjob1.getJSONValue());
	}
	
	@Test
	public final void testEmailJobInheritance() {
		assertEquals (0, this.emailjob1.getParentJobId());
		assertEquals (null, this.emailjob1.getService());
		assertEquals (null, this.emailjob1.getPk());
		assertEquals (0, this.emailjob1.getTimeout());
		assertEquals (null, this.emailjob1.getJobDefinition());
		assertEquals (null, this.emailjob1.getParameters());
		assertEquals (null, this.emailjob1.getTaskState());
		assertEquals (null, this.emailjob1.getCurrentTask());
		assertEquals (null, this.emailjob1.getJobType());
	}
	
	@Test
	public final void testEmailJobToString() {
		assertEquals("EmailJob: [200], [Sat Jan 17 02:14:00 GMT 1970], [Nissan], [Phil], [Standard],"
				+ " [1], [Not yet started], [Medium], [username], [{\"test\":\"this is a test\"}]", this.emailjob1.toString());
	}
	
	@Test
	public final void testEmailJobEquals() {
		assertFalse (this.emailjob1.equals(this.emailJob2));
		this.emailjob1.setJobId(this.emailJob2.getJobId());
		assertTrue (this.emailjob1.equals(this.emailJob2));
	}
}

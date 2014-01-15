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

public class EmailTemplateTest {
	
	private EmailTemplate template;
	
	@Before
	public void setUp() throws Exception {
		this.template = new EmailTemplate();
	}
	
	@Test
	public final void testEmailTemplateSetId() {
		assertEquals(0, this.template.getTemplateId());
		this.template.setTemplateId(1);
		assertEquals(1, this.template.getTemplateId());
	}
	
	@Test
	public final void testEmailTemplateSetSchema() {
		assertEquals(null, this.template.getSchema());
		this.template.setSchema("Nissan");
		assertEquals("Nissan", this.template.getSchema());
	}
	
	@Test
	public final void testEmailTemplateSetClob() {
		assertEquals(null, this.template.getClob());
		this.template.setClob("yada yada");
		assertEquals("yada yada", this.template.getClob());
	}
	
	@Test
	public final void testEmailTemplateSetDescription() {
		assertEquals(null, this.template.getDescription());
		this.template.setDescription("used for marketing");
		assertEquals("used for marketing", this.template.getDescription());
	}
	
	@Test
	public final void testEmailTemplateSetFormat() {
		assertEquals(EmailTemplateFormat.PLAIN, this.template.getFormat());
		this.template.setFormat(EmailTemplateFormat.HTML);
		assertEquals(EmailTemplateFormat.HTML, this.template.getFormat());
	}
	
	@Test
	public final void testEmailTemplateSetSecurityClass() {
		assertEquals(null, this.template.getSecurityClass());
		this.template.setSecurityClass("ADMIN");
		assertEquals("ADMIN", this.template.getSecurityClass());
	}
	
	@Test
	public final void testEmailTemplateSetType() {
		assertEquals(null, this.template.getTemplateType());
		this.template.setTemplateType("text");
		assertEquals("text", this.template.getTemplateType());
	}

	@Test
	public final void testEmailTemplateSetCountry() {
		assertEquals(null, this.template.getCountry());
		this.template.setCountry("FRANCE");
		assertEquals("FRANCE", this.template.getCountry());
	}
	
	@Test
	public final void testEmailTemplateSetLanguage() {
		assertEquals(null, this.template.getLanguage());
		this.template.setLanguage("French");
		assertEquals("French", this.template.getLanguage());
	}
	
	@Test
	public final void testEmailTemplateSetSubject() {
		assertEquals(null, this.template.getSubject());
		this.template.setSubject("This is a test");
		assertEquals("This is a test", this.template.getSubject());
	}
	
	@Test
	public final void testEmailTemplateToString() {
		assertEquals ("EmailTemplate: [0], [null], [null], [PLAIN_TEXT], [null], [null], [null], [null]", this.template.toString());
	}
}

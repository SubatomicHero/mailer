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

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class EmailTagTest {
	
	private EmailTag tag;
	private File file;
	
	@Before
	public void setUp() throws Exception {
		this.file = new File("C://test.txt");
		this.tag = new EmailTag();
	}
	
	@Test
	public final void testEmailTagSetJobID() {
		assertEquals(0, this.tag.getJobId());
		this.tag.setJobId(100);
		assertEquals(100, this.tag.getJobId());
	}
	
	@Test
	public final void testEmailTagSetTagName() {
		assertEquals(null, this.tag.getTagName());
		this.tag.setTagName("SALUTATION");
		assertEquals("SALUTATION", this.tag.getTagName());
	}
	
	@Test
	public final void testEmailTagSetTagValue() {
		assertEquals(null, this.tag.getTagValue());
		this.tag.setTagValue("Mr");
		assertEquals("Mr", this.tag.getTagValue());
	}
	
	@Test
	public final void testEmailTagSetTagType() {
		assertEquals(null, this.tag.getTagType());
		this.tag.setTagType("TEXT");
		assertEquals("TEXT", this.tag.getTagType());
	}
	
	@Test
	public final void testEmailTagSetTagData() {
		assertEquals(null, this.tag.getTagData());
		this.tag.setTagData(this.file);
		assertEquals(this.file,  this.tag.getTagData());
	}
}

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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class EmailUserTest {
	
	private EmailUser user;
	
	@Before
	public void setUp() throws Exception {
		this.user = new EmailUser();
	}
	
	@Test
	public final void testEmailUserSetEmailAddress() throws InvalidEmailAddressException {
		assertEquals(null, this.user.getEmailAddress());
		this.user.setEmailAddress("bill.gates@microsoft.com");
		assertEquals("bill.gates@microsoft.com", this.user.getEmailAddress());
	}
	
	@Test
	public final void testEmailUserSetSalutation() {
		assertEquals(null, this.user.getSalutation());
		this.user.setSalutation("Mr");
		assertEquals("Mr", this.user.getSalutation());
	}
	
	@Test
	public final void testEmailUserSetFirstName() {
		assertEquals(null, this.user.getFirstName());
		this.user.setFirstName("Martin");
		assertEquals("Martin", this.user.getFirstName());
	}
	
	@Test
	public final void testEmailUserSetSurname() {
		assertEquals(null, this.user.getSurName());
		this.user.setSurName("Dickins");
		assertEquals("Dickins", this.user.getSurName());
	}
	
	@Test
	public final void testEmailUserSetAddress1() {
		assertEquals(null, this.user.getAddress1());
		this.user.setAddress1("10 Downing Street");
		assertEquals("10 Downing Street", this.user.getAddress1());
	}
	
	@Test
	public final void testEmailUserSetAddress2() {
		assertEquals(null, this.user.getAddress2());
		this.user.setAddress2("London");
		assertEquals("London", this.user.getAddress2());
	}
	
	@Test
	public final void testEmailUserSetAddress3() {
		assertEquals(null, this.user.getAddress3());
		this.user.setAddress3("England");
		assertEquals("England", this.user.getAddress3());
	}
	
	@Test
	public final void testEmailUserSetCountry() {
		assertEquals(0, this.user.getCountry());
		this.user.setCountry(74);
		assertEquals(74, this.user.getCountry());
	}
	
	@Test
	public final void testEmailUserSetLanguage() {
		assertEquals(0, this.user.getLanguageId());
		this.user.setLanguageId(25);
		assertEquals(25, this.user.getLanguageId());
	}
	
	@Test
	public final void testEmailUserSetPostcode() {
		assertEquals(null, this.user.getPostCode());
		this.user.setPostCode("W1 1PP");
		assertEquals("W1 1PP", this.user.getPostCode());
	}
	
	@Test
	public final void testEmailUserSetTelNo() {
		assertEquals(null, this.user.getTelephoneNumber());		
		this.user.setTelephoneNumber("01818118181");
		assertEquals("01818118181", this.user.getTelephoneNumber());
	}
	
	@Test
	public final void testEmailAddressIsValid() {
		assertTrue (this.user.isValidEmailAddress("martin.whittington@snapon.com"));
		assertFalse (this.user.isValidEmailAddress("gfgdfgdfgdf"));
		assertFalse (this.user.isValidEmailAddress(null));
		assertTrue (this.user.isValidEmailAddress("mw@h.co.uk"));
		assertFalse (this.user.isValidEmailAddress("mw@gfgf@fdfd@ff.co.yuk"));
	}
	
	@Test
	public final void testGenerateUUID() {
		assertTrue (this.user.generateUUID(UUID.randomUUID()) != 0);
		assertFalse (this.user.generateUUID(UUID.randomUUID()) == 0);
		
		// test for duplicates over an iteration of 25000
		// if the size of the set is not 25000, then we had a duplicate
		Set<Long> testSet = new HashSet<Long>();
		for (int i=0; i < 25000; i++) {
			testSet.add(this.user.generateUUID(UUID.randomUUID()));
		}
		assertTrue (testSet.size() == 25000);
	}
	
	@Test(expected=InvalidEmailAddressException.class)
	public final void testInvalidAddress()
			throws InvalidEmailAddressException {
		EmailUser user = new EmailUser();
		user.setEmailAddress("gfgdfgdfgdfg");
	}
	
	@Test
	public final void testEmailUserToString() {
		assertEquals(this.user.toString(), "[{\"email\":\"null\",\"uuid\":\"" + this.user.getUuid() + 
				"\",\"salutation\":\"null\",\"firstName\":\"null\",\"surName\":\"null\",\"address1\":\"null\","
				+ "\"address2\":\"null\",\"address3\":\"null\",\"countryId\":\"0\",\"languageId\":\"0\",\"postCode\":\"null\",\"telNo\":\"null\"}]");
	}
}

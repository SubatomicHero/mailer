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

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alisonassociates.npd.framework.util.LogUtil;
import com.alisonassociates.npd.framework.util.NxdLog;
import com.google.gson.annotations.SerializedName;
/**
 * A class representing the email user.
 * @author Martin Whittington
 *
 */
public class EmailUser {
	private static NxdLog logger = LogUtil.make();
	
	@SerializedName("text:emailRecipient")
	private String emailAddress;
	
	@SerializedName("uuid")
	private long uuid;
	
	@SerializedName("text:salutation")
	private String salutation;
	
	@SerializedName("text:firstName")
	private String firstName;
	
	@SerializedName("text:surname")
	private String surName;
	
	@SerializedName("text:address1")
	private String address1;
	
	@SerializedName("text:address2")
	private String address2;
	
	@SerializedName("text:address3")
	private String address3;
	
	@SerializedName("text:countryId")
	private int countryId;
	
	@SerializedName("text:languageId")
	private long languageId;
	
	@SerializedName("text:postCode")
	private String postCode;
	
	@SerializedName("text:telephoneNumber")
	private String telephoneNumber;
	
	/**
	 * Default constructor.
	 * @throws InvalidEmailAddressException if the email address is invalid
	 */
	public EmailUser() throws InvalidEmailAddressException {
		this(null, null, null, null, null, null, null, 0, 0, null, null);
	}

	/**
	 * Overloaded constructor.
	 * @param address the email address
	 * @param salutation the salutation
	 * @param firstName first name of the user
	 * @param surName surname of the user
	 * @param address1 line 1 of their address
	 * @param address2 line 2 of their address
	 * @param address3 line 3 of their address
	 * @param country the country of the user
	 * @param languageId the language of the user
	 * @param postCode the postcode of the user
	 * @param telNo the users telephone number
	 * @throws InvalidEmailAddressException if the email address is invalid
	 */
	public EmailUser(String address, String salutation, String firstName, String surName, String address1,
			String address2, String address3, int country, long languageId, String postCode, String telNo)
					throws InvalidEmailAddressException {
		this.emailAddress = address;
		this.uuid = this.generateUUID(UUID.randomUUID());
		this.salutation = salutation;
		this.firstName = firstName;
		this.surName = surName;
		this.address1 = address1;
		this.address2 = address2;
		this.address3 = address3;
		this.countryId = country;
		this.languageId = languageId;
		this.postCode = postCode;
		this.telephoneNumber = telNo;
	}
	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 * @throws InvalidEmailAddressException if the email address is invalid
	 */
	public void setEmailAddress(String emailAddress)
			throws InvalidEmailAddressException {
		if (!(this.isValidEmailAddress(emailAddress))) {
			throw new InvalidEmailAddressException(emailAddress + " is invalid for emailing. Please correct your records.");
		} else {
			this.emailAddress = emailAddress;
		}
	}
	
	/**
	 * @return the uuid
	 */
	public long getUuid() {
		return uuid;
	}
	
	/**
	 * @return the salutation
	 */
	public String getSalutation() {
		return salutation;
	}

	/**
	 * @param salutation the salutation to set
	 */
	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the surName
	 */
	public String getSurName() {
		return surName;
	}

	/**
	 * @param surName the surName to set
	 */
	public void setSurName(String surName) {
		this.surName = surName;
	}

	/**
	 * @return the address1
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * @param address1 the address1 to set
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	/**
	 * @return the address2
	 */
	public String getAddress2() {
		return address2;
	}

	/**
	 * @param address2 the address2 to set
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	/**
	 * @return the address3
	 */
	public String getAddress3() {
		return address3;
	}

	/**
	 * @param address3 the address3 to set
	 */
	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	/**
	 * @return the country
	 */
	public int getCountry() {
		return countryId;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(int country) {
		this.countryId = country;
	}

	/**
	 * @return the languageId
	 */
	public long getLanguageId() {
		return languageId;
	}

	/**
	 * @param languageId the languageId to set
	 */
	public void setLanguageId(long languageId) {
		this.languageId = languageId;
	}

	/**
	 * @return the postCode
	 */
	public String getPostCode() {
		return postCode;
	}

	/**
	 * @param postCode the postCode to set
	 */
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	/**
	 * @return the telephoneNumber
	 */
	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	/**
	 * @param telephoneNumber the telephoneNumber to set
	 */
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}
	
	@Override
	public String toString() {
		return "[{\"email\":\"" + this.emailAddress + "\",\"uuid\":\"" + String.valueOf(uuid) + "\",\"salutation\":\"" + this.salutation +
				"\",\"firstName\":\"" + this.firstName + "\",\"surName\":\"" + this.surName + "\",\"address1\":\"" + this.address1 +
				"\",\"address2\":\"" + this.address2 + "\",\"address3\":\"" + this.address3 + "\",\"countryId\":\"" + this.countryId +
				"\",\"languageId\":\"" + this.languageId + "\",\"postCode\":\"" + this.postCode + "\",\"telNo\":\"" + this.telephoneNumber + "\"}]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((emailAddress == null) ? 0 : emailAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EmailUser other = (EmailUser) obj;
		if (emailAddress == null) {
			if (other.emailAddress != null) {
				return false;
			}
		} else if (!emailAddress.equals(other.emailAddress)) {
			return false;
		}
		return true;
	}

	/**
	 * This method checks that the email address is valid and passes regular expression.
	 * NOTE: The regex in this method SHOULD be updated incase newer regex's are available.
	 * @param address the email address to check
	 * @return true if the address is valid, false otherwise
	 */
	boolean isValidEmailAddress(String address) {
		if (!("".equals(address) || address == null)) {
			String  expression="\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
			CharSequence inputStr = address;
			Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(inputStr);
			return matcher.matches();
		}
		return false;
	}
	
	/**
	 * This method generates a universally unique identifier. The UUID class
	 * generates a id that contains numbers, letters and characters. The purpose
	 * of the this method is to strip the letters and characters away and 
	 * return a long
	 * @param id The UUID to parse
	 * @return the cleaned long value
	 */
	long generateUUID(UUID id) {
		long retValue = 0;
		String pattern = id.toString();
		try {
			// This regex removes all non numeric characters from the string
			pattern = pattern.replaceAll("[^\\d]", "");
			pattern = pattern.substring(0, Math.min(15, pattern.length()));
			retValue = Long.parseLong(pattern);
		} catch (NumberFormatException e) {
			logger.error(pattern + " is not a number");
			// recall this method to get a new number, hopefully this only happens once
			retValue = this.generateUUID(UUID.randomUUID());
		}
		return retValue;
	}
}

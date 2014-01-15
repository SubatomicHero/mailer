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

import java.io.File;
import com.alisonassociates.npd.framework.util.LogUtil;
import com.alisonassociates.npd.framework.util.NxdLog;

/**
 * This class represents a tag and the details it can carry.
 * A tag is a string in an agreed syntax that allows itself
 * to be replaced by either text, an image or a link. This
 * should give the users flexibility on customising their templates.
 * @author Martin Whittington
 */
public class EmailTag {
	private static NxdLog logger = LogUtil.make();
	private long jobId;
	private String tagName;
	private String tagValue;
	private String tagType;
	private File tagData;
	
	/**
	 * Default constructor.
	 */
	public EmailTag() {
		this(0, null, null, null, null);
		logger.trace("EmailTag() created");
	}
	
	/**
	 * Overloaded constructor.
	 * @param jobId the job id
	 * @param tagName the tag name
	 * @param tagValue the tag value
	 * @param tagType the tag type
	 * @param file the file if any
	 */
	public EmailTag(long jobId, String tagName, String tagValue, String tagType, File file) {
		this.jobId = jobId;
		this.tagName = tagName;
		this.tagValue = tagValue;
		this.tagType = tagType;
		this.tagData = file;
	}
	
	/**
	 * Sets the job id
	 * @param job_id the job id to set
	 */
	public void setJobId(long job_id) {
		this.jobId = job_id;
	}
	
	/**
	 * Gets the job id
	 * @return the job id
	 */
	public long getJobId() {
		return this.jobId;
	}
	
	/**
	 * @return the tagName
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * @param tagName the tagName to set
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	/**
	 * @return the tagValue
	 */
	public String getTagValue() {
		return tagValue;
	}

	/**
	 * @param tagValue the tagValue to set
	 */
	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}

	/**
	 * @return the tagType
	 */
	public String getTagType() {
		return tagType;
	}

	/**
	 * @param tagType the tagType to set
	 */
	public void setTagType(String tagType) {
		this.tagType = tagType;
	}

	/**
	 * @return the tagData
	 */
	public File getTagData() {
		return tagData;
	}

	/**
	 * @param tagData the tagData to set
	 */
	public void setTagData(File tagData) {
		this.tagData = tagData;
	}
}

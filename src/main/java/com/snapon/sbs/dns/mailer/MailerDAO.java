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

import java.io.IOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alisonassociates.npd.framework.base.Environment;
import com.alisonassociates.npd.framework.exception.PersistenceException;
import com.alisonassociates.npd.framework.sql.SqlTools;
import com.alisonassociates.npd.framework.util.LogUtil;
import com.alisonassociates.npd.framework.util.NxdLog;
import com.alisonassociates.npd.workflowmanager.JobStatus;
import com.alisonassociates.npd.workflowmanager.ServiceException;
import com.snapon.sbs.dns.emailsettings.EmailJob;
import com.snapon.sbs.dns.emailsettings.EmailLog;
import com.snapon.sbs.dns.emailsettings.EmailMessageType;
import com.snapon.sbs.dns.emailsettings.EmailPriority;
import com.snapon.sbs.dns.emailsettings.EmailProperty;
import com.snapon.sbs.dns.emailsettings.EmailRecipient;
import com.snapon.sbs.dns.emailsettings.EmailUser;

/**
 * Class to help pass jobs objects to and from the database.
 * @author Martin Whittington
 */
public class MailerDAO {
	
	private static NxdLog logger = LogUtil.make();
	public final static int ENTRY_FAILED = -999;
	public final static int ENTRY_PRESENT = 100;
	
	/**
	 * Returns a list of email jobs with a status equal to <code>status</code>.
	 * @param status the status to look for
	 * @return The list of matching email jobs
	 * @throws ServiceException If a database error occurs
	 */
	static EmailJob selectJobByState(JobStatus status)
		throws ServiceException {
		logger.debug("selectJobByState(): ", status.desc());
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		EmailJob job = null;
		try {
			conn = SqlTools.getConnection();
			conn.setAutoCommit(false);
			String sql = "SELECT * from EMAIL_JOB WHERE rowid IN (SELECT rowid from(" + 
							"SELECT e.rowid, row_number() over(ORDER by priority, date_created) ord " +
							"FROM EMAIL_JOB e where status=?) where ord=1) FOR UPDATE";
			
			// TODO scheduled jobs here?
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, status.dbKey());
			rs = stmt.executeQuery();
			if (rs.next()) {
				job = createJob(rs);
				job.setJobState(JobStatus.RUNNING);
				updateJob(job, conn);
			}
			conn.commit();
		} catch (SQLException e) {
			logger.error("Error getting email jobs in status: " + status.desc(), e);
			throw new PersistenceException("Error getting email jobs with status " + status.desc(), e);
		} finally {
			SqlTools.free(conn, rs, stmt);
		}
		return job;
	}
	
	/**
	 * Returns an emailjob with the specified job id
	 * @param jobId the id to look for
	 * @return the email job which may be null if no job was found
	 * @throws ServiceException if a database error occurs
	 */
	public static EmailJob selectByJobId(long jobId) throws ServiceException {
		logger.debug("selectByJobId(): " + jobId);
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		EmailJob job = null;
		try {
			conn = SqlTools.getReadonlyConnection();
			String statement = "SELECT * from EMAIL_JOB WHERE job_id = ?";
			stmt = conn.prepareStatement(statement);
			stmt.setLong(1, jobId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				job = createJob(rs);
			}
			logger.debug("selectByJobId: job ", jobId, " found");
		} catch (SQLException e) {
			logger.error("Error getting job by id: ", e);
		} finally {
			SqlTools.free(conn, rs, stmt);
		}
		return job;
	}
	
	/**
	 * This method creates an EmailJob bean from the current row of the ResultSet.
	 * @param rs the result set to read
	 * @return an email job created from a resultset
	 * @throws SQLException If an SQL error occurs while retrieving the database record 
	 */
	private static EmailJob createJob(ResultSet rs) 
			throws SQLException {
		EmailJob localJob = new EmailJob();
		localJob.setJobId(rs.getLong("JOB_ID"));
		localJob.setDateCreated(rs.getDate("DATE_CREATED"));
		localJob.setSchema(rs.getString("SCHEMA"));
		localJob.setRequesterId(rs.getString("CREATED_BY"));
		localJob.setEmailType(rs.getString("EMAIL_TYPE"));
		localJob.setTemplateId(rs.getInt("TEMPLATE_ID"));
		localJob.setJobState(JobStatus.NOTSTARTED);
		localJob.setJobPriority(EmailPriority.values()[rs.getInt("PRIORITY")]);
		localJob.setSender(rs.getString("SENDER"));
		localJob.setJSONData(rs.getString("JSON_DATA"));
		logger.debug("createJob(): ", localJob);
		return localJob;
	}
	
	/**
	 * This method updates a jobs state onto the database.
	 * @param job the job we're interested in
	 * @param conn the current connection
	 * @throws ServiceException If an SQL error occurs
	 */
	static void updateJob(EmailJob job, Connection conn) throws ServiceException {
		PreparedStatement stmt = null;
		logger.debug("updateJob(): ", job);
		try {
			// Build the sql statement
			String sql = "UPDATE EMAIL_JOB set status=? WHERE job_id=?";
			stmt = conn.prepareStatement(sql);
			
			// Now set the details in the statement
			stmt.setInt(1, job.getJobState().dbKey());
			stmt.setLong(2, job.getJobId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new ServiceException("Error storing job: " + job, e);
		} finally {
			SqlTools.free(stmt);
		}
	}
	
	/**
	 * This method adds an entry into the email log table. The message type
	 * is either information, warning or error.<P>Connection cannot be null if calling this method
	 * @param currentLog the log entry to add
	 * @param conn the current connection
	 * @throws ServiceException if anything else goes wrong
	 */
	static final void insertLog(EmailLog currentLog, Connection conn) throws ServiceException {
		if (currentLog == null) {
			logger.error("updateJobLog() currentLog is null. Cannot update log");
			return;
		}
		PreparedStatement stmt = null;
		logger.debug("updateJobLog(): ", currentLog);
		
		try {
			// Build the SQL statement
			String sql = "INSERT into EMAIL_LOG VALUES (?,sysdate,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setLong(1, currentLog.getJobId());
			stmt.setString(2, currentLog.getMessage());
			stmt.setString(3, currentLog.getMessageType().getType());
			stmt.setString(4, currentLog.getUserId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new ServiceException("Error inserting log: ", e);
		} finally {
			SqlTools.free(stmt);
		}
	}
	
	/**
	 * This method inserts a job into the database
	 * @param job The job to insert into the EMAIL_JOB table
	 * @return the job id if successfully added into the db, -999 if there was an error
	 * @throws ServiceException if anything goes wrong with the SQL
	 * @throws IOException if an error occurs with the clob writing
	 */
	public static long insertJob(EmailJob job)
			throws ServiceException, IOException {
		job.setJobId(SqlTools.getNextInSeq("job_id_seq"));
		long retValue = 0;
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		logger.debug("insertJob(): ", job);
		try {
			conn = SqlTools.getConnection();
			
			// First lets create a record
			String sql = "INSERT into EMAIL_JOB values (?,sysdate,?,?,?,?,?,?,?,empty_clob())";
			st = conn.prepareStatement(sql);
			st.setLong(1, job.getJobId());
			st.setString(2, job.getSchema());
			st.setString(3, job.getRequesterId());
			st.setString(4, job.getEmailType());
			st.setInt(5, job.getTemplateId());
			st.setInt(6, job.getJobState().dbKey());
			st.setInt(7, job.getJobPriority().getOrder());
			st.setString(8, job.getSender());
			// an empty clob is added into the statement to prepare the column for data
			st.executeUpdate();
			st.close();
			
			// Now we handle the clob
			sql = "SELECT json_data from EMAIL_JOB where job_id=? FOR UPDATE of json_data";
			st = conn.prepareStatement(sql);
			st.setLong(1, job.getJobId());
			rs = st.executeQuery();
			while (rs.next()) {		// we should only ever get one record
				Clob tempClob = rs.getClob("JSON_DATA");
				tempClob.setString(1, job.getJSONValue());
				// we have updated the clob locally, lets send it back to the database
				sql = "UPDATE EMAIL_JOB set json_data=? where job_id=?";
				st = conn.prepareStatement(sql);
				st.setClob(1, tempClob);
				st.setLong(2, job.getJobId());
				st.executeUpdate();
			}
			// we commit here just incase the db is locked
			conn.commit();
			retValue = job.getJobId();
		} catch (SQLException e) {
			retValue = ENTRY_FAILED;
			logger.error("Error creating job: " + job, e);
			throw new PersistenceException("Error creating job: ", e);
		} finally {
			SqlTools.free(conn, rs, st);
		}
		return retValue;
	}
	
	/**
	 * This method inserts a record into the EMAIL_RECIPIENT table per
	 * recipient in the passed list. There will be one job id per job but with
	 * multiple recipients.
	 * @param recipients the recipients to add to the EMAIL_RECIPIENT table
	 * @param conn the current connection
	 */
	static final void insertEmailRecipient(List <EmailRecipient> recipients, Connection conn) {
		if (recipients == null || recipients.size() < 1) {
			logger.error("updateEmailRecipient() cannot insert recipients. List is either empty or null");
			return;
		}
		logger.debug("insertEmailRecipient(): ", recipients);
		PreparedStatement stmt = null;
		
		// variables to control batch sizes, just incase
		final int batchSize = 1000;
		int count = 0;
		
		try {
			String sql = "INSERT into EMAIL_RECIPIENT (job_id, email_address, uuid) values (?,?,?)";// no date opened yet
			stmt = conn.prepareStatement(sql);
			for (EmailRecipient rec : recipients) {
				stmt.setLong(1, rec.getJobId());
				stmt.setString(2, rec.getEmailAddress());
				stmt.setLong(3, rec.getUuid());
				stmt.addBatch();
				
				if (++count % batchSize == 0) {	// if there are more than 1000 records lets add that batch
					stmt.executeBatch();
				}
			}
			stmt.executeBatch();
		} catch (SQLException e) {
			logger.error("EXCEPTION updating email recipient list: ", e);
		} finally {
			SqlTools.free(stmt);
		}
	}
	
	/**
	 * This method will insert entries into the email user table
	 * based on the size of the user list that comes in
	 * @param userList the list of users that needs to be entered
	 * @param conn the current connection
	 */
	static final void insertEmailUser(Set<EmailUser> userList, Connection conn) {
		if (userList == null || userList.size() < 1) {
			logger.error("insertEmailUser() cannot insert userList");
			return;
		}
		logger.debug("insertEmailUser(): " + userList);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = null;		
		try {
			for (EmailUser user : userList) {
				// First check we dont already have the email address in the email_user table
				sql = "SELECT email_address FROM EMAIL_USER where email_address = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, user.getEmailAddress());
				rs = stmt.executeQuery();
				boolean update = rs.next();
				stmt.close();
				// if we dont have that email address we need to insert it in the table with the new details
				if (!update) {
					// resultset is empty
					sql = "INSERT into EMAIL_USER values (?,?,?,?,?,?,?,?,?,?,?,?)";
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, user.getEmailAddress());
					stmt.setLong(2, user.getUuid());
					stmt.setString(3, user.getSalutation());
					stmt.setString(4, user.getFirstName());
					stmt.setString(5, user.getSurName());
					stmt.setString(6, user.getAddress1());
					stmt.setString(7, user.getAddress2());
					stmt.setString(8, user.getAddress3());
					stmt.setInt(9, user.getCountry());
					stmt.setLong(10, user.getLanguageId());
					stmt.setString(11, user.getPostCode());
					stmt.setString(12, user.getTelephoneNumber());
					stmt.executeUpdate();
				} else {
					// otherwise we update the record with everything else apart from the email address
					// resultset is not empty
					sql = "UPDATE EMAIL_USER set uuid=?, salutation=?, first_name=?, surname=?, address1=?,"
							+ " address2=?, address3=?, country_id=?, language_id=?, post_code=?, tel_no=?"
							+ " where email_address=?";	
					stmt = conn.prepareStatement(sql);
					stmt.setLong(1, user.getUuid());
					stmt.setString(2, user.getSalutation());
					stmt.setString(3, user.getFirstName());
					stmt.setString(4, user.getSurName());
					stmt.setString(5, user.getAddress1());
					stmt.setString(6, user.getAddress2());
					stmt.setString(7, user.getAddress3());
					stmt.setInt(8, user.getCountry());
					stmt.setLong(9, user.getLanguageId());
					stmt.setString(10, user.getPostCode());
					stmt.setString(11, user.getTelephoneNumber());
					stmt.setString(12, user.getEmailAddress());
					stmt.executeUpdate();
				}
			}
		} catch (SQLException e) {
			logger.error("EXCEPTION upating email user list: ", e);
		} finally {
			SqlTools.free(rs, stmt);
		}
	}
	
	/**
	 * This method will insert a property entry into the EMAIL_property table.
	 * @param property the property to add into the db.
	 * @param conn the current connection
	 */
	static final void insertJobProperties(EmailProperty property, Connection conn) {
		if (property == null) {
			logger.error("updateJobProperties() property is null");
			return;
		}
		logger.debug("updateJobProperties()");
		PreparedStatement stmt = null;
		
		// variables to control batch sizes, just incase
		final int batchSize = 100;
		int count = 0;
		
		try {
			String sql = "INSERT into EMAIL_PROPERTY values (?,?,?)";
			stmt = conn.prepareStatement(sql);
			for (Map.Entry<String, Object> entry : property.getNameValuePair().entrySet()) {
				// for every name/value pair we have
				stmt.setLong(1, property.getJobId());
				stmt.setString(2, entry.getKey());
				stmt.setString(3, entry.getValue().toString());
				stmt.addBatch();
				
				if (++count % batchSize == 0) {	// to ensure we dont run out of memory
					stmt.executeBatch();
				}
			}
			stmt.executeBatch();
		} catch (SQLException e) {
			logger.error("Exception updating email properties: ", e);
		} finally {
			SqlTools.free(stmt);
		}
	}
	
	/**
	 * This method will remove an entry from the EMAIL_BLACKLIST table
	 * re-subscribing the email address to a particular email type.
	 * @param whiteList the list of email address that need removing with the email type
	 * @return an int based on the success or failure of the operation
	 */
	public static final int removeFromBlackList(Map<String, Object> whiteList) {
		if (whiteList == null || whiteList.isEmpty()) {
			logger.error("Whitelist entry cannot be made. List is null or empty");
			return ENTRY_FAILED;
		}
		// The map is a parsed json string with these variables
		// Key = "emailAddress" value = "email address to remove",
		// Key = "emailType" value ="type of email to subscribe to" (if null then all)
		Connection conn = null;
		PreparedStatement stmt = null;
		int retValue = 0;
		try {
			conn = SqlTools.getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append("DELETE from EMAIL_BLACKLIST where email_address=?");
			sql.append((whiteList.get("emailType") == null || "ALL".equalsIgnoreCase(whiteList.get("emailType").toString())
					? "" : " AND email_type=?"));
			sql.append(" AND schema=?");
			stmt = conn.prepareStatement(sql.toString());
			stmt.setString(1, whiteList.get("emailAddress").toString());
			if (!(whiteList.get("emailType") == null || "ALL".equalsIgnoreCase(whiteList.get("emailType").toString()))) {
				stmt.setString(2, whiteList.get("emailType").toString());
				stmt.setString(3, whiteList.get("schema").toString());
			} else {
				stmt.setString(2, whiteList.get("schema").toString());
			}
			retValue = stmt.executeUpdate();
			
			// Lets keep a log of this action
			insertLog(new EmailLog(Long.valueOf(0), new Date(0), whiteList.get("emailAddress").toString() + " has been removed from the blacklist.",
					EmailMessageType.INFORMATION, whiteList.get("emailAddress").toString()), conn);
		} catch (SQLException | NumberFormatException | ServiceException e) {
			logger.error("Error removing entry from EMAIL_BLACKLIST: ", e);
			retValue = ENTRY_FAILED;
		} finally {
			SqlTools.free(conn, stmt);
		}
		return retValue;
	}
	
	/**
	 * This method inserts an entry into the EMAIL_BLACKLIST table
	 * @param uuid the unique id needed to add the correct entry
	 * @return the status of the entry, needed for feedback
	 */
	public static final int insertBlackList(long uuid) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int retValue = 0;
		try {
			conn = SqlTools.getConnection();
			String emailAddress = null;
			String schema = null;
			String emailType = null;
			
			// First we need to get the email address and schema matching the uuid
			String sql = "SELECT email_address, schema, email_type from email_recipient r, email_job j"
					+ " WHERE r.job_id = j.job_id AND r.UUID=?";
			stmt = conn.prepareStatement(sql);
			stmt.setLong(1, uuid);
			rs = stmt.executeQuery();
			while (rs.next()) {
				// we should only get one row
				emailAddress = rs.getString(1);
				schema = rs.getString(2);
				emailType = rs.getString(3);
			}
			stmt.close();
			rs.close();
			
			// Now we can add this record to the table
			sql = "SELECT email_address from email_blacklist where email_address=? and schema=? and email_type=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, emailAddress);
			stmt.setString(2, schema);
			stmt.setString(3, emailType);
			
			rs = stmt.executeQuery();
			if(!rs.next()) {
				// insert a new record
				stmt.close();
				sql = "INSERT into email_blacklist VALUES (?,?,sysdate,?)";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, emailAddress);
				stmt.setString(2, emailType);
				stmt.setString(3, schema);
				retValue = stmt.executeUpdate();
				insertLog(new EmailLog(Long.valueOf(0), new Date(System.currentTimeMillis()),
						emailAddress + " has been added to the blacklist and will not recieve emails of type " + emailType,
						EmailMessageType.INFORMATION, emailAddress), conn);
			} else {
				// already present
				retValue = ENTRY_PRESENT;
			}
		} catch (SQLException | ServiceException e) {
			logger.error("Error in blacklist: ", e);
			retValue = -ENTRY_FAILED;
		} finally {
			SqlTools.free(conn, rs, stmt);
		}
		return retValue;
	}
	
	/**
	 * This method returns a List of blacklisted emails that should not receive emails
	 * based on the type of email they have unsubscribed from.
	 * @param schema the schema to refine to
	 * @param conn the current connection
	 * @return a list of blacklist objects or an empty list
	 */
	static final List<Map<String, String>> getBlackList(String schema, Connection conn) {
		if (schema == null || "".equals(schema)) {
			logger.error("Blacklist list cannot be retrieved.");
			return null;
		}
		logger.debug("getBlackList(): " + schema);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Map<String, String>> blackList = new ArrayList<Map<String, String>>();
		try {
			String sql = "SELECT NVL(email_address, ' '), NVL(email_type, ' ') FROM EMAIL_BLACKLIST WHERE schema=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, schema);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("email_address", rs.getString("EMAIL_ADDRESS").toLowerCase(Environment.sysLocale));
				map.put("email_type", rs.getString("EMAIL_TYPE").toLowerCase(Environment.sysLocale));
				blackList.add(map);
			}
		} catch (SQLException e) {
			logger.error("Exception getting blacklist: ", e);
		} finally {
			SqlTools.free(rs, stmt);
		}
		logger.debug("blacklist is " + blackList);
		return blackList;
	}
	
	/**
	 * This method will update the email recipient table with the exact time and date
	 * the email was opened (i.e. when the tracking servlet is activated) ONLY if
	 * the email hasnt already been opened.
	 * @param uuid the uuid to update
	 */
	public final static void updateEmailRecipient(long uuid) {
		logger.trace("updateEmailRecipient(): ", uuid);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = SqlTools.getConnection();
			String sql = "SELECT date_opened from email_recipient WHERE uuid=? AND date_opened is null";
			stmt = conn.prepareStatement(sql);
			stmt.setLong(1, uuid);
			rs = stmt.executeQuery();
			boolean isPresent = rs.next();
			stmt.close();
			if (!isPresent) {
				// resultset is empty
				logger.trace(uuid + " has already opened the email.");
			} else {
				// we need to update the record
				sql = "UPDATE email_recipient SET date_opened=sysdate WHERE uuid=?";
				stmt = conn.prepareStatement(sql);
				stmt.setLong(1, uuid);
				stmt.executeUpdate();
			}
		} catch (SQLException e) {
			logger.error("Could not update the email_recipient table: ", e);
		} finally {
			SqlTools.free(conn, rs, stmt);
		}
	}
	
	/**
	 * This method returns a map of templates the user is allowed to see based on their security class, 
	 * schema, country id and language id.
	 * @param securityClasses the map containing the parsed json string from the application
	 * @return a map containing the template id, template file and descripion
	 */
	public final static List<Map<String, Object>> getTemplates(Map<String, Object> securityClasses) {
		logger.debug("getTemplates(): ", securityClasses);
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Map<String, Object>> templates = new ArrayList<Map<String, Object>>();
		try {
			conn = SqlTools.getConnection();
			List<?> list = (List<?>) securityClasses.get("securityClass");	// put the security class into a list
			
			// Build the sql statement
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT template_id, template_file, description, security_class FROM email_template ");
			sql.append("WHERE (schema=? or schema is null) and (country_id=? or country_id is null) ");
			sql.append("and (language_id=? or language_id is null) and template_type like '%'||?||'%'");
			logger.trace("templates sql is: " + sql.toString());
			stmt = conn.prepareStatement(sql.toString());
			stmt.setString(1, securityClasses.get("schema").toString());
			stmt.setInt(2, Integer.parseInt(securityClasses.get("countryId").toString()));
			stmt.setInt(3, Integer.parseInt(securityClasses.get("languageId").toString()));
			stmt.setString(4, securityClasses.get("templateType").toString());
			
			rs = stmt.executeQuery();
			// if we have results
			if (rs.next()) {
				do {
					if (list.isEmpty()) {
						// we have received a request for a template with someone with no security access
						// return an empty list. ALL users whom have access to the mailer should also have
						// associated security classes.
						break;
					}
					// split the possible list of classes into an array
					String[] classes = rs.getString("SECURITY_CLASS").split(",");
					// and check whether any of the classes in the list match the contents of the array
					if (classes.length > 0) {
						for (String cls : classes) {
							for (Object obj : list) {
								String otherClass = obj.toString();
								if (otherClass.toLowerCase(Environment.sysLocale).equals(cls.toLowerCase(Environment.sysLocale))) {
									Map<String, Object> template = new HashMap<String, Object>();
									template.put("templateId", rs.getString("TEMPLATE_ID"));
									template.put("templateFile", rs.getString("TEMPLATE_FILE"));
									template.put("description", rs.getString("DESCRIPTION"));
									templates.add(template);
								}
							}
						}
					}
				} while (rs.next());
			}
		} catch (SQLException e) {
			logger.error("Error retrieving templates list: ", e);
		} finally {
			SqlTools.free(conn, rs, stmt);
		}
		logger.debug("getTemplates() returning: ", templates);
		return templates;
	}

	/**
	 * This method returns the html string that is need to populate the emails.
	 * The tags within the email are to then be substituted by the mailer handler.
	 * @param templateId the template id to look for
	 * @param schema the schema the template is associated with
	 * @return the html template in string format that we can parse
	 */
	public static String getTemplateFile(int templateId, String schema) {
		return getTemplateFile(templateId, schema, null);
	}
	
	/**
	 * This method returns the html string that is need to populate the emails.
	 * The tags within the email are to then be substituted by the mailer handler.<p>
	 * This method must have a valid connection and not be passed null.
	 * @param templateId the template id to look for
	 * @param schema the schema the template is associated with
	 * @param conn the current connection
	 * @return the html template in string format that we can parse
	 */
	public static String getTemplateFile(int templateId, String schema, Connection conn) {
		logger.debug("getTemplateFile(): ", templateId, " ", schema);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection localConn = conn;
		boolean freeConn = false;
		if (localConn == null) {
			localConn = SqlTools.getConnection();
			freeConn = true;
		}
		String result = "";
		try {
			String sql = "SELECT template_file FROM email_template WHERE template_id=? AND (schema=? OR schema is null)";
			stmt = localConn.prepareStatement(sql);
			stmt.setInt(1, templateId);
			stmt.setString(2, schema);
			rs = stmt.executeQuery();
			while (rs.next()) {
				result = rs.getString("TEMPLATE_FILE");
			}
		} catch (SQLException e) {
			logger.error("Error retrieving template file: templateid: " + templateId + " schema: " + schema, e);
			throw new PersistenceException("Error retrieving template file: templateid: " + templateId + " schema: " + schema, e);
		} finally {
			SqlTools.free(rs, stmt);
			if (freeConn) {
				SqlTools.free(localConn);
			}
		}
		logger.trace("returning ", result);
		return result;
	}

	/**
	 * This method will add a template into the EMAIL_TEMPLATE table that has been created
	 * by a user of an application.
	 * @param templateDetails the map containing the details
	 * @return either the template id if successful or -999 if there was an error
	 */
	public static long insertTemplate(Map<String, Object> templateDetails) {
		logger.debug("insertTemplate(): ", templateDetails);
		templateDetails.put("templateId", SqlTools.getNextInSeq("template_id_seq"));
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		long status = 0;
		
		try {
			conn = SqlTools.getConnection();
			String sql = "INSERT into email_template VALUES (?,?,empty_clob(),?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setLong(1, Long.parseLong(templateDetails.get("templateId").toString()));
			stmt.setString(2, templateDetails.get("schema").toString());
			stmt.setString(3, templateDetails.get("description").toString());
			stmt.setString(4, templateDetails.get("format").toString());
			stmt.setString(5, templateDetails.get("templateType").toString());
			stmt.setString(6, templateDetails.get("securityClass").toString());
			stmt.setInt(7, Integer.parseInt(templateDetails.get("countryId").toString()));
			stmt.setInt(8, Integer.parseInt(templateDetails.get("languageId").toString()));
			stmt.setString(9, templateDetails.get("subjectText").toString());
			stmt.executeUpdate();
			stmt.close();
			
			// now we handle the clob part of the query			
			sql = "SELECT template_file FROM email_template WHERE template_id=? FOR UPDATE of template_file";
			stmt = conn.prepareStatement(sql);
			stmt.setLong(1, Long.parseLong(templateDetails.get("templateId").toString()));
			rs = stmt.executeQuery();
			while (rs.next()) {
				Clob tempClob = rs.getClob("TEMPLATE_FILE");
				
				tempClob.setString(1, templateDetails.get("templateFile").toString());
				sql = "UPDATE email_template SET template_file=? where template_id=?";
				stmt = conn.prepareStatement(sql);
				stmt.setClob(1, tempClob);
				stmt.setLong(2, Long.parseLong(templateDetails.get("templateId").toString()));
				stmt.executeUpdate();
			}
			conn.commit();
			status = Long.parseLong(templateDetails.get("templateId").toString());
		} catch (SQLException e) {
			logger.error("Error inserting template: ", e);
			status = ENTRY_FAILED;
		} finally {
			SqlTools.free(conn, rs, stmt);
		}
		return status;
	}
	
	/**
	 * This method inserts email tags into the database ONLY if the job entry was successful.
	 * We then retrieve these tags and use them for the substitution in mailer handler
	 * @param jobId the job id these tags are related to
	 * @param tags the tags collection
	 */
	public static void insertEmailTags(long jobId, List<Map<String, Object>> tags) {
		logger.debug("insertEmailTags(): " + tags);
		PreparedStatement stmt = null;
		Connection conn = null;
		
		try {
			// first insert the tags into the table
			conn = SqlTools.getConnection();
			String sql = "INSERT into email_tag values (?,?,?,?,empty_blob(),?)";
			stmt = conn.prepareStatement(sql);
			String emailAddress = null;
			for (Map<String, Object> tagPairs : tags) {
				for (Map.Entry<String, Object> pair : tagPairs.entrySet()) {
					if (tagPairs.get("emailRecipient") != null) {
						emailAddress = tagPairs.get("emailRecipient").toString();
					}
					stmt.setLong(1, jobId);
					stmt.setString(2, pair.getKey());
					stmt.setString(3, pair.getValue().toString());
					stmt.setString(4, (pair.getKey().contains("TEXT:") ? "TEXT" : (pair.getKey().contains("IMAGE:") ? "IMAGE" : "ATTACHMENT")));
					stmt.setString(5, emailAddress);
					stmt.addBatch();
				}
			}
			stmt.executeBatch();
			stmt.close();
			
			// second update the tag that relates to the job if one exists for job_id 0, which are
			// preconfigured tags and images
			String updateSql = "UPDATE email_tag t SET (tag_value, tag_data)=" +
			"(SELECT s.tag_value, s.tag_data FROM email_tag s " + 
			"WHERE s.job_id=0 AND s.tag_type = t.tag_type " + 
			"AND s.tag_name = t.tag_name) WHERE t.job_id=? " +
			"AND (tag_name, tag_type) in (SELECT tag_name, tag_type " +
			"FROM email_tag where job_id=0)";
			
			stmt = conn.prepareStatement(updateSql);
			stmt.setLong(1, jobId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("Error inserting email tags: ", e);
			return;
		} finally {
			SqlTools.free(conn, stmt);
		}
	}
	
	/**
	 * This method retrieves a list of email tags based on the job id. As each job id is unique
	 * in the email tag table this method only needs to return a map rather than a list of maps.
	 * @param jobId the job id relating to the email tags
	 * @param conn the current connection
	 * @return a populated map of tag names and tag values, or null if something fails
	 */
	public static Map<String, Object> getEmailTags(long jobId, Connection conn) {
		logger.debug("getEmailTags(): " + jobId);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Map<String, Object> tags = new HashMap<String, Object>();
		
		try {
			String sql = "SELECT tag_name, tag_value from email_tag where job_id=?";
			stmt = conn.prepareStatement(sql);
			stmt.setLong(1, jobId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				tags.put(rs.getString("TAG_NAME"), rs.getString("TAG_VALUE"));
			}
		} catch (SQLException e) {
			logger.error("Error getting email tags: ", e);
			tags = null;
		} finally {
			SqlTools.free(rs, stmt);
		}
		return tags;
	}
	
	/**
	 * This method return a blob image that is used in each email that needs tag replacement.
	 * @param tagName the tagname to look for
	 * @param pathName the pathName this request came from
	 * @return the blob if the tagname exists or null
	 */
	public static Blob getEmailImage(String tagName, String pathName) {
		logger.debug("getEmailImage(): tag "+ tagName + " path " + pathName);
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Blob image = null;
		Connection conn = SqlTools.getConnection();
		try {
			String sql = "SELECT tag_data from email_tag where tag_name=? and job_id=0";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, tagName);
			rs = stmt.executeQuery();
			if (rs.next()) {
				image = rs.getBlob("TAG_DATA");
			}
			stmt.close();
			
			sql = "UPDATE email_tag set tag_value=? WHERE job_id=0 and tag_name=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, pathName);
			stmt.setString(2, tagName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("Error getting email image: ", e);
		} finally {
			SqlTools.free(conn, rs, stmt);
		}
		return image;
	}
}

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
import java.sql.SQLException;
import java.util.Properties;

import com.alisonassociates.npd.framework.base.Environment;
import com.alisonassociates.npd.framework.schedule.ManagedTask;
import com.alisonassociates.npd.framework.schedule.TaskException;
import com.alisonassociates.npd.framework.sql.SqlTools;
import com.alisonassociates.npd.framework.util.LogUtil;
import com.alisonassociates.npd.framework.util.NxdLog;
import com.alisonassociates.npd.workflowmanager.JobStatus;
import com.alisonassociates.npd.workflowmanager.ServiceException;
import com.snapon.sbs.dns.emailsettings.EmailJob;

/**
 * This class polls the EMAIL_JOB table for new jobs and if it finds any
 * that have not yet started, will pass them to a mailer handler.
 * @author Martin Whittington
 */
public class MailerService implements ManagedTask {
	private static String handlerBaseClass = "com.alisonassociates.npd.workflowmanager.JobHandler";
	private static NxdLog logger = LogUtil.make();
	
	/** 15 second delay to check if this handler has any jobs to do */
	private static final int DELAY = 15000;
	
	/** Flag indicating whether the Handler should keep running or terminate. */ 
	private volatile boolean keepRunning = true;
	private volatile int status = ManagedTask.NOT_STARTED;
	
	// Variables needed for the smtpserver status
	private Properties properties;
	private String mailServer;
	
	// for threading development
	private EmailJob[] currentJobs = null;
	private MailerHandler[] mailers = null;
	private Thread[] threads = null;
	private int numHandlers = 1;	// default if the property isnt present
	private String handlerClass = null;
	
	@Override
	public void run() {
		try {
			this.start();
			this.processingLoop();
		} catch (Exception e) {
			logger.error("run() exception: ", e);
		}
	}

	@Override
	public void init(Properties properties) throws TaskException {
		this.properties = properties;
	}

	@Override
	public void terminate() {
		logger.debug("Mailer Service is being terminated.");
		if (this.status == ManagedTask.KILLED) {
			logger.debug("Mailer Service already terminated");
			return;
		}
		try {
			// loop through all the handlers
			for (int i = 0; i < this.numHandlers; i++) {
				logger.debug("Stopping handler: ", (i+1));
				// if the mailer is busy and the job state is currently running...
				if (MailerHandlerStatus.HANDLER_BUSY.equals(this.mailers[i].getCurrentStatus()) && 
						(JobStatus.RUNNING.equals(this.currentJobs[i].getJobState()))) {
					// then we need to update the job the mailer has as interrupted
					if (this.mailers[i].getCurrentJob() != null && this.mailers[i].getCurrentJob().getJobState() != JobStatus.FINISHEDOK) {
						this.mailers[i].getCurrentJob().setJobState(JobStatus.INTERRUPTED);
						try {
							MailerDAO.updateJob(this.mailers[i].getCurrentJob(), SqlTools.getConnection());
						} catch (ServiceException e) {
							logger.error("Error updating record: ", this.mailers[i].getCurrentJob(), e);
						}
					}
				}
				this.mailers[i].stopMailer();
				this.threads[i].interrupt();
			}
		} catch (NullPointerException e) {
			logger.info("Error shutting down: ", e);
		}
		this.status = ManagedTask.KILLED;
	}

	@Override
	public void start()
			throws TaskException {
		logger.debug("Starting ", this.getName());
		if (this.status == ManagedTask.NOT_STARTED) {
			this.initialize();
		}
		
		// SMTP server configuration
		this.status = ManagedTask.STARTED;
		this.properties = System.getProperties();
		this.mailServer = Environment.getInstance().getProperty("smtp.server");
		this.properties.put("mail.smtp.host", mailServer);
	}
	
	/**
	 * Initialises this instance
	 * @throws TaskException 
	 */
	private void initialize()
			throws TaskException {
		logger.debug("Initializing the MailerService");
		
		// Get the handler class property
		this.handlerClass = this.properties.getProperty("handler");
		if (this.handlerClass == null) {
			this.handlerClass = "com.snapon.sbs.dns.mailer.MailerHandler";
		}
		
		// Get the number of handlers we need
		String num = this.properties.getProperty("handlerCount");
		if ((num != null) && (num.length() > 0)) {
			this.numHandlers = Integer.parseInt(num);
		}
		
		try {
			// loads the handler class into a class object and checks it derives from JobHandler
			Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(this.handlerClass);
			this.checkHandlerClass(cls);
			
			// Initialise the arrays - the elements at this point are all null
			this.currentJobs = new EmailJob[this.numHandlers];
			this.mailers = new MailerHandler[this.numHandlers];
			this.threads = new Thread[this.numHandlers];
			
			// loop through the arrays and initialise them (not the jobs)
			logger.debug("Handlers count: " + this.numHandlers);
			for (int count = 0; count < this.numHandlers; count++) {
				logger.debug("Starting Handler " + (count+1));
				
				// first the mailers
				this.mailers[count] = (MailerHandler) cls.newInstance();
				this.mailers[count].initHandler();
				
				// then the threads
				this.threads[count] = new Thread(this.mailers[count], "MailerHandler " + count);
				this.threads[count].start();
			}
		} catch (ClassNotFoundException e) {
			logger.error("EXCEPTION", e);
			throw new TaskException("Failed to Start: ", e);
		} catch (ServiceException e) {
			logger.error("EXCEPTION", e);
			throw new TaskException("Failed to Start: ", e);
		} catch (InstantiationException e) {
			logger.error("EXCEPTION", e);
			throw new TaskException("Failed to Start: ", e);
		} catch (IllegalAccessException e) {
			logger.error("EXCEPTION", e);
			throw new TaskException("Failed to Start: ", e);
		}
	}
	
	/**
	 * Checks whether a Class is a subclass of the JobHandler class.
	 *
	 * @param cls The class to be checked.
	 * @throws ServiceException If the Handler class is invalid.
	 */
	private void checkHandlerClass(Class<?> cls) throws ServiceException {
		logger.debug("Checking ancestors of class ", cls.getName());
		Class<?> superClass = cls;
		while (!superClass.getName().equals(MailerService.handlerBaseClass)) {
			logger.debug("SuperClass =  ", superClass.getName());
			superClass = superClass.getSuperclass();
			logger.debug("Next superClass =  ", (superClass == null ? "null" : superClass.getName()));
			if (superClass == null) {
				logger.error("Handler class ", cls.getName(), " is not a subclass of ", MailerService.handlerBaseClass);
				throw new ServiceException("Handler class " + cls.getName() + " is not a subclass of " + MailerService.handlerBaseClass);
			}
		}
	}
	
	/**
	 * The main loop, switching through the states using a state machine.
	 * @throws ServiceException if anything odd occurs
	 * @throws TaskException if an error occurs with termination
	 * @throws SQLException if an error occurs with the database
	 * @throws InvalidStateException if the state of the mailer goes awry
	 */
	private void processingLoop()
			throws TaskException, SQLException, InvalidStateException, ServiceException {
		int handlerBusyCount = 0;
		while (this.keepRunning) {
			if ((this.status == ManagedTask.STARTED) || (this.status == ManagedTask.INITIALIZED)) {
				// if the thread was interrupted somehow
				if (Thread.interrupted()) {
					this.terminate();
				}
				// Cycle through all the current mailer instances
				for (int i = 0; i < this.numHandlers; i++) {
					switch (this.mailers[i].getCurrentStatus()) {
					// if the handler is not initialised we need to set some variables
					case HANDLER_UNINITALIZED:
						this.mailers[i].initHandler();
						break;
					case HANDLER_READY:
						// find a job that has not yet started
						if (this.currentJobs[i] == null) {
							try {
								EmailJob nextJob = this.lookForNewJob(JobStatus.NOTSTARTED);
								if (nextJob != null) {
									this.currentJobs[i] = nextJob;
									// find a handler that is ready to accept a job for processing
									for (int num = 0; num < this.numHandlers; num++) {
										if (this.mailers[num].accept(nextJob)) {
											this.mailers[num].processJob(nextJob);
											break;	// we break here so we dont give another handler the same job
										}
									}
								} else {
									logger.debug("No jobs waiting for processing.");
								}
							} catch (ServiceException se) {
								logger.error("Error looking for new jobs: ", se.getMessage());
							} catch (IOException e) {
								logger.error("Error in processJob closing the socket: ", e);
							}
						}
						break;
					case HANDLER_BUSY:
						// if the current state of the handler is busy increment the busy count
						handlerBusyCount++;
						break;
					case HANDLER_FINISHED:
						this.mailers[i].cleanup();
						if (this.currentJobs[i] != null) {
							this.currentJobs[i] = null;
						}
						handlerBusyCount--;
						break;
					case HANDLER_KILLED:
						break;
					default:
						break;
					}
				}
				if ((handlerBusyCount == 0) && (this.status == ManagedTask.KILLED)) {
					logger.info("Service stopping permanently - all handlers have finished");
					for (int c = 0; c < this.numHandlers; c++) {
						this.mailers[c].stopMailer();
						this.threads[c].interrupt();
					}
					this.stop();
				}
				try {
					logger.trace("Sleeping for " + MailerService.DELAY);
					Thread.sleep(MailerService.DELAY);
				} catch (InterruptedException ie) {
					this.stop();
				}
			}
		}
		this.cleanUp();
	}

	/**
	 * A method that checks for jobs that have not yet started.
	 * @param status the email status to look for
	 * @return Either a new job from the database or null
	 * @throws InvalidStateException if there is an error with the states
	 * @throws ServiceException if anything else should go wrong
	 */
	private EmailJob lookForNewJob(JobStatus status)
			throws InvalidStateException, ServiceException {
		EmailJob currentJob = null;
		try {
			currentJob = MailerDAO.selectJobByState(status);
		} catch (ServiceException e) {
			logger.error("Exception in getting job from database: ", e);
		}
		return currentJob;
	}
	
	/**
	 * Clean up any resources before the handler thread exits
	 */
	public void cleanUp() {
		// Do I require any resources to be freed?
		logger.debug("thread has finished, freeing resources");
		this.currentJobs = null;
		this.mailers = null;
		this.threads = null;
	}
	
	@Override
	public void stop() throws TaskException {
		logger.debug("Loop is stopping");
		this.keepRunning = false;
	}
	
	@Override
	public Properties getProperties() {
		return this.properties;
	}

	@Override
	public String getName() {
		return "MailerService";
	}

	@Override
	public int getStatus() {
		return this.status;
	}

	@Override
	public String getSummary() {
		return "MailerService is in state " + this.status;
	}
	
	/**
	 * @return if the main loop is still running
	 */
	public boolean getLoopIsRunning() {
		return this.keepRunning;
	}
	
	/**
	 * @return the array of current jobs
	 */
	public EmailJob[] getCurrentJobs() {
		EmailJob[] jobsCopy = this.currentJobs;
		return jobsCopy;
	}
	
	/**
	 * @return the array of mailer handlers
	 */
	public MailerHandler[] getCurrentMailers() {
		MailerHandler[] handlersCopy = this.mailers;
		return handlersCopy;
	}
	
	/**
	 * @return the array of current threads
	 */
	public Thread[] getCurrentThreads() {
		Thread[] threadsCopy = this.threads;
		return threadsCopy;
	}
}

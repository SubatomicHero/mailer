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

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.lang.StringUtils;

import com.alisonassociates.npd.framework.base.Environment;
import com.alisonassociates.npd.framework.schedule.Task;
import com.alisonassociates.npd.framework.schedule.TaskDAO;
import com.alisonassociates.npd.framework.schedule.TaskScheduler;
import com.alisonassociates.npd.framework.util.LogUtil;
import com.alisonassociates.npd.framework.util.NxdLog;
import com.alisonassociates.npd.framework.util.StringTokenizer;

/**
 * The AutostartServlet provides a mechanism for starting arbitrary threads when the webapp is started. It assumes that this servlet is
 * configured to autostart in web.xml. When this servlet is initialized, it looks for an environment property schedule.types, which should
 * be a comma-separated list of task types. For each type found, it will query the SCHEDULE table in the database for tasks of that type. For
 * each task found it will attempt to create an instance of the class specified in the param-value, create a new thread, and call the run()
 * method of the task.
 */
public class AutostartServlet extends HttpServlet {


	private static final long serialVersionUID = -3137681010288696821L;

	private static final String SCHEDULE_TYPES_PROPERTY = "schedule.types";
	
	private static NxdLog logger = LogUtil.make();
	
	public void init() {
		Environment env = Environment.getInstance();
		env.setWebapp(this.getServletContext().getContextPath());
		String types = null;
		try {
			types = (String) env.getInitialContext().lookup(SCHEDULE_TYPES_PROPERTY);
		} catch (NamingException e) {
			logger.trace("init: NamingException - ", e);
		}
		
		if (StringUtils.isBlank(types)) {
			types = Environment.getInstance().getProperty(SCHEDULE_TYPES_PROPERTY);
		}
		
		if (StringUtils.isBlank(types)) {
			logger.error("Environment property " + SCHEDULE_TYPES_PROPERTY + " not found");
		}
		
		logger.debug("Scheduling tasks of types: " + types);
		TaskScheduler scheduler = TaskScheduler.getInstance();
		StringTokenizer toke = new StringTokenizer(types, ",");
		List<Task> allTasks = new ArrayList<Task>();
		while (toke.hasMoreTokens()) {
			String type = toke.nextToken().trim();
			List<Task> tasks = TaskDAO.getInstance().getTasks(type);
			logger.debug("Read tasks of type " + type + ". " + tasks.size() + " found");
			allTasks.addAll(tasks);
		}
		if (allTasks.size() > 0) {
			scheduler.init(allTasks);
		} else {
			logger.debug("No tasks found.");
		}
	}
}

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alisonassociates.npd.framework.util.FileTools;

/**
 * This class outputs an image file which is embedded in every email
 * and used for tracking when the user opened the email.
 * @author Martin Whittington
 */
public class TrackingServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		// update the database as we have pulled the image
		String id = request.getParameter("id");
		long uuid = Long.parseLong(id);
		MailerDAO.updateEmailRecipient(uuid);
		FileInputStream in = null;
		OutputStream out = null;
		try {
			// Setup the filename and mime type
			ServletContext context = getServletContext();
			String filename = context.getRealPath("/Images/tracking.png");
			String mime = context.getMimeType(filename);
			if (mime == null) {
				// return an early response if we cant get a mime type
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			
			// configure the response and create the file
			response.setContentType(mime);
			File file = new File(filename);
			response.setContentLength((int)file.length());
			in = new FileInputStream(file);
			out = response.getOutputStream();
			
			// copy the image to the output stream
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = in.read(buffer)) >= 0) {
				out.write(buffer, 0, count);
			}
		} catch (IOException e) {
			throw new IOException("File could not be loaded: ", e);
		} finally {
			// finally close the streams
			FileTools.close(in);
			FileTools.close(out);
		}
	}
}

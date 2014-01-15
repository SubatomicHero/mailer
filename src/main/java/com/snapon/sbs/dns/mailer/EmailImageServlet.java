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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.sql.Blob;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alisonassociates.npd.framework.util.FileTools;

/**
 * This servlet will be called to pull images from the database dependant
 * on the tag that is contained in the email.
 * @author Martin Whittington
 */
public class EmailImageServlet extends HttpServlet {

	private static final long serialVersionUID = 7348492979329460480L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		
		// Get the parameters from the url
		String tagName= request.getParameter("name");
		String pathName = request.getRequestURL().toString() + request.getQueryString();
		
		// create a connection and get an image based on the tagname
		Blob image = MailerDAO.getEmailImage(tagName, pathName);
		InputStream in = null;
		OutputStream out = null;
		try {
			in = image.getBinaryStream();
			String mime = URLConnection.guessContentTypeFromStream(in);
			if (mime == null) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			}
			
			// set the contenttype and length
			response.setContentType(mime);
			response.setContentLength((int) image.length());
			out = response.getOutputStream();
			
			// display the image via the output stream
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = in.read(buffer)) >= 0) {
				out.write(buffer, 0, count);
			}			
		} catch (Exception e) {
			throw new IOException("File could not be loaded: ", e);
		} finally {
			// close the streams and the connection
			FileTools.close(in);
			FileTools.close(out);
			try {
				image.free();
			} catch (SQLException e) {
				throw new ServletException("Could not free blob: ", e);
			}
		}
	}
}

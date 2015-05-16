/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmppserver;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/GCMNotification")
public class GCMNotification extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Put your Google API Server Key here
	private static final String GOOGLE_SERVER_KEY = "AIzaSyBrdsbpPVX8N6cv0jZ0UOaGw0VM4jTUG68";

	// Put your Google Project number here
	final String GOOGLE_USERNAME = "156429511339" + "@gcm.googleapis.com";

	public GCMNotification() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {
			String userMessage = request.getParameter("message");
                        /*
			Set<String> regIdSet = RegIdManager.readFromFile();
			String toDeviceRegId = (String) (regIdSet.toArray())[0];
                        */
                        String toDeviceRegId = request.getParameter("regid");
			SmackCcsClient.sendMessage(GOOGLE_USERNAME, GOOGLE_SERVER_KEY,
                                                   toDeviceRegId, userMessage);
			request.setAttribute("pushStatus", "Mensaje envíado");
		} 
                /*
                catch (IOException ioe) {
			ioe.printStackTrace();
			request.setAttribute("pushStatus",
					"RegId required: " + ioe.toString());
		}
                */
                catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("pushStatus", e.toString());
		}
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}

}

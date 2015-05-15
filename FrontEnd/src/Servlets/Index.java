package Servlets;

import javax.servlet.RequestDispatcher;
import java.io.IOException;

/**
 * Created by Jaime on 15/05/2015.
 *
 *
 */
public class Index extends javax.servlet.http.HttpServlet {
	protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

	}

	protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
		RequestDispatcher rd = request.getRequestDispatcher("index.html");


		rd.forward(request, response);
	}
}

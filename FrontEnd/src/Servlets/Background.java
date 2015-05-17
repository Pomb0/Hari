package Servlets;

import Voter.Results.VoterResult;
import Voter.Voter;
import org.json.simple.JSONArray;
import server.ServiceIdentifier;
import server.ServiceManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Jaime on 16/05/2015.
 * Takes care of the background insulin dosage
 */
public class Background extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		boolean valid = false;
		int weight = 0;
		Voter voter;
		VoterResult voterResult;
		JSONArray jsonArray = new JSONArray();
		String jsonString = "";

		//Parameter parsing and validation
		Map<String, String[]> parameters = request.getParameterMap();
		if(parameters.containsKey("weight")){
			try {
				weight = Integer.parseInt(request.getParameter("weight"));
				if(weight>=60 && weight<=120) valid = true;
			}catch (NumberFormatException exception){ valid = false; }
		}

		//Voter instantiation and usage. May reinforce the time restrictions at this level :o
		if(valid){
			ServiceManager.loadServices(this.getServletContext().getRealPath("properties.xml"));

			ServiceIdentifier[] list = ServiceManager.getServiceList();
			voter = new Voter(list, 3, 2500);
			voterResult = voter.backgroundInsulinDose(weight);
			jsonArray.add(voterResult.getJSON());
			jsonString = jsonArray.toJSONString();
		}

		//Handle response, if necessary JSON conversion.

		System.out.println(jsonString);
		response.setContentType("application/json");
		response.getWriter().write(jsonString);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}
}

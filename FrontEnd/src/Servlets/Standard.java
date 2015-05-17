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

public class Standard extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		boolean valid = false;
		/*
			chgrams: 60 - 150
			chperunit: 10 - 15
			bloodsugar: 120 - 250
			targetbloodsugar: 80 - 120
			sensitivity: 15 - 100
		 */

		int chgrams = 0;
		int chperunit = 0;
		int bloodsugar = 0;
		int targetbloodsugar = 0;
		int sensitivity = 0;
		Voter voter;
		VoterResult voterResult;
		JSONArray jsonArray = new JSONArray();
		String jsonString = "";

		//Parameter parsing and validation
		Map<String, String[]> parameters = request.getParameterMap();
		if(
				parameters.containsKey("chgrams") &&
				parameters.containsKey("chperunit") &&
				parameters.containsKey("bloodsugar") &&
				parameters.containsKey("targetbloodsugar") &&
				parameters.containsKey("sensitivity")
		){
			try {
				chgrams = Integer.parseInt(request.getParameter("chgrams"));
				chperunit = Integer.parseInt(request.getParameter("chperunit"));
				bloodsugar = Integer.parseInt(request.getParameter("bloodsugar"));
				targetbloodsugar = Integer.parseInt(request.getParameter("targetbloodsugar"));
				sensitivity = Integer.parseInt(request.getParameter("sensitivity"));

				if(
						chgrams>=60 && chgrams<=150  &&
						chperunit>=10 && chperunit<=15  &&
						bloodsugar>=120 && bloodsugar<=250  &&
						targetbloodsugar>=80 && targetbloodsugar<=120  &&
						sensitivity>=15 && sensitivity<=100
				){
					valid = true;
				}
			}catch (NumberFormatException exception){ valid = false; }
		}

		//Voter instantiation and usage. May reinforce the time restrictions at this level :o
		if(valid){
			ServiceManager.loadServices(this.getServletContext().getRealPath("properties.xml"));
			ServiceIdentifier[] list = ServiceManager.getServiceList();
			voter = new Voter(list, 3, 2500);
			voterResult = voter.mealtimeInsulinDose(chgrams, chperunit, bloodsugar, targetbloodsugar, sensitivity);
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

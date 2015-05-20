package Servlets;

import Voter.Results.VoterResult;
import Voter.VoterClean.VoterClean;
import org.json.simple.JSONArray;
import server.ServiceIdentifier;
import server.ServiceManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class Personal extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		boolean valid = false;

		int chgrams = 0;
		int chperunit = 0;
		int bloodsugar = 0;
		int targetbloodsugar = 0;
		int todaypalevel = 0;
		int sensitivity = 0;
		int[] palevel = new int[0];
		int[] bsdrop = new int[0];

		VoterClean voter;
		VoterResult voterResult;
		VoterResult voterResultSensiativity;
		JSONArray jsonArray = new JSONArray();
		String jsonString = "";

		//Parameter parsing and validation
		Map<String, String[]> parameters = request.getParameterMap();
		if(

				parameters.containsKey("chgrams") &&
				parameters.containsKey("chperunit") &&
				parameters.containsKey("bloodsugar") &&
				parameters.containsKey("targetbloodsugar") &&
				parameters.containsKey("todaypalevel") &&
				parameters.containsKey("palevel") &&
				parameters.containsKey("bsdrop")
		){
			try {
				chgrams = Integer.parseInt(request.getParameter("chgrams"));
				chperunit = Integer.parseInt(request.getParameter("chperunit"));
				bloodsugar = Integer.parseInt(request.getParameter("bloodsugar"));
				targetbloodsugar = Integer.parseInt(request.getParameter("targetbloodsugar"));
				todaypalevel = Integer.parseInt(request.getParameter("todaypalevel"));
				palevel = stringToIntArray(request.getParameterValues("palevel"));
				bsdrop = stringToIntArray(request.getParameterValues("bsdrop"));

				if(
						chgrams>=60 && chgrams<=150  &&
						chperunit>=10 && chperunit<=15  &&
						bloodsugar>=120 && bloodsugar<=250  &&
						targetbloodsugar>=80 && targetbloodsugar<=120  &&
						todaypalevel>=0 && todaypalevel<=10  &&
						palevel.length > 1 && palevel.length <= 10 &&
						bsdrop.length > 1 && bsdrop.length <= 10 &&
						intArrayInRange(palevel, 0, 10) &&
						intArrayInRange(bsdrop, 15, 100)

				){
					valid = true;
				}
			}catch (NumberFormatException exception){ valid = false; }
		}

		//Voter instantiation and usage. May reinforce the time restrictions at this level :o
		if(valid){
			ServiceManager.loadServices(this.getServletContext().getRealPath("properties.xml"));
			ServiceIdentifier[] list = ServiceManager.getServiceList();
			voter = new VoterClean(list, 3, 3500l);
			voterResultSensiativity = voter.personalSensitivityToInsulin(todaypalevel, palevel, bsdrop);
			jsonArray.add(voterResultSensiativity.getJSON());
			if(voterResultSensiativity.isSuccessful()){
				sensitivity = voterResultSensiativity.getResult();
				voterResult = voter.mealtimeInsulinDose(chgrams, chperunit, bloodsugar, targetbloodsugar, sensitivity);
				jsonArray.add(voterResult.getJSON());
			}
			jsonString = jsonArray.toJSONString();
		}
		//Handle response, if necessary JSON conversion.

		System.out.println(jsonString);
		response.setContentType("application/json");
		response.getWriter().write(jsonString);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	private int[] stringToIntArray(String[] strArray){
		int size = strArray.length;
		int[] array = new int[size];
		for(int i=0; i<size; i++){
			try {
				array[i] = Integer.parseInt(strArray[i]);
			}catch (NumberFormatException exception){
				array[i] = -1;
			}
		}
		return array;
	}

	private boolean intArrayInRange(int[] array, int min, int max){
		int size = array.length;
		for (int anArray : array) if (anArray < min || anArray > max) return false;
		return true;
	}
}

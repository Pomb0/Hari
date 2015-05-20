package Voter.VoterClean;

import Voter.Results.VoterResult;
import server.ServiceIdentifier;
import server.ServiceManager;

/**
 * Created by Jaime on 18/05/2015.
 *
 */
public class Testing {
	public static void main(String[] args){
		ServiceManager.loadServices("FrontEnd/web/properties.xml");
		ServiceIdentifier[] services = ServiceManager.getServiceList();
		VoterResult result;
		VoterClean voterClean = new VoterClean(services, 3, 3000l);
		//Voter.Voter voter = new Voter.Voter(services, 3, 3000l);

		//result = voterClean.backgroundInsulinDose(80);
		//"chgrams=60&chperunit=12&bloodsugar=120&targetbloodsugar=80&todaypalevel=1&palevel=1&bsdrop=15&palevel=1&bsdrop=15"


		result = voterClean.personalSensitivityToInsulin(1, new int[]{1, 2}, new int[]{15, 16});
		System.out.println(result.getJSON().toJSONString());

		result = voterClean.mealtimeInsulinDose(60, 10, 120, 80, result.getResult());

		System.out.println(result.getJSON().toJSONString());
		//voterClean = new VoterClean(services, 3, 3000l);
		//result = voterClean.backgroundInsulinDose(80);
		//System.out.println(result.getJSON().toJSONString());

		//result = voter.personalSensitivityToInsulin(5, new int[]{1, 1, 2, 3, 4}, new int[]{15, 16, 17, 18, 19});
		//System.out.println(result.getJSON().toJSONString());
	}
}

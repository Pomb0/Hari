package server;

import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Jaime on 17/05/2015.
 *
 *
 */
public class ServiceManager {
	static List<ServiceIdentifier> serviceList = new LinkedList<ServiceIdentifier>();

	public static InsulinDoseCalculator ServiceClientFactory(ServiceIdentifier identifier){
		InsulinDoseCalculator client = null;
		try {
			Service s = Service.create(identifier.getWsdlLocation(), identifier.getServiceName());
			client = s.getPort(InsulinDoseCalculator.class);
		}catch (WebServiceException exception){
			System.out.println(">>" + identifier.getWsdlLocation().toString() + ": NOT UP :D");
			client = null;
		}
		return client;
	}

	public static synchronized void loadServices(String filePath){
		if(serviceList.size()>0) return;
		XMLParser parser = new XMLParser(filePath);
		List<ServiceIdentifier> list = parser.getList();
		for(ServiceIdentifier i : list) serviceList.add(i);
	}

	public static ServiceIdentifier[] getServiceList(){
		return (ServiceIdentifier[]) serviceList.toArray(new ServiceIdentifier[serviceList.size()]);
	}

	public static int getServiceCount(){
		return serviceList.size();
	}

}

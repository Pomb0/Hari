package server;

import javax.xml.namespace.QName;
import java.net.URL;

/**
 * Created by Jaime on 17/05/2015.
 *
 */
public class ServiceIdentifier {
	private URL wsdlLocation;
	private QName serviceName;

	public URL getWsdlLocation() {
		return wsdlLocation;
	}

	public ServiceIdentifier setWsdlLocation(URL wsdlLocation) {
		this.wsdlLocation = wsdlLocation;
		return this;
	}

	public QName getServiceName() {
		return serviceName;
	}

	public ServiceIdentifier setServiceName(QName serviceName) {
		this.serviceName = serviceName;
		return this;
	}
}

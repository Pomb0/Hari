package server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

/**
 * Created by Jaime on 17/05/2015.
 */


public class XMLParser {

	private Document doc;
	private String fileName = "properties.xml";

	public XMLParser(String path){
		this.fileName = path;
		Constructor();
	}

	public XMLParser(){
		Constructor();
	}

	private void Constructor(){
		try {
			File file = new File(this.fileName);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			this.doc = db.parse(file);
			this.doc.getDocumentElement().normalize();
		} catch ( IOException e) {
			e.printStackTrace();
		} catch ( SAXException e) {
			e.printStackTrace();
		} catch ( ParserConfigurationException  e) {
			e.printStackTrace();
		}
	}

	public LinkedList<ServiceIdentifier> getList(){
		String namespace;
		String url;
		LinkedList <ServiceIdentifier> list = new LinkedList<ServiceIdentifier>();
		try {
			NodeList nodeLst = doc.getElementsByTagName("webService");

			for (int s = 0; s < nodeLst.getLength(); s++) {

				Node fstNode = nodeLst.item(s);

				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

					Element fstElmnt = (Element) fstNode;
					NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("namespace");
					Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
					NodeList name = fstNmElmnt.getChildNodes();
					namespace = name.item(0).getNodeValue().toString();



					NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("url");
					Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
					NodeList urltemp = lstNmElmnt.getChildNodes();
					url = urltemp.item(0).getNodeValue().toString();


					list.add(new ServiceIdentifier()
									.setWsdlLocation(new URL(url))
									.setServiceName(new QName("http://server/", namespace))
					);
				}

			}

			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}

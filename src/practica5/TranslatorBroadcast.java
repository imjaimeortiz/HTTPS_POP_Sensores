package practica5;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;

public class TranslatorBroadcast implements Translator {
	
	// Serializar
	// <ServidorId=46><Temperatura=21><Viento=34><Humedad(%)=43><PPM=8947><SO2(%)=72><O3(%)=8>
	public String javaToXML(Msg bMsg) {
		String xmlMessage = "<server>";
		xmlMessage = xmlMessage.concat("<id>" + ((BroadcastMessage) bMsg).getId() + "</id>\r\n");
		if (((BroadcastMessage) bMsg).getSensors().size() > 0)
			xmlMessage = xmlMessage.concat("<temperatura>" + ((BroadcastMessage) bMsg).getSensors().get(0) + "</temperatura>\r\n");
		if (((BroadcastMessage) bMsg).getSensors().size() > 1)
			xmlMessage = xmlMessage.concat("<viento>" + ((BroadcastMessage) bMsg).getSensors().get(1) + "</viento>\r\n");
		if (((BroadcastMessage) bMsg).getSensors().size() > 2)
			xmlMessage = xmlMessage.concat("<humedad>" + ((BroadcastMessage) bMsg).getSensors().get(2) + "</humedad>\r\n");	
		if (((BroadcastMessage) bMsg).getSensors().size() > 3)
			xmlMessage = xmlMessage.concat("<ppm>" + ((BroadcastMessage) bMsg).getSensors().get(3) + "</ppm>\r\n");
		if (((BroadcastMessage) bMsg).getSensors().size() > 4)
			xmlMessage = xmlMessage.concat("<so2>" + ((BroadcastMessage) bMsg).getSensors().get(4) + "</so2>\r\n");
		if (((BroadcastMessage) bMsg).getSensors().size() > 5)
			xmlMessage = xmlMessage.concat("<o3>" + ((BroadcastMessage) bMsg).getSensors().get(5) + "</o3>\r\n");
		xmlMessage = xmlMessage.concat("</server>");
		return xmlMessage;
	}
	
	// Deserializar
	public BroadcastMessage XMLtoJava(String string) {
		String[] array = {"id", "temperatura", "viento", "humedad", "ppm", "so2", "o3"};
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			StringBuilder xmlStringBuilder = new StringBuilder();
			xmlStringBuilder.append(string);
			ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
			Document doc = builder.parse(input);
			doc.getDocumentElement().normalize();
			
			// Del elemento con etiqueta server, sacamos los nodos hijos, que serán el id y los sensores y de ahí vamos almacenando el mapeo nombre-valor
			NodeList nList = doc.getElementsByTagName("server");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					NodeList sensores = nNode.getChildNodes();
					for (int i = 0; i < sensores.getLength()/2; i++) {
						NodeList list = doc.getElementsByTagName(array[i]);
						Element e = (Element) list.item(0);
						map.put(array[i], e.getTextContent());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (map.containsKey("o3"))
			return new BroadcastMessage(Integer.parseInt(map.get("id")), Integer.parseInt(map.get("temperatura")), Integer.parseInt(map.get("viento")), Integer.parseInt(map.get("humedad")), Integer.parseInt(map.get("ppm")), Integer.parseInt(map.get("so2")), Integer.parseInt(map.get("o3")));
		if (map.containsKey("so2"))
			return new BroadcastMessage(Integer.parseInt(map.get("id")), Integer.parseInt(map.get("temperatura")), Integer.parseInt(map.get("viento")), Integer.parseInt(map.get("humedad")), Integer.parseInt(map.get("ppm")), Integer.parseInt(map.get("so2")));
		if (map.containsKey("ppm"))
			return new BroadcastMessage(Integer.parseInt(map.get("id")), Integer.parseInt(map.get("temperatura")), Integer.parseInt(map.get("viento")), Integer.parseInt(map.get("humedad")), Integer.parseInt(map.get("ppm")));
		if (map.containsKey("humedad"))
			return new BroadcastMessage(Integer.parseInt(map.get("id")), Integer.parseInt(map.get("temperatura")), Integer.parseInt(map.get("viento")), Integer.parseInt(map.get("humedad")));
		if (map.containsKey("viento"))
			return new BroadcastMessage(Integer.parseInt(map.get("id")), Integer.parseInt(map.get("temperatura")), Integer.parseInt(map.get("viento")));
		else
			return new BroadcastMessage(Integer.parseInt(map.get("id")), Integer.parseInt(map.get("temperatura")));
	}
	
	// Serializar
	public String javaToJson(Msg msg) {
		Gson gson = new Gson();
		return gson.toJson(msg);
	}
	
	// Deserializar
	public BroadcastMessage JsonToJava(String string) {
		Gson gson = new Gson();
		return gson.fromJson(string, BroadcastMessage.class);
	}


}

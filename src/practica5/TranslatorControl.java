package practica5;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;

public class TranslatorControl implements Translator {

	// Serializar
		// <ServidorId=46><Temperatura=21><Viento=34><Humedad(%)=43><PPM=8947><SO2(%)=72><O3(%)=8>
		public String javaToXML(Msg msg) {
			String xmlMessage = "<client>";
			xmlMessage = xmlMessage.concat("<action>" + ((ControlMessage)msg).getAction() + "</action>\r\n");
			xmlMessage = xmlMessage.concat("</client>");
			return xmlMessage;
		}
		
		// Deserializar
		public ControlMessage XMLtoJava(String string) {		
			String action = new String();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				StringBuilder xmlStringBuilder = new StringBuilder();
				xmlStringBuilder.append(string);
				ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
				Document doc = builder.parse(input);
				doc.getDocumentElement().normalize();
				NodeList nList = doc.getElementsByTagName("action");
				Element e = (Element) nList.item(0);
				action = e.getTextContent();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new ControlMessage(action);
		}
		
		// Serializar
		public String javaToJson(Msg msg) {
			Gson gson = new Gson();
			return gson.toJson(msg);
		}
		
		// Deserializar
		public ControlMessage JsonToJava(String string) {
			Gson gson = new Gson();
			ControlMessage ctrlMsg = gson.fromJson(string, ControlMessage.class);
			return ctrlMsg;
		}
}

package practica5;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

public class SchemaValidation {

	public boolean validateBroadcastXML(String xmlMessage) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new File("./data/schemaBroadcast.xsd"));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlMessage)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: "+e.getMessage());
            return false;
        }
        return true;
	}
	
	public boolean validateControlXML(String xmlMessage) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new File("./data/schemaControl.xsd"));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlMessage)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: "+e.getMessage());
            return false;
        }
        return true;
	}
}

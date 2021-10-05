package practica5;

public interface Translator {
		// Serializar
		public String javaToXML(Msg msg);
		
		// Deserializar
		public Msg XMLtoJava(String string);
		
		// Serializar
		public String javaToJson(Msg msg);
		
		// Deserializar
		public Msg JsonToJava(String received);
}

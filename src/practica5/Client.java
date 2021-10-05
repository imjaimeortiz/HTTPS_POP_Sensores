package practica5;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Client {
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		Socket client = null;
		InputStream is;
		OutputStream os;
		HashMap<String, Integer> cookies;

		try {
			cookies = new HashMap<String, Integer>();
			Scanner	in = new Scanner(System.in);
			
			// Mientras no cerremos la consola
			while (true) {
				// Abrimos el socket
				client = new Socket("localhost", 9999);
				is = client.getInputStream();
				os = client.getOutputStream();
				// Pedimos el recurso
				System.out.println("Introduce el nombre del recurso : ");
				String url = in.nextLine();
				String request = "GET /" + url + " HTTP/1.1\r\n";
				// Concatenamos las cookies que se han tratado hasta el momento
				for (String c : cookies.keySet())
					request =  request.concat("Cookie: " + c + "=" + cookies.get(c) + "\r\n");
				// Replicamos información
				os.write(request.getBytes());
				System.out.println(request);
	        	// Recibimos datos (respuesta del servidor)
		        byte[] buffer = new byte[1000];
		        int leido = is.read(buffer);
		        String response = new String(buffer, 0, leido);
		        System.out.println(response);
		        // Procesar cookies
		        if (!cookies.containsKey("/" + url)) {
		        	String [] lines = response.split("\r\n");
			        for (String string : lines) {
			        	// Para cada cabecera Set-Cookie, la parte izquierda del igual será el nombre de la cookie y la derecha el valor
			        	if (string.contains("Set-Cookie: ")) {
			        		String [] sub = string.split("=");
			        		String [] sub2 = sub[0].split(" ");
			        		if (!cookies.containsKey(sub2[1]))
			        			cookies.put(sub2[1], new Integer(sub[1]));
			        	}
					}	
		        }
		        
		        is.close();
		        os.close();
		        client.close();
			}
		} catch (Exception e) {
			System.out.println("ERROR : " + e.getMessage() + ":" + e);
		}
	}
	

}

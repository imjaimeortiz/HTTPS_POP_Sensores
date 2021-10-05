package practica5;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.net.ssl.SSLSocket;

public class GestorPeticionHttps extends Thread {
	
	SSLSocket client;
	InformationService iS;
	
	public GestorPeticionHttps(SSLSocket client, InformationService iS) {
		this.client = client;
		this.iS = iS;
	}

	public void run() {
		InputStream is;
		OutputStream os;

		try {
			
			is = client.getInputStream();
	        os = client.getOutputStream();

	        // Recibir datos
	        byte[] buffer = new byte[1000];
	        int leido;
	        leido = is.read(buffer);
	        String request = new String(buffer, 0, leido);
			// Procesamiento
				// Procesar salida
			String response = this.getResponse();
			// Replicar datos
			byte[] bufferResponse = response.getBytes();
			os.write(bufferResponse);

			is.close();
			os.close();
			client.close();
		} catch (IOException e) {
			System.out.println("ERROR: " + e.getMessage() + ":" + e);
		}
	}

	private String getResponse() {
		// Concatenamos la respuesta
		String response = "HTTP/1.1 200 OK\r\n\r\n";
		response = response.concat("<html><head><title> SERVICIO DE INFORMACIÓN </title>");
		response = response.concat("</head><body>");
		response = response.concat("<h1>" + iS.getLastData() + "</h1>");
		response = response.concat("</body></html>");
		return response;
	}
} 

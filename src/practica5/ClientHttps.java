package practica5;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Scanner;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class ClientHttps {
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		SSLSocket client = null;
		InputStream is;
		OutputStream os;
		HashMap<String, Integer> cookies;

		try {
			cookies = new HashMap<String, Integer>();
			Scanner	in = new Scanner(System.in);
			
			// Mientras no cerremos la consola
			while (true) {
				client = getSocket(); 
				is = client.getInputStream();
				os = client.getOutputStream();
				// Pedimos el recurso
				System.out.println("Introduce el nombre del recurso : ");
				String url = in.nextLine();
				String request = "GET /" + url + " HTTP/1.1\r\n";
				// Concatenamos las cookies que se han tratado hasta el momento
				for (String c : cookies.keySet())
					request =  request.concat("Cookie: " + c + "=" + cookies.get(c) + "\r\n");
				// Replicamos informaci�n
				os.write(request.getBytes());
				System.out.println(request);
	        	// Recibimos datos (respuesta del servidor)
		        byte[] buffer = new byte[1000];
		        int leido = is.read(buffer);
		        String response = new String(buffer, 0, leido);
		        System.out.println(response);
		        		        
		        is.close();
		        os.close();
		        client.close();
			}
		} catch (Exception e) {
			System.out.println("ERROR : " + e.getMessage() + ":" + e);
		}
	}
	
	public static SSLSocket getSocket() throws Exception {
		final int HTTPS_PORT = 4430;
		String passwd = "keystore2";
		char[] keystorepwd = passwd.toCharArray();
		final String cliente = "./cert/cliente.ks";
		final String cacert = "./cert/cacert.pem";
		
		// Recuperamos keystore
		 KeyStore ks = KeyStore.getInstance ("PKCS12");
		 ks.load (new FileInputStream(cliente), keystorepwd);
		 KeyManagerFactory kmf = KeyManagerFactory.getInstance ("SunX509");
		 // Entramos al keystore con la contraseña
		 kmf.init (ks, keystorepwd);
		 
		 KeyStore ksTrust = KeyStore.getInstance("PKCS12");
		 ksTrust.load(null, null);
		 // Obtenemos el certificado de confianza de la CA
		 java.security.cert.Certificate myCert = CertificateFactory.getInstance("X509").generateCertificate(new FileInputStream(cacert));
		 ksTrust.setCertificateEntry("CA", myCert);
		 // Para indicar que confiamos en él
		 TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		 tmf.init(ksTrust);
		 
		 // Se va a utilizar TLS como protocolo seguro
		 SSLContext sslctx = SSLContext.getInstance("TLS");
		 // (KeyStore : fuentes de las claves, cacert : autoridad)
		 sslctx.init (kmf.getKeyManagers (), tmf.getTrustManagers(), null);
		 SSLSocketFactory sf = sslctx.getSocketFactory();
		 SSLSocket s = (SSLSocket) sf.createSocket ("localhost", HTTPS_PORT);
		 s.startHandshake();
		 return s;
	 }
	

}

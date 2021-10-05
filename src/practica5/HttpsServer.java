package practica5;

import java.io.*;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.util.*;

public class HttpsServer extends Thread {
	
	private InformationService iS;
	
	public HttpsServer(InformationService iS) {
		this.iS = iS;
	}
	
	public void run() {
		SSLServerSocket listen;
		
		try {
			listen = getServer();
			while (true) {
				SSLSocket client = (SSLSocket) listen.accept();
				GestorPeticionHttps c = new GestorPeticionHttps(client, iS);
				c.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static SSLServerSocket getServer() throws Exception {
		final int HTTPS_PORT = 4430;
		String passwd = "keystore";
		char[] keystorepwd = passwd.toCharArray();
		final String keystore = "./cert/servidor.ks";
		final String cacert = "./cert/cacert.pem";
		
		// Recuperamos keystore
		 KeyStore ks = KeyStore.getInstance ("PKCS12");
		 ks.load (new FileInputStream(keystore), keystorepwd);
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
		 SSLServerSocketFactory ssf = sslctx.getServerSocketFactory ();
		 SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket (HTTPS_PORT);
		 ss.setNeedClientAuth(true);
		 return ss;
	 }
}
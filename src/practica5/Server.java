package practica5;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Server extends Thread {

	static public final String	IP_DEFAULT = "192.168.100.1";
	static public final String	BCAST_DEFAULT = "192.168.100.255";
	static public final int PORT_DEFAULT = 6666;
	private DatagramSocket socket;
	private boolean running;
	private int delay;
	private int id;
	private int nSensores;
	private int port;
	private InetAddress broadcastAddress;
	private String format;
	private Translator trBroadcast;
	private Translator trControl;
	private BufferedWriter bw;
	private Msg broadcastMsg;
	private Msg controlMsg;
	private SchemaValidation schValidation;

	public Server(String format,File file) {
		this.format = format;
		this.id = (int)(Math.random()*90) + 10;
		this.delay = 1000;
		this.nSensores = (int)(Math.random()*6) + 1;
		this.port = (int)(Math.random()*60000) + 1024;
		this.trBroadcast = new TranslatorBroadcast();
		this.trControl = new TranslatorControl();
		this.schValidation = new SchemaValidation();
		try {
			this.bw = new BufferedWriter(new FileWriter(file));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			this.broadcastAddress = InetAddress.getByName(findBroadcastAddress());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			this.socket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		running = true;
		byte[] buf = new byte[256];
		// Se delega la funcionalidad del mensaje de BC a un hilo auxiliar para que el servidor pueda atender las peticiones unicast del cliente
		new ServerSender(this).start();
		while (running) {
			sendBroadcast();
			// Recepción paquete unicast
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Procesamiento en función de qué hemos recibido
			processReceived(new String(packet.getData(), 0, packet.getLength()));
		}
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socket.close();
	} 
	
	// Método de envío broadcast
	public void sendBroadcast() {
		String msg = new String();
		byte [] buf = new byte[256];
		this.getSensors(nSensores);
		if (format.equals("XML"))
			msg = trBroadcast.javaToXML(broadcastMsg);
		else if (format.equals("JSON"))
			msg = trBroadcast.javaToJson(broadcastMsg);
		try {
			bw.write(msg + "\n\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		buf = msg.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, broadcastAddress, PORT_DEFAULT);
		try {
			socket.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Procesamiento en función de qué operación se va a realizar
	private void processReceived (String received) {
		// Es XML
		if (received.contains("<")) {
			if (schValidation.validateControlXML(received)) {
				controlMsg = trControl.XMLtoJava(received);	
				controlMsg.showMessage("XML");
			}
		// Es JSON
		} else {
			controlMsg = trControl.JsonToJava(received);
			controlMsg.showMessage("JSON");
		}
		String action = ((ControlMessage)controlMsg).getAction();
		// La acción puede ser : parar servidor, cambiar frecuecia o cambiar formato envío
		if (action.equals("stop")) {
			running = false;
			socket.close();
		}
		else if (action.equals("XML") || action.equals("JSON"))
			format = action;
		else {
			try { // Esto lo hacemos porque introducimos la frecuencia en Hz
				delay = 1000000/Integer.parseInt(action);
			} catch (Exception e) {
				System.out.println("<Servidor>	<" + received + ">	Operación no soportada");
			}
		}
	}
	
	static protected String findBroadcastAddress () {
		int	count = 0;
		String ipBcastAddress = null;
		do {
			try {
				Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces ();
		        while (interfaces.hasMoreElements ()) {
		            NetworkInterface iface = interfaces.nextElement ();
		            // filters out 127.0.0.1 and inactive interfaces
		            if (iface.isLoopback () || !iface.isUp ())
		                continue;
	                for (InterfaceAddress address : iface.getInterfaceAddresses ()) {
	                	InetAddress bcast = address.getBroadcast ();
	                	if (bcast != null) {
	                		ipBcastAddress = bcast.getHostAddress ();
	                	}
	                }
		        }
		    } catch (SocketException e) { e.printStackTrace(); ipBcastAddress = IP_DEFAULT; }
		    
		    if (ipBcastAddress == null) {
		    	System.out.println ("--[UdpManager] No network interface ready yet. Waiting ... ");
		        try { Thread.sleep (5000); } catch (Exception e) { }
		        count ++;
		    }
		} while ((ipBcastAddress == null) && (count < 5));
	    if (ipBcastAddress == null)
	    	ipBcastAddress = BCAST_DEFAULT;
	    
	    return ipBcastAddress;
	}
	
	// Generación de los nSensores de cada servidor de forma aleatoria
	// Se crea un mensaje de broadcast, para cuando serialicemos podamos sacar bien los valores
	private String getSensors(int nSensores) {
		int temp = (int)(Math.random()*50);
		int wind = (int)((Math.random()*110) + 10);
		int h = (int)((Math.random()*70) + 20);
		int ppm = (int)(Math.random()*9000);
		int so2 = (int)(Math.random()*100);
		int o3 = (int)(Math.random()*100);
		
		String sensores = format + "<ServidorId=" + this.id + ">";
		if (nSensores == 6) {
			sensores = sensores.concat("<Temperatura=" + temp + ">");
			sensores = sensores.concat("<Viento=" + wind + ">");
			sensores = sensores.concat("<Humedad(%)=" + h + ">");
			sensores = sensores.concat("<PPM=" + ppm + ">");
			sensores = sensores.concat("<SO2(%)=" + so2 + ">");
			sensores = sensores.concat("<O3(%)=" + o3 + ">");
			broadcastMsg = new BroadcastMessage(id, temp, wind, h, ppm, so2, o3);
		} 
		else if (nSensores == 5) {
			sensores = sensores.concat("<Temperatura=" + temp + ">");
			sensores = sensores.concat("<Viento=" + wind + ">");
			sensores = sensores.concat("<Humedad(%)=" + h + ">");
			sensores = sensores.concat("<PPM=" + ppm + ">");
			sensores = sensores.concat("<SO2(%)=" + so2 + ">");
			broadcastMsg = new BroadcastMessage(id, temp, wind, h, ppm, so2);
		}
		else if (nSensores == 4) {
			sensores = sensores.concat("<Temperatura=" + temp + ">");
			sensores = sensores.concat("<Viento=" + wind + ">");
			sensores = sensores.concat("<Humedad(%)=" + h + ">");
			sensores = sensores.concat("<PPM=" + ppm + ">");
			broadcastMsg = new BroadcastMessage(id, temp, wind, h, ppm);
		}
		else if (nSensores == 3) {
			sensores = sensores.concat("<Temperatura=" + temp + ">");
			sensores = sensores.concat("<Viento=" + wind + ">");
			sensores = sensores.concat("<Humedad(%)=" + h + ">");		
			broadcastMsg = new BroadcastMessage(id, temp, wind, h);
		}
		else if (nSensores == 2) {
			sensores = sensores.concat("<Temperatura=" + temp + ">");
			sensores = sensores.concat("<Viento=" + wind + ">");					
			broadcastMsg = new BroadcastMessage(id, temp, wind);
		}
		else {
			sensores = sensores.concat("<Temperatura=" + temp + ">");
			broadcastMsg = new BroadcastMessage(id, temp);
		}
		return sensores;
	}
	
	public boolean isRunning() {
		return this.running;
	}
}

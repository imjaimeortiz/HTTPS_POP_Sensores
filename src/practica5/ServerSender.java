package practica5;

// Este hilo se utiliza para enviar los mensajes de broadcast sin que se bloquee el servidor
public class ServerSender extends Thread {

	private Server server;

	public ServerSender(Server server) {
		this.server = server;
	}
	
	@Override
	public void run() {
		while (server.isRunning())
			server.sendBroadcast();
	}
}

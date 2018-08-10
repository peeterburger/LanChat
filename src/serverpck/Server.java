package serverpck;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import util.Debugger;
import util.EncryptedString;
import util.Metadata;
import util.SerializableMessage;

/*
 * Der Server ist die Zentrale des LanChats. Hier werden alle Empfangenen Nachrichten weitergeleitet.
 */
public class Server {
	/*
	 * Der lokale ServerSocket des Servers
	 */
	private ServerSocket localServerSocket;
	/*
	 * Eine Liste mit allen verbundenen Clients
	 */
	private ArrayList<Socket> connectedClients = new ArrayList<>();

	/*
	 * Sendet ein Broadcast an alle verbundenen Clients.Diese Broadcasts sind in den
	 * meisten Fällen die Nachrichten von anderen Clients.
	 */
	private void sendBroadcastToClients(SerializableMessage messageToBroadcast) {
		Debugger.println(3, "SERVER", null, "Sending Broadcast");
		for (Socket socket : connectedClients) {
			try {
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(messageToBroadcast);
				Debugger.println(4, "SERVER", socket.getInetAddress().toString(), "Foreward last Message");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Wartet auf eine Nachricht vom jeweiligen Client. Wird eine Nachricht
	 * empfangen, wird sie an alle verbundenen Clients weitergeleitet.
	 */
	private SerializableMessage acceptClientMessage(Socket remoteClient) {
		try {
			ObjectInputStream ois = null;
			int sleepTimeInS = 2;
			do {
				try {
					ois = new ObjectInputStream(remoteClient.getInputStream());
				} catch (SocketException e) {
					Debugger.println(2, "CLIENT HANDLER", remoteClient.getInetAddress().toString(),
							"Connection Timed Out... Trying again in " + sleepTimeInS + " sec");
					sleepTimeInS *= 2;
					Thread.sleep(sleepTimeInS * 1000);
				}
			} while (ois == null);
			return (SerializableMessage) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Leitet empfangene Nachrichten an alle verbundenen Clients weitergeleitet.
	 */
	private void handleConnection(Socket remoteClient) {
		Debugger.println(2, "CLIENT HANDLER", remoteClient.getInetAddress().toString(),
				"handling connection from " + remoteClient.getInetAddress().toString());
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					SerializableMessage message = acceptClientMessage(remoteClient);
					Debugger.println(2, "CLIENT HANDLER", remoteClient.getInetAddress().toString(), "Message recived");
					if (message.getMetadata().getMessageType() == Metadata.TEXT) {
						EncryptedString text = (EncryptedString) message.getMessage();
						Debugger.println(3, "MESSAGE", remoteClient.getInetAddress().toString(),
								"Type: TEXT; Value: " + text.decrypt());
						sendBroadcastToClients(message);
					}
				}
			}
		}).start();
	}

	/*
	 * Startet den Server und wartet auf neue Verbindungen
	 */
	public void start() {
		Debugger.println(0, "SERVER", null, "Server started");
		Debugger.println(1, "ACCEPTION HANDLER", null, "Waiting for clients to accept");

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket acceptedClient = localServerSocket.accept();
						Debugger.println(1, "ACCEPTION HANDLER", null,
								"Client accepted: " + acceptedClient.getInetAddress());
						connectedClients.add(acceptedClient);
						handleConnection(acceptedClient);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	/*
	 * Konstruktor, um eine Server-Instanz zu erzeugen. Es muss ein ServerSocket
	 * übergeben werden. Der Server wird noch nicht gestartet.
	 */
	public Server(ServerSocket localServerSocket) {
		this.localServerSocket = localServerSocket;
	}

	public static void main(String[] args) throws IOException {
		Server server = new Server(new ServerSocket(8888));
		server.start();
	}
}
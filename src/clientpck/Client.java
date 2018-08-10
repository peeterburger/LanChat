package clientpck;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import clientevents.BroadcastListener;
import util.EncryptedString;
import util.SerializableMessage;

/*
 * Ein Client kann sich mit dem Server schicken und mit anderen Clients (über den Server) kommunizieren
 */
public class Client implements Observer{
	@Override
	public void update(Observable o, Object arg) {
		System.out.println("update");	
	}
	
	/*
	 * Der Socket des Clients, mit dem er mit dem Server kommuniziert
	 */
	private Socket localClientSocket;

	/*
	 * Der Client wartet auf eventuelle Broadcasts vom Server. Diese Broadcasts sind
	 * in den meisten Fällen die Nachrichten von anderen Clients.
	 */
	private void listenForServerBroadcast() {
		new BroadcastHandler(localClientSocket).start();
	}

	/*
	 * Sendet eine Nachricht an den Server, welcher sie an die anderen Clients
	 * weiterleitet. Eine Nachricht kann entweder ein Beliebiges Objekt sein, muss
	 * aber als SerializableMassage übergeben werden (um zusätzliche Metadaten und
	 * Informationen anzuhängen)
	 */
	public void sendMessage(SerializableMessage message) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(localClientSocket.getOutputStream());
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Konstruktor, um eine Client-Instanz zu erzeugen. Es muss ein Socket übergeben
	 * werden. Beim aufrufen des Konstruktors wird sofort auf Broadcasts von Server
	 * gewartet
	 */
	public Client(Socket localClientSocket) {
		BroadcastListener bcl = new BroadcastListener();
		bcl.addObserver(this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				bcl.listenBroadcast(localClientSocket);
			}
		}).start();

		this.localClientSocket = localClientSocket;
		this.listenForServerBroadcast();
	}

	/*
	 * Test-Klasse
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		Client client = new Client(new Socket("127.0.0.1", 8888));
		client.sendMessage(new SerializableMessage(new EncryptedString("lol")));
		Thread.sleep(5000);
	}

}
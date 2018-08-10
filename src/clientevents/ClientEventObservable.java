package clientevents;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Observable;

import clientpck.Client;
import util.Debugger;
import util.EncryptedString;
import util.SerializableMessage;

public class ClientEventObservable extends Observable{
	public void listenBroadcast(Socket socket) {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			int sleepTimeInS = 2;
			SerializableMessage message = null;
			do {
				try {
					message = (SerializableMessage) ois.readObject();
					setChanged();
					notifyObservers(message);
				} catch (SocketException e) {
					Debugger.println(0, "LOCAL", new Date().toString(),
							"Server unreachable. Trying again in " + sleepTimeInS + " sec");
					Thread.sleep(sleepTimeInS * 1000);
					sleepTimeInS *= 2;
				}
			} while (message == null);
			Debugger.println(0, "REMOTE", new Date().toString(), message.getMessage().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		ClientEventObserver observer = new ClientEventObserver();
		ClientEventObservable observable = new ClientEventObservable();
		Socket socket = new Socket("127.0.0.1", 8888);
		Client client = new Client(socket);
		observable.addObserver(observer);
		observable.listenBroadcast(socket);
		
		Thread.sleep(1000);
		client.sendMessage(new SerializableMessage(new EncryptedString("lol")));
	}
}
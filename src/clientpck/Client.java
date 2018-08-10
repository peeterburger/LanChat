package clientpck;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import util.EncryptedString;
import util.SeializableMessage;

public class Client {
	private Socket localClientSocket;

	private void listenForServerBroadcast() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				ObjectInputStream ois = null;
				try {
					ois = new ObjectInputStream(localClientSocket.getInputStream());
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				try {
					while (true) {
						SeializableMessage message = (SeializableMessage) ois.readObject();
						if (message != null) {
							System.out.println(message.getMessage());
						}
						Thread.sleep(10);
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void sendMessage(SeializableMessage message) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(localClientSocket.getOutputStream());
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Client(Socket localClientSocket) {
		this.localClientSocket = localClientSocket;
		this.listenForServerBroadcast();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		Client client = new Client(new Socket("127.0.0.1", 8888));
		client.sendMessage(new SeializableMessage(new EncryptedString("lol")));
		Thread.sleep(5000);
	}
}
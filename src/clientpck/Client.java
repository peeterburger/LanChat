package clientpck;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import util.EncryptedString;
import util.SeializableMessage;

public class Client {
	private Socket localClientSocket;
	
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
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Client client = new Client(new Socket("127.0.0.1", 8888));
		client.sendMessage(new SeializableMessage(new EncryptedString("lol")));
		Thread.sleep(5000);
	}
}
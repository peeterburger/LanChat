package clientpck;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

import util.Debugger;
import util.SerializableMessage;

public class BroadcastHandler extends Thread {
	private Socket socket;
	
	@Override
	public void run() {
		ObjectInputStream ois = null; // Es wird ein ObjectInputStream vom lokalen Socket erzeugt.
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
	
	public BroadcastHandler(Socket socket) {
		this.socket = socket;
	}
}

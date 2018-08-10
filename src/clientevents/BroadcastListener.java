package clientevents;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Observable;

import util.Debugger;
import util.SerializableMessage;

public class BroadcastListener extends Observable{
	public void listenBroadcast(Socket socket) {
		while(true) {
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
	}
}

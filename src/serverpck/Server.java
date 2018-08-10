package serverpck;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import util.EncryptedString;
import util.Metadata;
import util.SeializableMessage;

public class Server {
	private ServerSocket localServerSocket;
	
	public SeializableMessage acceptClientMessage(Socket remoteClient) {
		try {
			ObjectInputStream ois = null;
			int sleepTimeInS = 2;
			do {
				try {
					ois = new ObjectInputStream(remoteClient.getInputStream());
				}catch(SocketException e) {
					System.out.println("\t\t[CLIENT HANDLER: " + remoteClient.getInetAddress() + "] Connection Timed Out... Trying again in "  + sleepTimeInS + " sec");
					sleepTimeInS *= 2;
					Thread.sleep(sleepTimeInS * 1000);
				}
			}while(ois == null);
			
			while(true) {
				SeializableMessage message = (SeializableMessage) ois.readObject();
				if(message != null) {
					return message;
				}
				Thread.sleep(10);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void handleConnection(Socket remoteClient) {
		System.out.println("\t\t[CLIENT HANDLER: " + remoteClient.getInetAddress() + "] handling connection on " + remoteClient.getInetAddress());
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					SeializableMessage message = acceptClientMessage(remoteClient);
					System.out.println("\t\t[CLIENT HANDLER: " + remoteClient.getInetAddress() + "] Message recived");
					if(message.getMetadata().getMessageType() == Metadata.TEXT) {
						EncryptedString text = (EncryptedString)message.getMessage();
						System.out.println("\t\t\t[MESSAGE: " + remoteClient.getInetAddress() + "] Type: TEXT; Value: " + text.decrypt());
					}
				}
			}
		}).start();
	}
	
	public void start() {
		System.out.println("[SERVER Server started");
		System.out.println("\t[ACCEPTION HANDLER] Waiting for clients to accept");
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						Socket acceptedClient = localServerSocket.accept();
						System.out.println("\t[ACCEPTION HANDLER] Client accepted: " + acceptedClient.getInetAddress());
						handleConnection(acceptedClient);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public Server(ServerSocket localServerSocket) {
		this.localServerSocket = localServerSocket;
	}
	
	public static void main(String[] args) throws IOException {
		Server server = new Server(new ServerSocket(8888));
		server.start();
	}
}
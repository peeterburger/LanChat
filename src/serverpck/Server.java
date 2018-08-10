package serverpck;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import util.EncryptedString;
import util.Metadata;
import util.SeializableMessage;

public class Server {
	private ServerSocket localServerSocket;
	private ArrayList<Socket> connectedClients = new ArrayList<>();
	
	private void sendBroadcastToClients(SeializableMessage messageToBroadcast) {
		System.out.println("\t\t[SERVER] Sending Broadcast");
		for (Socket socket : connectedClients) {
			try {
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject(messageToBroadcast);
				System.out.print("\t\t\t[BROADCAST TO: " + socket.getInetAddress() + "] ");
				
				if(messageToBroadcast.getMetadata().getMessageType() == Metadata.TEXT) {
					EncryptedString text = (EncryptedString)messageToBroadcast.getMessage();
					System.out.println("Type: TEXT; Value: " + text.decrypt());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private SeializableMessage acceptClientMessage(Socket remoteClient) {
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
	
	private void handleConnection(Socket remoteClient) {
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
						sendBroadcastToClients(message);
					}
				}
			}
		}).start();
	}
	
	public void start() {
		System.out.println("[SERVER] Server started");
		System.out.println("\t[ACCEPTION HANDLER] Waiting for clients to accept");
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						Socket acceptedClient = localServerSocket.accept();
						System.out.println("\t[ACCEPTION HANDLER] Client accepted: " + acceptedClient.getInetAddress());
						connectedClients.add(acceptedClient);
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
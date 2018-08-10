package util;

import java.io.Serializable;

public class Metadata implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int TEXT = 1;
	public static final int FILE = 2;

	private int messageType;

	public int getMessageType() {
		return this.messageType;
	}

	public Metadata(Object message) {
		if (message.toString().equals("EncryptedString")) {
			this.messageType = TEXT;
		} else if (message.toString().equals("EncryptedFile")) {
			this.messageType = FILE;
		}
	}
}

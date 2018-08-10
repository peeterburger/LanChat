package util;

import java.io.Serializable;

public class EncryptedString implements Serializable {
	private static final long serialVersionUID = 1L;

	private String message;

	public String getDecryptedString() {
		return message;
	}

	public void setDecryptedString(String message) {
		this.message = message;
	}

	private void encrypt(String stringToDecrypt) {
		this.message = stringToDecrypt;
	}

	public String decrypt() {
		String decryptedString = this.message;
		return decryptedString;
	}

	@Override
	public String toString() {
		return "EncryptedString";
	}

	public EncryptedString(String message) {
		encrypt(message);
	}
}
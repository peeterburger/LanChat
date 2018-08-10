package util;

import java.io.Serializable;

/*
 * Ist eine Verschlüsselte Version von einem String
 */
public class EncryptedString implements Serializable {
	private static final long serialVersionUID = 1L;

	private String message;

	public String getDecryptedString() {
		return message;
	}

	public void setDecryptedString(String message) {
		this.message = message;
	}

	/*
	 * Verschlüsselt den String
	 */
	private void encrypt(String stringToDecrypt) {
		this.message = stringToDecrypt;
	}

	/*
	 * Entschlüsselt den String
	 */
	public String decrypt() {
		String decryptedString = this.message;
		return decryptedString;
	}

	/*
	 * Überschreibt die toString() Methode. Gerantiert bessere Sicherheit und wird zur Udentifizierung benötigt
	 */
	@Override
	public String toString() {
		return "EncryptedString";
	}

	public EncryptedString(String message) {
		encrypt(message);
	}
}
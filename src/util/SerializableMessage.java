package util;

import java.io.Serializable;

/*
 * Kombiniert eine Nachricht mit den dazugehörigen Metadaten
 */
public class SerializableMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	private Object message;
	private Metadata metadata;

	public Object getMessage() {
		return message;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	@Override
	public String toString() {
		return "SeializableMessage";
	}

	public SerializableMessage(Object message) {
		this.message = message;
		this.metadata = new Metadata(message);
	}
}
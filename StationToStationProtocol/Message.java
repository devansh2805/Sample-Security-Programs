import java.io.Serializable;
import java.security.PublicKey;

public class Message implements Serializable {
	private static final long serialVersionUID = 7190035301100250470L;
	String normalString;
	byte[] encryptedString;
	PublicKey publicKey;

	public Message(String normalString, byte[] encryptedString, PublicKey publicKey) {
		this.normalString = normalString;
		this.encryptedString = encryptedString;
		this.publicKey = publicKey;
	}

}

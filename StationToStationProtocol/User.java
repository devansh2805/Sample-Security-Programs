import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class User {
	final static long P = 99991;
	final static long G = 5;
	final static String KEYPAIR_ALGORITHM = "DSA";
	final static String SIGNING_ALGORITHM = "SHA256withDSA";
	final static String ENCRYPTION_ALGORITHM = "Blowfish";

	String name;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private PublicKey receivedPublicKey;
	private String userId;
	private String receivedUserId;

	public User(String name) {
		this.name = name;
		generateKeys();
		setUserId(generateId());
	}

	private void generateKeys() {
		try {
			final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(User.KEYPAIR_ALGORITHM);
			keyPairGenerator.initialize(2048);
			final KeyPair keyPair = keyPairGenerator.generateKeyPair();
			setPrivateKey(keyPair.getPrivate());
			setPublicKey(keyPair.getPublic());
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			noSuchAlgorithmException.printStackTrace();
		}

	}

	private String generateId() {
		String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
		StringBuilder stringBuilder = new StringBuilder(10);
		for (int i = 0; i < 10; i++) {
			int index = (int) (alphaNumericString.length() * Math.random());
			stringBuilder.append(alphaNumericString.charAt(index));
		}
		return stringBuilder.toString();
	}

	public byte[] signMessage(String message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature signature = Signature.getInstance(User.SIGNING_ALGORITHM);
		signature.initSign(getPrivateKey());
		signature.update(message.getBytes());
		return signature.sign();
	}

	public boolean verifySignature(String message, byte[] signatureSent)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature signature = Signature.getInstance(User.SIGNING_ALGORITHM);
		signature.initVerify(getReceivedPublicKey());
		signature.update(message.getBytes());
		return signature.verify(signatureSent);
	}

	public byte[] encryptMessage(String key, byte[] message) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Key secretKeySpec = new SecretKeySpec(key.getBytes(), User.ENCRYPTION_ALGORITHM);
		Cipher cipher = Cipher.getInstance(User.ENCRYPTION_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		return cipher.doFinal(message);
	}

	public byte[] decryptMessage(String key, byte[] message) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Key secretKeySpec = new SecretKeySpec(key.getBytes(), User.ENCRYPTION_ALGORITHM);
		Cipher cipher = Cipher.getInstance(User.ENCRYPTION_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		return cipher.doFinal(message);
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public PublicKey getReceivedPublicKey() {
		return receivedPublicKey;
	}

	public void setReceivedPublicKey(PublicKey receivedPublicKey) {
		this.receivedPublicKey = receivedPublicKey;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getReceivedUserId() {
		return receivedUserId;
	}

	public void setReceivedUserId(String receivedUserId) {
		this.receivedUserId = receivedUserId;
	}
}

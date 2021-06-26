import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Connector {
	ObjectInputStream objectInputStream;
	ObjectOutputStream objectOutputStream;
	Message message;
	Scanner scanner;
	Socket connectorSocket;
	User connectorUser;
	protected long R1;
	protected long R2;
	protected long K;

	public Connector(String name) {
		objectInputStream = null;
		objectOutputStream = null;
		message = null;
		scanner = new Scanner(System.in);
		connectorSocket = null;
		connectorUser = new User(name);
		R1 = 0;
		R2 = 0;
		K = 0;
	}

	public void start() {

		try {
			connectorSocket = new Socket("127.0.0.1", 5236);
			objectOutputStream = new ObjectOutputStream(connectorSocket.getOutputStream());
			objectInputStream = new ObjectInputStream(connectorSocket.getInputStream());

			// Exchanging User Id's
			message = (Message) objectInputStream.readObject();
			connectorUser.setReceivedUserId(message.normalString);
			message = new Message(connectorUser.getUserId(), null, null);
			objectOutputStream.writeObject(message);

			// Receiving R1
			System.out.println("\033[0;35m");
			message = (Message) objectInputStream.readObject();
			System.out.println("Received R1 from " + connectorUser.getReceivedUserId());
			R1 = Long.parseLong(message.normalString);
			System.out.println("R1 = " + R1);
			System.out.println("\033[0m");

			// Computing R2, K and Sending R2, My Public Key, Encrypted(Signed(MyUserId, R1,
			// R2), K)
			System.out.print("Enter y: ");
			int y = scanner.nextInt();
			System.out.println("\033[0;33m");
			R2 = ((long) Math.pow(User.G, y)) % User.P;
			K = ((long) Math.pow(R1, y)) % User.P;
			System.out.println("Computing R2 = g^y mod p = " + R2);
			System.out.println("Computing K = R1^y mod p = " + K);
			System.out.println("Sending R2, My Public Key, Encrypted(Signed(MyUserId, R1, R2), K)");
			message = new Message(Long.toString(R2),
					connectorUser.encryptMessage(Long.toString(K),
							connectorUser.signMessage(connectorUser.getReceivedUserId() + " " + R1 + " " + R2)),
					connectorUser.getPublicKey());
			objectOutputStream.writeObject(message);
			System.out.println("\033[0m");

			System.out.println("\033[0;35m");
			message = (Message) objectInputStream.readObject();
			System.out.println("Received Public Key of " + connectorUser.getReceivedUserId()
					+ ", Encrypted(Signed(UserId, R1, R2), K)");
			System.out.println("Decrypting with Key K = " + K);
			System.out.println(
					"Verifying Signature with Public Key of User " + connectorUser.getReceivedUserId() + " ......");
			connectorUser.setReceivedPublicKey(message.publicKey);
			boolean verify = connectorUser.verifySignature(connectorUser.getUserId() + " " + R1 + " " + R2,
					connectorUser.decryptMessage(Long.toString(K), message.encryptedString));
			if (verify) {
				System.out.println("Signature Verified");
				System.out.println("\033[0m");
				System.out.println("\033[1;32m");
				System.out.println("All further encrypted message communication can be done with Key = " + K);
				System.out.println("\033[0m");
			} else {
				System.out.println("\033[0;31m");
				System.out.println("Signature Verification Failed");
				System.out.println("\033[0m");
			}
		} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException | SignatureException
				| ClassNotFoundException exception) {
			exception.printStackTrace();
		} finally {
			try {
				scanner.close();
				if (connectorSocket != null)
					connectorSocket.close();
				if (objectInputStream != null)
					objectInputStream.close();
				if (objectOutputStream != null)
					objectOutputStream.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
}

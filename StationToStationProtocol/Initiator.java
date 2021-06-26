import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Initiator {
	ObjectInputStream objectInputStream;
	ObjectOutputStream objectOutputStream;
	Message message;
	Scanner scanner;
	ServerSocket serverSocket;
	Socket initiatorSocket;
	User initiatorUser;
	protected long R1;
	protected long R2;
	protected long K;

	public Initiator(String name) {
		objectInputStream = null;
		objectOutputStream = null;
		message = null;
		scanner = new Scanner(System.in);
		serverSocket = null;
		initiatorSocket = null;
		initiatorUser = new User(name);
		R1 = 0;
		R2 = 0;
		K = 0;
	}

	public void start() {

		try {
			serverSocket = new ServerSocket(5236);
			initiatorSocket = serverSocket.accept();
			objectOutputStream = new ObjectOutputStream(initiatorSocket.getOutputStream());

			// Exchanging User Id's
			objectInputStream = new ObjectInputStream(initiatorSocket.getInputStream());
			message = new Message(initiatorUser.getUserId(), null, null);
			objectOutputStream.writeObject(message);
			message = (Message) objectInputStream.readObject();
			initiatorUser.setReceivedUserId(message.normalString);

			// Sending R1
			System.out.print("\nEnter x: ");
			int x = scanner.nextInt();
			R1 = ((long) Math.pow(User.G, x)) % User.P;
			System.out.println("\033[0;33m");
			System.out.println("Computing R1 = g^x mod p = " + R1);
			message = new Message(Long.toString(R1), null, null);
			objectOutputStream.writeObject(message);
			System.out.println("Sending R1 to " + initiatorUser.getReceivedUserId() + " ......");
			System.out.println("\033[0m");

			// Receiving R2, Public Key, Encrypted(Signed(UserId, R1, R2), K)
			message = (Message) objectInputStream.readObject();
			System.out.println("\033[0;35m");
			System.out.println("Received R2, Public Key of " + initiatorUser.getReceivedUserId()
					+ ", Encrypted(Signed(UserId, R1, R2), K)");
			R2 = Long.parseLong(message.normalString);
			System.out.println("R2 = " + R2);
			K = ((long) Math.pow(R2, x)) % User.P;
			System.out.println("Computing K = R2^x mod p = " + K);
			System.out.println("Decrypting with Key K = " + K);
			System.out.println(
					"Verifying Signature with Public Key of User " + initiatorUser.getReceivedUserId() + " ......");
			initiatorUser.setReceivedPublicKey(message.publicKey);
			boolean verify = initiatorUser.verifySignature(initiatorUser.getUserId() + " " + R1 + " " + R2,
					initiatorUser.decryptMessage(Long.toString(K), message.encryptedString));
			if (verify) {
				System.out.println("Signature Verified");
				System.out.println("\033[0m");

				// Sending Public Key, Encrypted(Signed(MyUserId, R1, R2), K)
				message = new Message("",
						initiatorUser.encryptMessage(Long.toString(K),
								initiatorUser.signMessage(initiatorUser.getReceivedUserId() + " " + R1 + " " + R2)),
						initiatorUser.getPublicKey());
				System.out.println("\033[0;33m");
				System.out.println("Sending My Public Key, Encrypted(Signed(MyUserId, R1, R2), K)");
				System.out.println("\033[0m");
				objectOutputStream.writeObject(message);
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
				if (serverSocket != null)
					serverSocket.close();
				if (initiatorSocket != null)
					initiatorSocket.close();
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

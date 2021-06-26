import java.io.Console;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Clients {

	public static void main(String[] args) {
		String loginString = "", responseFromServer = "", otp = ""; 
		Client client = new Client();
		Console console = System.console();
		client.connectToServer("127.0.0.1", 5000);
		loginString = getLoginCredentials();
		client.sendDataToServer(loginString);
		System.out.println("\033[0;35m" + "Sent Login Info to Server" + "\033[0m");
		responseFromServer = client.readDatafromSever();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}
		if(!client.getClosedStatus()) {
			System.out.println("\033[0;33m" + "OTP Challenge Received from Server: " + responseFromServer + "\033[0m");
			otp = console.readLine("\033[0;36m" + "Enter Response: " + "\033[0m");
			client.sendDataToServer(otp);
			responseFromServer = client.readDatafromSever();
			System.out.println(responseFromServer);
		} else {
			System.out.println(responseFromServer);
		}
		client.closeConnection();
	}
	
	public static String hashPassword(String passwordText) {
		MessageDigest messageDigest;
		byte[] password = new byte[32];
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			password = messageDigest.digest(passwordText.getBytes(StandardCharsets.UTF_8));
		} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			noSuchAlgorithmException.printStackTrace();
		}
		BigInteger number = new BigInteger(1, password);
		return number.toString(16);
	}
	
	public static String getLoginCredentials() {
		String username = "", password = "", hashedPassword = "";
		char[] passwordChars = null;
		Console console = System.console();
		username = console.readLine("Enter Username: ");
		passwordChars = console.readPassword("Enter Password: ");
		password = new String(passwordChars);
		hashedPassword = hashPassword(password);
		return username + " " + hashedPassword;
	}
}

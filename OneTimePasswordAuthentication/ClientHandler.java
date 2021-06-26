import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

public class ClientHandler extends Thread {
	private Socket connectedClientSocket;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	private String username;
	private String hashedPassword;
	private static HashMap<String, String> authHashMap;
	private String otp;
	private String challenge;
	private int port;

	public ClientHandler(Socket clientSocket, HashMap<String, String> authInfo) {
		connectedClientSocket = clientSocket;
		authHashMap = authInfo;
		username = "";
		hashedPassword = "";
		otp = "";
		challenge = "";
		port = clientSocket.getPort();
	}

	@Override
	public void run() {
		synchronized (this) {
			String loginInfo = "", passwordString = "", readOTP = "";
			String closingConnection = "Closing Connection for Connection ID = " + port;
			System.out.println("Server Connected To Client with Connection ID: " + port);
			loginInfo = this.readDataFromClient();
			System.out.println("Login Request from Connection ID " + port + ", Login Info: " + loginInfo);
			username = loginInfo.split("[ ]")[0];
			hashedPassword = loginInfo.split("[ ]")[1];
			boolean usernameCheck = authHashMap.containsKey(username);
			if (usernameCheck) {
				passwordString = authHashMap.get(username);
				if (passwordString.equals(hashedPassword)) {
					System.out.println("Username Password Pair Matched!!, Sending OTP Challenge......");
					this.OTPChallenge();
					this.sendDataToClient(challenge);
					System.out.println("OTP Challenge Sent to Connection ID = " + port);
					readOTP = this.readDataFromClient();
					System.out.println("OTP Received from Connection ID = " + port + " :" + readOTP);
					if (readOTP.equals(otp)) {
						this.sendDataToClient(
								"\033[1;32m" + "OTP Correct!! Login Successful for Connection ID = " + port + "\033[0m");
						System.out.println("\033[0;34m" + closingConnection + " (Login Request Satisfied)" + "\033[0m");
						this.closeConnection();
					} else {
						this.sendDataToClient("\033[0;31m" + "Incorrect OTP for Connection ID = " + port + "\033[0m");
						System.out.println("\033[0;31m" + closingConnection + " (Wrong OTP)" + "\033[0m");
						this.closeConnection();
					}
				} else {
					this.sendDataToClient("\033[0;31m" + "Wrong Password for username: " + username + "\033[0m");
					System.out.println("\033[0;31m" + closingConnection + " (Wrong Password)" + "\033[0m");
					this.closeConnection();
					return;
				}
			} else {
				this.sendDataToClient("\033[0;31m" + "Username " + username + " Does not Exist" + "\033[0m");
				System.out.println("\033[0;31m" + closingConnection + " (Wrong Username)" + "\033[0m");
				this.closeConnection();
				return;
			}
		}
	}

	public String readDataFromClient() {
		synchronized (this) {
			String readDataString = "";
			try {
				dataInputStream = new DataInputStream(connectedClientSocket.getInputStream());
				readDataString = dataInputStream.readUTF();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
			return readDataString;
		}
	}

	public void closeConnection() {
		synchronized (this) {
			try {
				dataInputStream.close();
				dataOutputStream.close();
				connectedClientSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public void sendDataToClient(String data) {
		synchronized (this) {
			try {
				dataOutputStream = new DataOutputStream(connectedClientSocket.getOutputStream());
				dataOutputStream.writeUTF(data);
				dataOutputStream.flush();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	public static boolean isPrime(int x) {
		for (int i = 2; i < x; i++) {
			if (x % i == 0) {
				return false;
			}
		}
		return true;
	}

	public void OTPChallenge() { 
		synchronized (this) {
			Random random = new Random();
			switch (username) {
				case "alice":
					// f(x) = sumOfDigits(x)
					String number = "" + random.nextInt(10000) + 100;
					int sum = 0;
					for (char element : number.toCharArray()) {
						sum += Character.getNumericValue(element);
					}
					challenge = number;
					otp = "" + sum;
					break;
				case "charlie":
					// f(x) = primeNumber(x)
					int primeNumberLocation = 1 + random.nextInt(10);
					int count = 2;
					for(int i=1;i<=primeNumberLocation;i++) {
						while(!isPrime(count)) {
							count++;
						}
						count++;
					}
					String location = Integer.toString(primeNumberLocation);
					challenge = "" + location;
					otp = "" + (count-1);
					break;
				case "alex":
					// f(x) = reverse(x)
					String x = "" + ((int)Math.pow(10, 4) + random.nextInt(9 * (int)Math.pow(10, 5)));
					challenge = x;
					StringBuilder stringBuilder = new StringBuilder(x);
					otp = stringBuilder.reverse().toString();
					break;
				case "bob":
					// f(x) = square(x)
					String rand = "" + random.nextInt(100);
					challenge = rand;
					otp = "" + (int)Math.pow(Integer.parseInt(rand), 2);
					break;
				default:
					break;
			}
		}
	}
}

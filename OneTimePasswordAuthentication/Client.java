import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Client {
	private Socket clientSocket;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	
	public Client() {
		clientSocket = null;
		dataInputStream = null;
		dataOutputStream = null;
	}
	
	public void connectToServer(String ipAddress, int portNumber) {
		try {
			clientSocket = new Socket(ipAddress, portNumber);
			dataInputStream = new DataInputStream(clientSocket.getInputStream());
			dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
			System.out.println("Client Connected to Server with Connection ID = " + clientSocket.getLocalPort());
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	public void closeConnection() {
		try {
			dataInputStream.close();
			dataOutputStream.close();
			clientSocket.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	public void sendDataToServer(String data) {
		try {
			dataOutputStream.writeUTF(data);
			dataOutputStream.flush();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	public String readDatafromSever() {
		String readDataString = "";
		try {
			readDataString = dataInputStream.readUTF();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		return readDataString;
	}
	
	public boolean getClosedStatus() {
		try {
			clientSocket.setSoTimeout(1000);
			dataInputStream.readUTF();
		} catch (EOFException eofException) {
			return true;
		} catch (SocketTimeoutException socketTimeoutException) {
			return false;
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		return false;
	}
}

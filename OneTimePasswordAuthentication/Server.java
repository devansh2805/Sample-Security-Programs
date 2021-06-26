import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

public class Server {
	static HashMap<String, String> authHashMap = new HashMap<String, String>();
	public static void main(String[] args) {
		authHashMap.put("alice", "a04399bc9666c1ee570e496673d1dec86722f71d10fe4247e17b4ec65681d3b7");
		authHashMap.put("charlie", "4998a79988a173c01af9b0dee8f62b4927b294b7ced31753f507b081389adaf2");
		authHashMap.put("alex", "4f282e0eeeb4a7f1bbdb2309566e822a6bef87794b9ae4c01934a6745c455b7e");
		authHashMap.put("bob", "c129710c857788d6ff616be35097e2745728725f0b0834d1ae6844f5dd91a309");
		try {
			ServerSocket serverSocket = new ServerSocket(5000);
			System.out.println("Server Started at 127.0.0.1:5000");
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						System.out.println("\033[0;37m" + " Closing Server" +  "\033[0m");
						serverSocket.close();
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				}
			});
			while(true) {
				Socket connectedClientSocket = serverSocket.accept();
				new ClientHandler(connectedClientSocket, authHashMap).start();
			}
		} catch (SocketException socketException) {
			
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
}

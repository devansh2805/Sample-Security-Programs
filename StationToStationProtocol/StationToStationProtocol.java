import java.util.Scanner;

public class StationToStationProtocol {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter you're Name: ");
		String name = scanner.next();
		switch (Integer.parseInt(args[0])) {
		case 1:
			Initiator initiator = new Initiator(name);
			System.out.println("User Id = " + initiator.initiatorUser.getUserId());
			initiator.start();
			break;
		case 2:
			Connector connector = new Connector(name);
			System.out.println("User Id = " + connector.connectorUser.getUserId());
			connector.start();
			break;
		}
		scanner.close();
	}
}

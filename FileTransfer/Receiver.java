import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Receiver extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	JFrame parentFrame;
	String ip;
	Container container;
	JLabel myIpLabel;
	JLabel ipLabel;
	JLabel textLabel;
	JTextField ipField;
	JButton connectButton;
	String zeroTo255;
	String regex;
	Pattern ipPattern;
	Socket receiverSocket;
	DataInputStream dataInputStream;
	DataOutputStream dataOutputStream;
	String currentWorkingDirectory;
	String operatingSystemName;
	File file;

	public Receiver(JFrame parentFrame, String ip) {
		this.parentFrame = parentFrame;
		this.ip = ip;
		container = this.getContentPane();
		ipLabel = new JLabel("Enter IP Address: ");
		myIpLabel = new JLabel("Your IP: " + this.ip);
		textLabel = new JLabel("Receivng Files......");
		connectButton = new JButton("Connect");
		ipField = new JTextField();
		zeroTo255 = "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])";
		regex = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
		ipPattern = Pattern.compile(regex);
		receiverSocket = null;
		dataInputStream = null;
		dataOutputStream = null;
		currentWorkingDirectory = System.getProperty("user.dir");
		operatingSystemName = System.getProperty("os.name");
		file = null;
		this.setLocationAndSize();
		this.addComponentsToContainer();
		connectButton.addActionListener(this);
		this.setTitle("Receiver");
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent windowEvent) {
				try {
					if (receiverSocket != null)
						receiverSocket.close();
					if (dataInputStream != null)
						dataInputStream.close();
					if (dataOutputStream != null)
						dataOutputStream.close();
					parentFrame.setVisible(true);
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		});
	}

	private void setLocationAndSize() {
		container.setLayout(null);
		this.setSize(300, 300);
		myIpLabel.setBounds(70, 20, 200, 20);
		ipLabel.setBounds(20, 70, 120, 20);
		ipField.setBounds(150, 65, 130, 30);
		connectButton.setBounds(100, 115, 100, 30);
	}

	private void addComponentsToContainer() {
		container.add(myIpLabel);
		container.add(ipLabel);
		container.add(ipField);
		container.add(connectButton);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource() == connectButton) {
			String ipToConnect = ipField.getText();
			if (ipToConnect.equals("") || ipToConnect == null) {
				JOptionPane.showMessageDialog(this, "Please Enter IP Address");
			} else {
				Matcher ipMatcher = ipPattern.matcher(ipToConnect);
				if (ipMatcher.matches()) {
					ConnectionHandler connectionHandler = new ConnectionHandler(this, ipToConnect);
					connectionHandler.start();
				} else {
					JOptionPane.showMessageDialog(this, "Wrong IP Format", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private void decryptFile() {
		String command = "openssl aes256 -d -a -k obQuubKgwmvX0CUjDVD1OkwsXVZgQUHa";
		command += " -in " + file.getAbsolutePath() + " -out ";
		String decryptedFilePath = "";
		if (operatingSystemName.equals("Linux")) {
			decryptedFilePath = currentWorkingDirectory + "/"
					+ file.getName().substring(0, file.getName().lastIndexOf("."));
		} else {
			decryptedFilePath = currentWorkingDirectory + "\\"
					+ file.getName().substring(0, file.getName().lastIndexOf("."));
		}
		command += decryptedFilePath;
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(command);
			DataInputStream disDataInputStream = new DataInputStream(process.getInputStream());
			byte[] str = disDataInputStream.readAllBytes();
			System.out.println(new String(str));
			file.delete();
		} catch (IOException ioException) {
		}
	}

	class ConnectionHandler extends Thread {
		JFrame frame;
		String ipToConnect;

		public ConnectionHandler(JFrame frame, String ipToConnect) {
			this.frame = frame;
			this.ipToConnect = ipToConnect;
		}

		@Override
		public void run() {
			try {
				receiverSocket = new Socket(ipToConnect, 5000);
				dataInputStream = new DataInputStream(receiverSocket.getInputStream());
				dataOutputStream = new DataOutputStream(receiverSocket.getOutputStream());
				container.remove(connectButton);
				container.remove(ipField);
				ipLabel.setText("Connected to " + receiverSocket.getInetAddress().getHostAddress());
				ipLabel.setBounds(50, 70, 200, 20);
				textLabel.setBounds(50, 120, 200, 20);
				container.add(textLabel);
				container.repaint();
				while (true) {
					receiveFile();
				}
			} catch (IOException ioException) {
			}
		}

		public void receiveFile() throws IOException {
			if (receiverSocket.isClosed()) {
				return;
			}
			String fileName = dataInputStream.readUTF();
			int fileLength = Integer.parseInt(dataInputStream.readUTF());
			if (operatingSystemName.equals("Linux")) {
				file = new File(currentWorkingDirectory + "/" + fileName);
			} else {
				file = new File(currentWorkingDirectory + "\\" + fileName);
			}
			file.createNewFile();
			byte[] encryptedFileBytes = new byte[fileLength];
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			int read = 0;
			int remaining = fileLength;
			@SuppressWarnings("unused")
			int totalRead = 0;
			while ((read = dataInputStream.read(encryptedFileBytes, 0,
					Math.min(encryptedFileBytes.length, remaining))) > 0) {
				totalRead += read;
				remaining -= read;
				fileOutputStream.write(encryptedFileBytes, 0, read);
			}
			fileOutputStream.close();
			decryptFile();
			JOptionPane.showMessageDialog(frame,
					"File " + fileName.substring(0, fileName.lastIndexOf('.')) + " Received");
		}
	}
}

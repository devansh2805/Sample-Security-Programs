import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Sender extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	JFrame parentFrame;
	String ip;
	ServerSocket serverSocket;
	Socket senderSocket;
	DataInputStream dataInputStream;
	DataOutputStream dataOutputStream;
	Container container;
	JLabel connectionStatusLabel;
	JLabel myIpLabel;
	JLabel fileLabel;
	JTextField fileTextField;
	JButton fileChooseButton;
	JButton sendButton;
	JFileChooser fileChooser;
	String operatingSystemName;
	File file;

	public Sender(JFrame parentFrame, String ip) throws IOException {
		this.parentFrame = parentFrame;
		this.ip = ip;
		container = this.getContentPane();
		myIpLabel = new JLabel("Your IP: " + this.ip);
		fileLabel = new JLabel("File: ");
		fileTextField = new JTextField();
		fileTextField.setEditable(false);
		fileChooseButton = new JButton("Choose File");
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		connectionStatusLabel = new JLabel("Waiting For Connection......");
		file = null;
		operatingSystemName = System.getProperty("os.name");
		sendButton = new JButton("Send");
		serverSocket = new ServerSocket(5000);
		senderSocket = null;
		dataInputStream = null;
		dataOutputStream = null;
		this.setLocationAndSize();
		this.addComponentsToContainer();
		this.setTitle("Sender");
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		fileChooseButton.addActionListener(this);
		sendButton.addActionListener(this);
		ConnecionHandler connecionHandler = new ConnecionHandler(this);
		connecionHandler.start();
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent windowEvent) {
				try {
					if (serverSocket != null)
						serverSocket.close();
					if (senderSocket != null)
						senderSocket.close();
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
		connectionStatusLabel.setBounds(50, 70, 200, 20);
		fileChooseButton.setBounds(50, 110, 100, 30);
		fileLabel.setBounds(10, 150, 40, 30);
		fileTextField.setBounds(60, 150, 200, 30);
		sendButton.setBounds(50, 200, 100, 30);
	}

	private void addComponentsToContainer() {
		container.add(myIpLabel);
		container.add(connectionStatusLabel);
		container.add(sendButton);
		sendButton.setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource() == fileChooseButton) {
			int selectedValue = fileChooser.showOpenDialog(this);
			if (selectedValue == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile();
				if (file.exists()) {
					fileTextField.setText(file.getAbsolutePath());
					sendButton.setVisible(true);
				} else {
					sendButton.setVisible(false);
					fileTextField.setText("");
					JOptionPane.showMessageDialog(this, "File Does not Exist", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		if (actionEvent.getSource() == sendButton) {
			try {
				dataInputStream = new DataInputStream(senderSocket.getInputStream());
				dataOutputStream = new DataOutputStream(senderSocket.getOutputStream());
				String encryptedFilePath = encryptFile();
				Thread.sleep(2000);
				File encryptedFile = new File(encryptedFilePath);
				byte[] encryptedFileBytes = new byte[(int) encryptedFile.length()];
				FileInputStream fileInputStream = new FileInputStream(encryptedFile);
				if (encryptedFile.exists()) {
					dataOutputStream.writeUTF(encryptedFile.getName());
					dataOutputStream.flush();
					dataOutputStream.writeUTF(Integer.toString((int) encryptedFile.length()));
					while (fileInputStream.read(encryptedFileBytes) > 0) {
						dataOutputStream.write(encryptedFileBytes);
					}
					dataOutputStream.flush();
					encryptedFile.delete();
				} else {
					JOptionPane.showMessageDialog(this, "Error Encrypting", "Error", JOptionPane.ERROR_MESSAGE);
				}
				fileInputStream.close();
			} catch (IOException ioException) {
			} catch (InterruptedException interruptedException) {
			}
		}
	}

	private synchronized String encryptFile() {
		String command = "openssl aes256 -e -a -k obQuubKgwmvX0CUjDVD1OkwsXVZgQUHa";
		command += " -in " + file.getAbsolutePath() + " -out ";
		String encryptedFilePath = "";
		if (operatingSystemName.equals("Linux")) {
			encryptedFilePath = file.getParent() + "/" + file.getName() + ".enc";
		} else {
			encryptedFilePath = file.getParent() + "\\" + file.getName() + ".enc";
		}
		command += encryptedFilePath;
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec(command);
		} catch (IOException ioException) {
		}
		return encryptedFilePath;
	}

	class ConnecionHandler extends Thread {
		JFrame frame;

		public ConnecionHandler(JFrame frame) {
			this.frame = frame;
		}

		@Override
		public synchronized void run() {
			try {
				senderSocket = serverSocket.accept();
				container.add(fileChooseButton);
				container.add(fileLabel);
				container.add(fileTextField);
				connectionStatusLabel.setText("Connected to " + senderSocket.getInetAddress().getHostAddress());
				frame.update(frame.getGraphics());
			} catch (SecurityException securityException) {
			} catch (IOException ioException) {
			} catch (IllegalArgumentException illegalArgumentException) {
			}
		}
	}
}

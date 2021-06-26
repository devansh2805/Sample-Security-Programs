import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class FileTransferFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1527685642L;
	Container container;
	JButton sendButton;
	JButton receiveButton;
	String ipAddress;
	
	public FileTransferFrame() {
		ipAddress = getPrivateIPAddress();
		container = this.getContentPane();
		sendButton = new JButton("Send");
		receiveButton = new JButton("Receive");
		this.setLocationAndSize();
		this.addComponentsToContainer();
		this.addActionListenertoButtons();
		this.setTitle("Secure File Transfer");
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
	}
	
	private void setLocationAndSize() {
		container.setLayout(null);
		this.setSize(300, 300);
		sendButton.setBounds(100, 50, 100, 30);
		receiveButton.setBounds(100, 150, 100, 30);
	}
	
	private void addComponentsToContainer() {
		container.add(sendButton);
		container.add(receiveButton);
	}
	
	private void addActionListenertoButtons() {
		sendButton.addActionListener(this);
		receiveButton.addActionListener(this);
	}
	
	private String getPrivateIPAddress() {
		try {
			Enumeration<NetworkInterface> networkInterfacEnumeration = NetworkInterface.getNetworkInterfaces();
			while (networkInterfacEnumeration.hasMoreElements()) {
    			NetworkInterface networkInterface = networkInterfacEnumeration.nextElement();
    			Enumeration<InetAddress> addressesEnumeration = networkInterface.getInetAddresses();
    			while (addressesEnumeration.hasMoreElements()) {
        			InetAddress address = addressesEnumeration.nextElement();
        			if(address.getHostAddress().startsWith("192")) {
        				return address.getHostAddress();
        			}
    			}
			}
		} catch (SocketException socketException) {
			socketException.printStackTrace();
		}
		return "";
	}
	
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if(actionEvent.getSource() == sendButton) {
			try {
				new Sender(this, ipAddress);
				this.setVisible(false);
			} catch (IOException ioException) {
				ioException.printStackTrace();
				JOptionPane.showMessageDialog(this, "IO Error", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		if(actionEvent.getSource() == receiveButton) {
			new Receiver(this, ipAddress);
			this.setVisible(false);
		}
	}

}

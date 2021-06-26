import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class AuthFrame extends JFrame implements ActionListener {
	static String LOGIN = "login";
	static String REGISTER = "register";
	HashMap<String, String> authInfoHashMap = new HashMap<String, String>();
	private static final long serialVersionUID = 1088740559800273990L;
	Container container = getContentPane();
	JLabel usernameLabel = new JLabel("Username");
	JLabel passwordLabel = new JLabel("Password");
	JTextField usernameTextField = new JTextField();
	JPasswordField passwordField = new JPasswordField();
	JButton button;
	JButton resetButton = new JButton("Reset");
	JCheckBox showPassword = new JCheckBox("Show Password");
	String type;

	public AuthFrame(String type) {
		this.type = type;
		container.setLayout(null);
		if (this.type == AuthFrame.LOGIN) {
			button = new JButton("Login");
			this.setTitle("Login");
			this.setBounds(30, 80, 370, 600);
		} else if (this.type == AuthFrame.REGISTER) {
			button = new JButton("Register");
			this.setTitle("Register");
			this.setBounds(500, 80, 370, 600);
		}
		this.setLocationAndSize();
		this.addComponentsToContainer();
		button.addActionListener(this);
		resetButton.addActionListener(this);
		showPassword.addActionListener(this);
		
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
	}

	public void setLocationAndSize() {
		usernameLabel.setBounds(50, 150, 100, 30);
		passwordLabel.setBounds(50, 220, 100, 30);
		usernameTextField.setBounds(150, 150, 150, 30);
		passwordField.setBounds(150, 220, 150, 30);
		showPassword.setBounds(150, 250, 150, 30);
		button.setBounds(50, 300, 100, 30);
		resetButton.setBounds(200, 300, 100, 30);
	}

	public void addComponentsToContainer() {
		container.add(usernameLabel);
		container.add(passwordLabel);
		container.add(usernameTextField);
		container.add(passwordField);
		container.add(showPassword);
		container.add(button);
		container.add(resetButton);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if (actionEvent.getSource() == button) {
			File authFile = new File("/home/devansh/eclipse-workspace/PasswordAuthentication/src/authinformation.txt");
			String usernameText = usernameTextField.getText();
			String passwordText = String.valueOf(passwordField.getPassword());
			String hashedPassword = "";
			boolean find = false;
			try(FileReader fileReader = new FileReader(authFile);
						BufferedReader bufferedReader = new BufferedReader(fileReader);) {
				hashedPassword = hashPassword(passwordText);
				String thisline = null;
				while((thisline = bufferedReader.readLine()) != null) {
					String[] partStrings = thisline.split("[,]");
					authInfoHashMap.put(partStrings[0], partStrings[1]);
				}
				find = authInfoHashMap.containsKey(usernameText);
				if (!authFile.exists()) {
					authFile.createNewFile();
				}
			} catch (IOException ioException) {
				ioException.printStackTrace();
			} catch (NoSuchAlgorithmException noSuchAlgorithmException) {
				noSuchAlgorithmException.printStackTrace();
			}
			if (this.type == AuthFrame.LOGIN) {
				if(find) {
					String savedPassword = authInfoHashMap.get(usernameText);
					if(savedPassword.equals(hashedPassword)) {
						JOptionPane.showMessageDialog(this, "Login Sucessful");
					} else {
						JOptionPane.showMessageDialog(this, "Password Incorrect");
					}
				} else {
					JOptionPane.showMessageDialog(this, "Username Wrong");
				}
			} else if (this.type == AuthFrame.REGISTER) {
				try (FileReader fileReader = new FileReader(authFile);
						BufferedReader bufferedReader = new BufferedReader(fileReader);
						FileWriter fileWriter = new FileWriter(authFile, true)) {
					if(!find) {
						fileWriter.write(usernameText + "," + hashedPassword);
						fileWriter.flush();
						JOptionPane.showMessageDialog(this, "Registration Successful");
					} else {
						JOptionPane.showMessageDialog(this, "Username Exists");
					}
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
			clear();
		}
		if (actionEvent.getSource() == resetButton) {
			clear();
		}
		if (actionEvent.getSource() == showPassword) {
			if (showPassword.isSelected()) {
				passwordField.setEchoChar((char) 0);
			} else {
				passwordField.setEchoChar('*');
			}
		}
	}

	public String toHexString(byte[] hash) {
		BigInteger number = new BigInteger(1, hash);
		return number.toString(16);
	}
	
	public void clear() {
		usernameTextField.setText("");
		passwordField.setText("");
	}
	
	public String hashPassword(String passwordText) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		byte[] password = messageDigest.digest(passwordText.getBytes(StandardCharsets.UTF_8));
		return toHexString(password);
	}
}

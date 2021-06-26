import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Authentication {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new AuthFrame(AuthFrame.LOGIN);
			new AuthFrame(AuthFrame.REGISTER);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
}

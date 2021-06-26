import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class FileTransfer {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			@SuppressWarnings("unused")
			FileTransferFrame fileTransferFrame = new FileTransferFrame();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException exceptions) {
			exceptions.printStackTrace();
		}
	}
}

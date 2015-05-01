import javax.swing.ImageIcon;
import javax.swing.JButton;

public class guiButton extends JButton {
	
	private static final long serialVersionUID = 1L;
	
	public guiButton(ImageIcon a){
	    this.setIcon(a);
		this.setBorderPainted(false);
		this.setFocusPainted(false);
		this.setContentAreaFilled(false);
	}
	
}

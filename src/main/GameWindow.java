package main;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;

public class GameWindow {

	private JFrame jFrame;

	public GameWindow(GamePanel gamePanel) {
		jFrame = new JFrame();

		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.add(gamePanel);
		jFrame.setResizable(false);
		jFrame.pack();
		jFrame.setLocationRelativeTo(null);
		jFrame.setVisible(true);
		jFrame.addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent e) {
				gamePanel.getGame().windowFocusLost();
			}
			
			@Override
			public void windowGainedFocus(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}

package huyngo__CSCI201L__Assignment2;

import javax.swing.UIManager;

public class Main {
	public static void main(String[] args){
		try{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}catch(Exception e){
			System.out.println("Warning! Cross-platform L&F not used!");
		}
		Window first_try = new Window();
		first_try.setVisible(true);
	}
}

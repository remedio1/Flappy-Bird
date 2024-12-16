package src;

import javax.swing.*;

public class Main {
	
	private static int boardHeigth = 640;
	private static int boardWidth = 360;
	
	public static void main(String[] args) {
	
		
	// instances 
	JFrame frame = new JFrame("FlappyBird");
	FlappyBird flappybird = new FlappyBird();
	// frame
	JButton start = new JButton();
	frame.add(start);
	frame.setVisible(true);
	frame.setFocusable(true);
	frame.setLocation(boardWidth, boardWidth);
	
	frame.pack();
	frame.setResizable(false);
	//frame.setLocationRelativeTo(null);
	frame.setSize(boardWidth, boardHeigth);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.add(flappybird);
	flappybird.requestFocus();
	frame.setVisible(true);
	flappybird.launchGame();
	}

	
}

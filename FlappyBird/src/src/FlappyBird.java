package src;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.Random;
import java.util.random.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JButton;
import javax.xml.stream.events.StartDocument;


public class FlappyBird extends JPanel implements Runnable, KeyListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int boardWidth = 340;
	private static int boardHeight = 640;
	BufferedImage image;
	Thread gameThread;
	
	// Images
	Image backgroundImage;   
	Image birdImage;
	Image topPipeImage;
	Image bottomPipeImage;
	
	// Bird
	int birdX = boardWidth/8;
	int birdY = boardHeight/4;
	int birdWidth = 34;
	int birdHeight = 24;
	
	// Pipes
	int pipeX = boardWidth;
	int pipeY = 0;
	int pipeWidth = 64;
	int pipeHeight = 512;
	
	// timer
	Timer placePipesTimer;
	
	//game over
	boolean gameOver = false;
	
	// score
	double score = 0;
	
	class Bird {
		int x = birdX;
		int y = birdY;
		int width = birdWidth;
		int height = birdHeight;
		Image image;
		
		Bird (Image image){
			this.image = image;
		}
	}
	
	class Pipe {
		int x = pipeX;
		int y = pipeY;
		int width = pipeWidth;
		int height = pipeHeight;
		Image image;
		boolean passed = false;
		
		Pipe(Image image){
			this.image = image;
		}
	}
	
	// game logic
	Bird bird;
	int velocityX = -4; // move pipes to the left speed (simulates bird moving right)
	int velocityY = 0;
	private static int gravity = 1;
	
	ArrayList<Pipe> pipes;
	Random random = new Random();
		
	//JFrame frame = new JFrame();
	//JButton start = new JButton("start");
	
	
	
	public void run () {
		double drawInterval = 1000000000/60;
		double delta = 0;
		long lasTime = System.nanoTime();
		long currenTime;
			
		while(gameThread != null) {
			
			currenTime = System.nanoTime();
			
			delta += (currenTime - lasTime)/drawInterval;
            lasTime = currenTime;
            
            if(delta >= 1) {
            	move();
            	repaint();
            	delta--;
            }
		}		
	}
	
	public void placePipes() {
		
		int openingSpace = boardHeight / 4;
	
		// top pipe
		int randomPipeY = (int)(pipeY - pipeHeight / 4 - Math.random() * (pipeHeight/ 2));
		Pipe topPipe = new Pipe(topPipeImage);
		topPipe.y = randomPipeY;
		pipes.add(topPipe);
		
		// bottom pipe
		Pipe bottomPipe = new Pipe(bottomPipeImage);
		bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
		pipes.add(bottomPipe);
				
	}
	
	public void launchGame(){
        gameThread = new Thread(this);
        gameThread.start();
    }
	
	public BufferedImage getImage (String imagePath) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
		} catch(IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public FlappyBird() {
		
		setFocusable(true);
		
		addKeyListener(this);
		
		setPreferredSize(new Dimension(boardWidth, boardHeight));
				
	//load images
		backgroundImage = getImage("/image/flappybirdbg");
		birdImage = getImage("/image/flappybird");
		topPipeImage = getImage("/image/toppipe");
		bottomPipeImage = getImage("/image/bottompipe");
		
	// bird
		bird = new Bird(birdImage);
		pipes = new ArrayList<Pipe>();
		
	// place pipes timer
		placePipesTimer = new Timer(900, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				placePipes();
			}
		});
	// place pipes timer
		placePipesTimer.start();              
	}
	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		draw(g);		
	}
	
	public void draw(Graphics g) {
		// background image
		g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, null);
		// bird image
		g.drawImage(bird.image, bird.x, bird.y,bird.width, bird.height, null);
		// pipes image
		for (int i = 0 ; i < pipes.size(); i++) {
			Pipe pipe = pipes.get(i);
			g.drawImage(pipe.image,pipe.x,pipe.y,pipe.width, pipe.height,null);			
		}		
		// game over
		if(gameOver) {
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial",Font.BOLD , 40));
			// g.drawString("GameOver", 70, 120);    
		}
		// score
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 32));
		if(gameOver) {
			g.drawString("GameOver: " + String.valueOf((int) score), 70, 120);
		} else {
			g.drawString(String.valueOf((int)score), 10, 35);
		}
		
	}
	
	@SuppressWarnings("removal")
	public void move() {
		
		//birds
		velocityY += gravity;
		bird.y += velocityY;
		bird.y = Math.max(bird.y, 0);	
		
		//pipes
		for(int i = 0; i < pipes.size(); i++) {
			Pipe pipe = pipes.get(i);
			pipe.x += velocityX;
		
			if(!pipe.passed && bird.x > pipe.x + pipe.width) {
				pipe.passed = true;
				score += 0.5;
			}
			
			if(collision(bird, pipe)) {
				gameOver = true;
			}
		}
		
		if(bird.y > boardHeight) {
			gameOver = true;
		}
		if(gameOver) {
    		placePipesTimer.stop();
    		gameThread = null;
    	}
		
	}
	 boolean collision(Bird a, Pipe b) {
	        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
	               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
	               a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
	               a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
	}

	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}	
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				velocityY = -10;
		if(gameOver) {
			// resetting the game
			bird.y = birdY;
			velocityY = 0;
			pipes.clear();
			score = 0;
			gameOver = false;
			launchGame();
			placePipesTimer.start();
			}
		}		
	}	
}

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.util.Random;

public class lobPong extends JFrame {
	private static final long serialVersionUID = 1L;
	public GraphicsPanel canvas; 
	Timer timer;
	Random random = new Random();
	JLabel levelLabel; // displays the level 
	JLabel timeLabel; // displays the time
	JLabel scoreLabel; // displays the score
	JLabel livesLabel; // displays the lives
	int level = 0;
	int lives = 3;
	double xBall = 10;
	double yBall = 10;
	int xPaddle = 200;
	int xKite; // extra-credit; for stationary object
	int yKite; // extra-credit; for stationary object
	int v0 = 10; // initial velocity
	double vX;
	double vY;
	int score = 0;
	double timeLeft; // time remaining per level
	double levelTime; // total time available per level
	boolean leftPressed; // for moving paddle
	boolean rightPressed; // for moving paddle
	int width = getWidth();
	int height = getHeight();
	public lobPong() {
		canvas = new GraphicsPanel();
		canvas.setSize(500,500);
		canvas.setVisible(true);
		canvas.addKeyListener(new KeyHandler());
		canvas.setFocusable(true);
		canvas.requestFocusInWindow();
		timer = new Timer(25, new TimerHandler()); // milliseconds
		levelLabel = new JLabel("Level: ");
		timeLabel = new JLabel("Time remaining: ");
		scoreLabel = new JLabel("Score: 0");
		livesLabel = new JLabel("Lives remaining: 3");
		canvas.add(levelLabel);
		canvas.add(timeLabel);
		canvas.add(scoreLabel);
		canvas.add(livesLabel);
		add(canvas);
		startLevel();
		// JButton button;
		// button = new JButton("START / NEXT LEVEL");
		// button.addActionListener(new ButtonHandler());
		// canvas.add(button);
	}
	public void startLevel() {
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		level += 1;
		if (level < 5) {
			levelLabel.setText("Level: " + level); 
			levelTime = 20.0 + 10.0*level;
			timeLeft = levelTime;
			timeLabel.setText("Time remaining: " + timeLeft);
			xBall = 10.0;
			yBall = 10.0;
			vX = v0*Math.sqrt(2)/2;
			vY = v0*Math.sqrt(2)/2;
			xKite = random.nextInt(width - 100); // extra-credit; reflections against a stationary object
			yKite = random.nextInt((int) (height*0.8 - 150));
			while (xKite < 150) {
				xKite = random.nextInt(width - 100);
			}
			while (yKite < 150) {
				yKite = random.nextInt((int) (height*0.8 - 150));
			}
			timer.start();
		} else {
			timeLabel.setText("Congratulations, you won the game!");
		}
	}
	public void reStartLevel() {
			levelTime = 20.0 + 10.0*level;
			timeLeft = levelTime;
			timeLabel.setText("Time remaining: " + timeLeft);
			xBall = 10.0;
			yBall = 10.0;
			vX = v0*Math.sqrt(2)/2;
			vY = v0*Math.sqrt(2)/2;
			timer.start();
	}
	// Initially we had a button which started the game and increased the level, however, 
	// then the focus would remain on the button, and the paddle would not move. 
	// Hence, we removed it. 
	
	/*public class ButtonHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			level += 1;
			if (level < 5) {
				levelTime = 40.0 + 20.0*level;
				timeLeft = levelTime;
				timeLabel.setText("Time remaining: " + timeLeft);
				xBall = 10.0;
				yBall = 10.0;
				vX = v0*Math.sqrt(2)/2;
				vY = v0*Math.sqrt(2)/2;
				timer.start();
			} else {
				timeLabel.setText("Congratulations, you won the game!");
			}
		}
		
	}*/
	public class KeyHandler implements KeyListener { // for moving the paddle using left and right arrow keys
		public int width = canvas.getWidth();
		@Override
		public void keyPressed(KeyEvent e) { 
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			case KeyEvent.VK_LEFT:
				if (xPaddle >= 20) {
					xPaddle -= 40;
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (xPaddle <= width - 120) {
					xPaddle += 40;
				}
				break;
			}
			repaint();
		}

		@Override
		public void keyReleased(KeyEvent e) {	
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	}
	public class TimerHandler implements ActionListener {
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		@Override
		public void actionPerformed(ActionEvent e) {
			if (timeLeft > 0) {
			if (xBall - 10 <= 0 && vX < 0) {   // reflections against the edge of the canvas
				if (timeLeft < levelTime - 2) {
					vX *= -1.0;
				}
			}
			if (xBall + 10 >= width && vX > 0) {
				vX *= -1.0;
			}
			if (yBall - 10 <= 0) {
				vY *= -1.0;
			}
			if (xBall >= xPaddle && xBall <= xPaddle + 100 && vY < 0) { // bouncing of the ball after reflecting against the paddle
				if (yBall + 10 >= (height*0.8 - 30)) {
					if (yBall + 10 >= height) {
						if (lives > 0) { // if the ball falls below the paddle, then the player loses a life
							lives -= 1;
							livesLabel.setText("Lives remaining: " + lives);
							timer.stop();
							reStartLevel();
						} else {
							timeLabel.setText("Sorry, you lost the game.");
							timer.stop();
						}
					} else {
						vY *= -1;
						score += 1;
						scoreLabel.setText("Score: " + score);
					}
				}
			}
			if (yBall - 10 >= height) { // when the ball misses the paddle and falls, the player loses a life and the level reStarts. 
				if (lives > 0) {
					lives -= 1;
					livesLabel.setText("Lives remaining: " + lives);
					timer.stop();
					reStartLevel();
				} else {
					timeLabel.setText("Sorry, you lost the game.");
					timer.stop();
				}
			}
			if (xBall - 10 <= xKite + 100 && xBall > xKite && yBall - 10 <= yKite + 100 && yBall + 10 >= yKite) {
				vX *= -1;
			}
			if (xBall + 10 >= xKite && xBall < xKite + 100 && yBall - 10 <= yKite + 100 && yBall + 10 >= yKite) {
				vX *= -1;
			}
			if (yBall - 10 <= yKite + 100 && yBall > yKite && xBall - 10 <= xKite + 100 && xBall + 10 >= xKite) {
				vY *= -1;
			}
			if (yBall + 10 >= yKite && yBall < yKite + 100 && xBall - 10 <= xKite + 100 && xBall + 10 >= xKite) {
				vY *= -1;
			}
			vY -= 0.245;
			xBall += vX;
			yBall -= vY;
			timeLeft -= 0.025;
			timeLabel.setText("Time remaining: " + timeLeft);
			} else {
				score += 3;
				scoreLabel.setText("Score: " + score);
				timer.stop();
				startLevel();
			}
			repaint();
		}
		
	}
	protected class GraphicsPanel extends JPanel { // graphics panel
		private static final long serialVersionUID = 1L;
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			int height = getHeight();
			g.setColor(Color.RED);
			g.fillOval((int) (xBall - 10), (int) (yBall - 10), 20, 20); // red ball
			g.setColor(Color.CYAN);
			g.fillRect(xPaddle, height*4/5, 150, 20); // cyan paddle
			g.setColor(Color.MAGENTA);
			g.fillRect(xKite, yKite, 100, 100); // magenta square (stationary object)
		}
	}
	public static void main(String [] args) { // main method
		lobPong app = new lobPong();
		app.setVisible(true);
		app.setSize(500,500);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

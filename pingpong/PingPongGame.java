package pingpong;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.animation.AnimationTimer;

public class PingPongGame extends Application {
	
	private Font font = Font.font("Rockwell", FontWeight.BOLD, 20);	// font for the program.
	
	private static final int WIDTH = 700;	// width of the screen.
	private static final int HEIGHT = 600;	// height of the screen.
	
	// Creating a canvas.
	private Canvas canvas = new Canvas(WIDTH, HEIGHT);
	private double canvasWidth = canvas.getWidth();
	private double canvasHeight = canvas.getHeight();
	
	private Player player;	// accessing Player class
	private Computer computer;	//accessing Computer class
	private Ball ball;	// accessing Ball class
	private ScoreBoard scoreboard;	// accessing ScoreBoard class
	
	private Stage stage;
	private AnimationTimer timer;
	
	// Nested classes
	// ----------------------------------------START-----------------------------------------------
	
	/**
	 * Nested class that represents the player paddle.
	 * The class includes updateFrame() method and draw() method.
	 * updateFram() is to update the frame as the game progress.
	 * draw() method is to draw the player paddle on the screen.
	 */
	private class Player {
		double x;	// player position at x.
		double y;	// player position at y.
		double centerY;
		
		// Constructor to define the starting position of the player paddle.
		Player() {
			x = canvasWidth - 80;
			y = (canvasHeight / 2) - 50;
		}
		
		// To make sure the paddle does not move off screen.
		void updateFrame() {
			centerY = y + 50;
			if (y < 0) {
				y = 0;
			}
			else if (y > canvasHeight - 100) {
				y = canvasHeight - 100;
			}
		}
		
		// To draw the current state of the paddle.
		void draw(GraphicsContext g) {
			g.setFill(Color.WHITE);
			g.fillRect(x, y, 20, 100);
		}
	}
	
	/**
	 * Nested class that represents the computer paddle.
	 * The class includes updateFrame() method and draw() method.
	 * updateFrame method is to update the frame as the game progresses.
	 * draw() method is to draw the computer paddle on the screen.
	 *
	 */
	private class Computer {
		double x;	// computer position at x
		double y;	// computer position at y
		double centerY;
		double i = 4.5; // movement speed of the computer
		
		// Constructor to define the starting position of the computer paddle.
		Computer() {
			x = 60;
			y = (canvasHeight / 2) - 50;
		}
		
		// To make the computer moves.
		void updateFrame() {
			y += i;
			centerY = y + 50;
			
			// The computer paddle will follow the ball in y direction.
			if (y < ball.y) {
				i = 4.5;
			}
			else if (y > ball.y) {
				i = -4.5;
			}
			
			// To avoid the computer moves off screen.
			if (y < 0) {
				y = 0;
			}
			else if (y > canvasHeight - 100) {
				y = canvasHeight - 100;
			}
		}
		
		// To draw the current state of the computer paddle.
		void draw(GraphicsContext g) {
			g.setFill(Color.WHITE);
			g.fillRect(x, y, 20, 100);
		}
	}
	
	/**
	 * Nested class that represents the ball.
	 * The class includes updateFrame() method and draw() method.
	 * updateFrame method is to update the frame as the game progresses.
	 * draw() method is to draw the ball on the screen.
	 *
	 */
	private class Ball {
		double x;	// ball position at x.
		double y;	// ball position at y.
		double centerY;
		int xi = 5;	// movement speed of the ball in x direction.
		int yi = 5;	// movement speed of the ball in y direction.
		
		// Constructor to define the starting position of the ball.
		Ball() {
			x = (canvasWidth / 2) - 10;
			y = (canvasHeight / 2) - 10;
			
		}
		
		// To make the ball moves.
		void updateFrame() {
			x += xi;
			y += yi;
			centerY = y + 20;
			// When the ball pass the player or computer paddle, 
			// it will restart at the center of the screen.
			if (x < - 100 || x > canvasWidth + 120) {
				x = (canvasWidth / 2) - 10;
				y = (canvasHeight / 2) - 10;
				xi *= -1;
				yi *= -1;
			}
			// To check if the ball hit the player paddle.
			else if ((int) x - (int) player.x == -20 && Math.abs((int) centerY - (int) player.centerY) <= 60) {
				xi *= -1;
			}
			// To check if the ball hit the computer paddle.
			else if ((int) x - (int) computer.x == 20 && Math.abs((int) centerY - (int) computer.centerY) <= 60) {
				xi *= -1;
			}
			// To check if the ball hit the top or bottom.
			if (y < 0 || y > canvasHeight - 20) {
				yi *= -1;
			}
		}
		
		// To draw the current state of the ball.
		void draw(GraphicsContext g) {
			g.setFill(Color.WHITE);
			g.fillRoundRect(x, y, 20, 20, 30, 30);
		}
	}
	
	/**
	 * Nested class that represents the score board.
	 * The class includes updateFrame() method and draw() method.
	 * updateFrame method is to update the frame as the game progresses.
	 * draw() method is to display the scores on the screen.
	 *
	 */
	private class ScoreBoard {
		int playerScore;	// player score
		int computerScore;	// computer score
		
		// Starting score is 0 for both player and computer
		ScoreBoard() {
			playerScore = 0;
			computerScore = 0;
		}
		
		// Check the position of the ball to update score
		void updateFrame() {
			if (ball.x == 0) {
				playerScore += 1;
			}
			else if (ball.x == canvasWidth) {
				computerScore += 1;
			}
		}
		
		// To display the current score
		void draw(GraphicsContext g) {
			g.setFill(Color.WHITE);
			g.setFont(font);
			g.fillText("Computer: " + computerScore, 150, 50);
			g.fillText("Player: " + playerScore, 450, 50);
		}
	}
	// -----------------------------------------END------------------------------------------------
	
	/**
	 * Drawing will be handled by this method.
	 * First, it creates a black background.
	 * Then, it creates a player paddle and computer paddle at right and left respectively.
	 * It also creates a ball in the middle of the screen.
	 */
	public void draw() {
		
		GraphicsContext g = canvas.getGraphicsContext2D();
		
		// To make the black background color.
		g.setFill(Color.BLACK);
		g.fillRect(0, 0, canvasWidth, canvasHeight);
		
		// To make the player paddle.
		player.draw(g);
		
		// To make the computer paddle.
		computer.draw(g);
		
		// To make the ball.
		ball.draw(g);
		
		// To display the score.
		scoreboard.draw(g);
	}
	
	/**
	 * @param evt will accept the event object.
	 * To move the player paddle by the user using the key up and key down.
	 * The paddle will only move in y direction; therefore, it only handles
	 * KeyUp and KeyDown event from the keyboard.
	 */
	private void movePlayer(KeyEvent evt) {
		KeyCode key = evt.getCode();
		
		if (key == KeyCode.UP) {
			player.y -= 7;
		}
		else if (key == KeyCode.DOWN) {
			player.y += 7;
		}
	}
	
	/**
	 * Setting up the scene of the game.
	 * Width: 700, Height: 600.
	 */
	public void start(Stage stage) {
		
		player = new Player();
		computer = new Computer();
		ball = new Ball();
		scoreboard = new ScoreBoard();
		
		this.stage = stage;
		
		draw();
		
		Pane root = new Pane(canvas);
		Scene scene = new Scene(root);
		
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Ping Pong Game");
		
		scene.setOnKeyPressed(e -> movePlayer(e));	// To handle the keyboard input.
		
		timer = new AnimationTimer() {
			// The handle method is called once per frame while the animation is running.
			long previousFrameTime;
			public void handle(long time) {
				if (time - previousFrameTime > 0.95e9 / 60) {
					player.updateFrame();
					computer.updateFrame();
					ball.updateFrame();
					scoreboard.updateFrame();
					draw();
					previousFrameTime = time;
				}
			}
		};
		
		stage.focusedProperty().addListener((obj, oldVal, newVal) -> {
			// To turn the animation off when the window does not have the input focus.
			
			// The window has gained focus.
			if (newVal) {
				timer.start();
			}
			// The window has lost focus.
			else {
				timer.stop();
			}
			draw();
		});
		
		stage.show();
		timer.start();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

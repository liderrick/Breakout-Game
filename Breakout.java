/*
 * Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Canvas height offset: Canvas height is always off by 19 pixels. Reason unknown */
	private static final int CANVAS_HEIGHT_OFFSET = 19;

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600 + CANVAS_HEIGHT_OFFSET;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT - CANVAS_HEIGHT_OFFSET;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;
	private static final int PADDLE_SECTION_WIDTH = PADDLE_WIDTH/3;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Diameter of the ball in pixels */
	private static final int BALL_DIAMETER = BALL_RADIUS * 2;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;

/** Paddle y-coordinate */
	private static final int PADDLE_Y_COORDINATE = HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;

/** Animation delay or pause time between ball moves */
	private static final int DELAY = 10;

/** Class (or instance) variables */
	private GRect brick, paddleLeft, paddleCenter, paddleRight;
	private GOval ball;
	private GLabel scoreLabel, scoreTracker, turnTracker, instruction1, instruction2, instruction3;

	private double vx, vy;
	private int turnNumber, score, bricksRemaining;

	private RandomGenerator rgen = RandomGenerator.getInstance();

	AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");

/** Starts an instance of the Breakout program. */
	public static void main(String[] args) {
		new Breakout().start(args);
	}

/** Runs the Breakout program. */
	public void run() {

		System.out.println("width: " + getWidth());
		System.out.println("height: " + getHeight());
		System.out.println("Breakout is running...");

		addMouseListeners();

		scoreSetup();
		labelSetup();
		brickSetup();
		paddleSetup();
		ballSetup();

	}

/** Initializes variables used to keep track game progress */
	private void scoreSetup() {
		turnNumber = NTURNS;
		bricksRemaining = 100;
		score = 0;
		vy = 3.0;
	}

/** Adds play instructions, and score and balls left indicator  */
	private void labelSetup() {
		/* Initialize scoreLabel */
		scoreLabel = new GLabel("");

		instruction1 = new GLabel("Click to start");
		instruction1.setFont("Times-24");
		add(instruction1, (WIDTH - instruction1.getWidth())/2, (HEIGHT + instruction1.getAscent()) * 4.5/8);

		instruction2 = new GLabel("You have " + NTURNS + " turns.");
		instruction2.setFont("Times-24");
		add(instruction2, (WIDTH - instruction2.getWidth())/2, (HEIGHT + instruction2.getAscent()) * 5/8);

		instruction3 = new GLabel("Pts: Cyan - 1, Green - 2, Yellow - 3, Orange - 4, Red - 5");
		instruction3.setFont("Times-14");
		add(instruction3, (WIDTH - instruction3.getWidth())/2, (HEIGHT + instruction3.getAscent()) * 6/8);

		scoreTracker = new GLabel("Score: " + score);
		scoreTracker.setFont("Times-Bold-14");
		add(scoreTracker, 2, HEIGHT - scoreTracker.getDescent());

		turnTracker = new GLabel("Balls left: " + turnNumber);
		turnTracker.setFont("Times-Bold-14");
		add(turnTracker, WIDTH - turnTracker.getWidth() - 7, HEIGHT - turnTracker.getDescent());

	}

/** Creates the brick setup. */
	private void brickSetup() {

		for (int i = 0; i < NBRICK_ROWS; i++) {
			for (int j = 0; j < NBRICKS_PER_ROW; j++) {

				int x_start = (WIDTH - BRICK_WIDTH * NBRICKS_PER_ROW - BRICK_SEP * (NBRICKS_PER_ROW - 1) ) / 2;
				int y_start = BRICK_Y_OFFSET ;

				int x = x_start + (BRICK_SEP + BRICK_WIDTH) * j;
				int y = y_start + (BRICK_SEP + BRICK_HEIGHT) * i;

				brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if (i < 2) {
					brick.setFillColor(Color.RED);
					brick.setColor(Color.RED);
				} else if (i >= 2 && i < 4) {
					brick.setFillColor(Color.ORANGE);
					brick.setColor(Color.ORANGE);
				} else if (i >= 4 && i < 6) {
					brick.setFillColor(Color.YELLOW);
					brick.setColor(Color.YELLOW);
				} else if (i >= 6 && i < 8) {
					brick.setFillColor(Color.GREEN);
					brick.setColor(Color.GREEN);
				} else if (i >= 8 && i < 10) {
					brick.setFillColor(Color.CYAN);
					brick.setColor(Color.CYAN);
				}
				add(brick);
			}
		}
	}

/** Creates the paddle setup. */
	private void paddleSetup() {
		double x_initial = (WIDTH - PADDLE_WIDTH)/2;

		paddleLeft = new GRect(PADDLE_SECTION_WIDTH, PADDLE_HEIGHT);
		paddleCenter = new GRect(PADDLE_SECTION_WIDTH, PADDLE_HEIGHT);
		paddleRight = new GRect(PADDLE_SECTION_WIDTH, PADDLE_HEIGHT);

		paddleLeft.setFilled(true);
		paddleCenter.setFilled(true);
		paddleRight.setFilled(true);

		add(paddleLeft, x_initial, PADDLE_Y_COORDINATE);
		add(paddleCenter, x_initial + PADDLE_SECTION_WIDTH, PADDLE_Y_COORDINATE);
		add(paddleRight, x_initial + PADDLE_SECTION_WIDTH * 2, PADDLE_Y_COORDINATE);


	}

/** Creates the ball setup. */
	private void ballSetup() {
		ball = new GOval (BALL_DIAMETER,BALL_DIAMETER);
		ball.setFilled(true);
		add(ball,(WIDTH - BALL_DIAMETER)/2,(HEIGHT - BALL_DIAMETER)/2);

		waitForClick();

		/* Removes play instructions on first turn */
		if (turnNumber == NTURNS) {
			remove(instruction1);
			remove(instruction2);
			remove(instruction3);
		}

		turnNumber--;
		turnTracker.setLabel("Balls left: " + turnNumber);

		/* Initializes ball movement parameters vy and vx.*/

		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) vx = -vx;

		while (ball.getX() < (WIDTH - BALL_DIAMETER) && ball.getY() < (HEIGHT - BALL_DIAMETER)){
			moveBall();
			checkForCollision();
			pause(DELAY);
		}

	}

/** Moves ball. */
	private void moveBall() {
		ball.move(vx, vy);
	}

/** Main check for collision. */
	private void checkForCollision() {
		wallCollisionCheck();
		GObject collider = 	getCollidingObject();
		while (collider != null) {
			if (collider == paddleLeft) {
				vy = -vy;
				if (vx > 0) {
					vx = rgen.nextDouble(3.0, 5.0);
				} else {
					vx = rgen.nextDouble(3.0, 5.0);
					vx = -vx;
				}
				bounceClip.play();

			} else if (collider == paddleCenter) {
				vy = -vy;
				if (vx > 0) {
					vx = rgen.nextDouble(1.0, 3.0);
				} else {
					vx = rgen.nextDouble(1.0, 3.0);
					vx = -vx;
				}
				bounceClip.play();

			} else if (collider == paddleRight) {
				vy = -vy;
				if (vx > 0) {
					vx = rgen.nextDouble(3.0, 5.0);
				} else {
					vx = rgen.nextDouble(3.0, 5.0);
					vx = -vx;
				}
				bounceClip.play();


			} else if (collider == scoreTracker || collider == turnTracker) {
				/* Null, score and turn trackers GLabels are not removed if ball collides with it */
			} else {

				/* Assigns various points to differnt color bricks */
				if (collider.getColor() == Color.CYAN){
					score++;
				} else if (collider.getColor() == Color.GREEN) {
					score = score + 2;
				} else if (collider.getColor() == Color.YELLOW) {
					score = score + 3;
				} else if (collider.getColor() == Color.ORANGE) {
					score = score + 4;
				} else {
					score = score + 5;
				}

				bricksRemaining--;

				remove(collider);

				bounceClip.play();
				vy = -vy;
				vy += 0.05; /* Increases game difficulty as bricks are removed */

				scoreTracker.setLabel("Score: " + score);

				if (bricksRemaining == 0 || bricksRemaining < 0) {

					GLabel gameOverLabel = new GLabel("You Win!");
					gameOverLabel.setFont("Times-Bold-42");
					add (gameOverLabel, (WIDTH - gameOverLabel.getWidth())/2, (HEIGHT + gameOverLabel.getAscent())/2);

					scoreLabel.setLabel("Highest Score: " + score);
					scoreLabel.setFont("Times-32");
					add (scoreLabel, (WIDTH - scoreLabel.getWidth())/2, gameOverLabel.getY()+scoreLabel.getHeight());

					remove(ball);
					vx = vy = 0;
				} else if (bricksRemaining < 0){
					/* This extra else/if statement updates the final score
					 * when the last two bricks are hit and removed simultaneously.
					 * Without it, the final score would be lagging by 1.
					 */
					scoreLabel.setLabel("Highest Score: " + score);
				}
			}
			break;
		}
	}

/** Check for wall collision. */
	private void wallCollisionCheck() {

		/* Collision with floor (bottom). */
		if (ball.getY() > (HEIGHT - BALL_DIAMETER)) {
			remove(ball);

			if (turnNumber > 0) {
				ballSetup();
			} else {
				GLabel gameOverLabel = new GLabel("Game Over");
				gameOverLabel.setFont("Times-Bold-42");
				add (gameOverLabel, (WIDTH - gameOverLabel.getWidth())/2, (HEIGHT + gameOverLabel.getAscent())/2);

				scoreLabel.setLabel("Final Score: " + score);
				scoreLabel.setFont("Times-32");
				add (scoreLabel, (WIDTH - scoreLabel.getWidth())/2, gameOverLabel.getY()+scoreLabel.getHeight());
			}

		}

		/* Collision with ceiling (top). */
		if (ball.getY() < 0) {
			vy = -vy;
			double diff = ball.getY();
			ball.move(0, -2 * diff);
		}

		/* Collision with right wall. */
		if (ball.getX() > (WIDTH - BALL_DIAMETER)) {
			vx = -vx;
			double diff = ball.getX() - (WIDTH - BALL_DIAMETER);
			ball.move(-2 * diff, 0);
		}

		/* Collision with left wall. */
		if (ball.getX() < 0){
			vx = -vx;
			double diff = ball.getX();
			ball.move(-2 * diff, 0);
		}
	}


/** Check for object collision and return colliding object */
	private GObject getCollidingObject() {
		double ballX = ball.getX();
		double ballY = ball.getY();
		GObject gobj = null; /* Initialize gobj to accept GObject */

		/* A while loop is used to detect if gobj != null, once it satisfies the condition,
		 * the loop is broken and gobj is returned. If gobj remains null by the end of the
		 * loop, then loop is broken and gobj is returned null */
		while (true) {

			/* The following if/else statement solves the sticking issue between ball and paddle */
			if (vy < 0 ) {
				gobj = getElementAt(ballX, ballY);
				if (gobj != null) break;

				gobj = getElementAt(ballX + BALL_DIAMETER, ballY);
				if (gobj != null) break;
			} else {
				gobj = getElementAt(ballX, ballY + BALL_DIAMETER);
				if (gobj != null) break;

				gobj = getElementAt(ballX + BALL_DIAMETER, ballY + BALL_DIAMETER);
				if (gobj != null) break;
			}

			break;
		}

		return gobj;

	}

/** Called on mouse moved to reposition paddle along x-axis.*/
	public void mouseMoved(MouseEvent e) {
		paddleLeft.move(e.getX() - paddleLeft.getX() - PADDLE_SECTION_WIDTH * 3/2, 0);
		paddleCenter.move(e.getX() - paddleCenter.getX() - PADDLE_SECTION_WIDTH * 1/2, 0);
		paddleRight.move(e.getX() - paddleRight.getX() + PADDLE_SECTION_WIDTH * 1/2, 0);
	}

}

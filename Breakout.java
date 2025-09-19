
import acm.graphics.*;
import acm.program.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

    /** Width and height of application window in pixels */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /** Dimensions of game board in pixels (usually the same) */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;

    /** Dimensions of the paddle */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;

    /** Offset of the paddle up from the bottom */
    private static final int PADDLE_Y_OFFSET = 30;

    /** Number of bricks per row */
    private static final int NBRICKS_PER_ROW = 10;

    /** Number of rows of bricks */
    private static final int NBRICK_ROWS = 10;

    /** Separation between bricks */
    private static final int BRICK_SEP = 4;

    /** Width of a brick */
    private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

    /** Height of a brick */
    private static final int BRICK_HEIGHT = 8;

    /** Radius of the ball in pixels */
    private static final int BALL_RADIUS = 10;

    /** Offset of the top brick row from the top */
    private static final int BRICK_Y_OFFSET = 70;

    /** Number of "lives" (balls) before the player loses */
    private static final int NUM_LIVES = 3;

    /**
     * Global variables declared here. You should feel free to add others as needed.
     */
    GRect paddle;
    GOval ball;
    int score = 0;
    double velocityX;
    double velocityY;
    int livesLeft = NUM_LIVES;
    int bricksLeft = NBRICKS_PER_ROW * NBRICK_ROWS;
    boolean gameHasEnded = false;

    Color[] brickColors = { Color.red, Color.orange, Color.yellow, Color.green, Color.cyan };

    GPolygon[] hearts = { new GPolygon(), new GPolygon(), new GPolygon() };
    boolean hasStartedGame = false;

    /** Runs the Breakout program. */
    public void run() {
        setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
        setupBricks();

        // showStartMenu();
        // while (!hasStartedGame){}
        hasStartedGame = true;
        addMouseListeners(); // register mouse events

        setupPaddle();
        drawHearts();
        countdown();
        setUpBall();
        animationLoop();

    }

    GRect normalMode;

    public void drawButtonWithBg(String text, int x, int y) {
        GLabel normalMode = new GLabel(text);
        normalMode.setFont("SansSerif-30");
        normalMode.setColor(Color.white);
        normalMode.setLocation(x - normalMode.getWidth() / 2, y - normalMode.getHeight() / 2);

        GRect background = new GRect(0, 0, normalMode.getWidth() * 5 / 4, normalMode.getHeight() * 3 / 2);
        background.setLocation(x - normalMode.getWidth() / 2, y - background.getHeight() / 2);
        background.setFilled(true);
        add(background);
        add(normalMode);
    }

    public void showStartMenu() {
        drawButtonWithBg("Normal Mode", getWidth() / 2, getHeight() / 2);
        drawButtonWithBg("Fenzy Mode", getWidth() / 2, getHeight() / 2 + 100);

    }

    public void drawHearts() {
        for (int i = 0; i < 3; i++) {
            hearts[i] = new GPolygon(getWidth() - 120 + i * 40, 20);
            drawHeart(hearts[i]);
        }

    }

    public void drawHeart(GPolygon heart) {
        heart.addVertex(0, 5);
        heart.addVertex(20, -10);
        heart.addVertex(30, 10);
        heart.addVertex(15, 25);
        heart.addVertex(0, 40);
        heart.addVertex(-15, 25);
        heart.addVertex(-30, 10);
        heart.addVertex(-20, -10);
        heart.setFilled(true);
        heart.scale(0.5);
        heart.setFilled(true);
        heart.setFillColor(Color.red);
        heart.setColor(Color.black);
        // heart.setLineWidth(8);
        add(heart);
    }

    public void resetBall() {
        if (livesLeft > 0) {
            remove(ball);
            countdown();
            setUpBall();
        } else {
            showEndScreen();
        }
    }

    public void showEndScreen() {
        if (gameHasEnded == false) {
            removeAll();
            gameHasEnded = true;
            setBackground(Color.red);
            GLabel stats = new GLabel("You lost :(");
            stats.setFont("SansSerif-30");
            stats.setLocation(getWidth() / 2 - stats.getWidth() / 2, getHeight() / 2 - stats.getHeight() / 2);
            GLabel bricksDestroyedLabel = new GLabel("You destroyed " + (100 - bricksLeft) + " bricks!");
            bricksDestroyedLabel.setFont("SansSerif-30");
            bricksDestroyedLabel.setLocation(getWidth() / 2 - bricksDestroyedLabel.getWidth() / 2,
                    getHeight() / 2 - bricksDestroyedLabel.getHeight() / 2 + 50);
            add(stats);
            add(bricksDestroyedLabel);
        }
    }

    public void showWinScreen() {
        if (gameHasEnded == false) {
            removeAll();
            gameHasEnded = true;
            setBackground(Color.green);
            GLabel stats = new GLabel("You Win :)");
            stats.setFont("SansSerif-30");
            stats.setLocation(getWidth() / 2 - stats.getWidth() / 2, getHeight() / 2 - stats.getHeight() / 2);
            add(stats);
        }
    }

    public void setupBricks() {
        for (int i = 0; i < NBRICK_ROWS; i++) {
            for (int j = 0; j < NBRICKS_PER_ROW; j++) {
                GRect brick = new GRect((BRICK_WIDTH + BRICK_SEP) * j, (BRICK_HEIGHT + BRICK_SEP) * i + BRICK_Y_OFFSET,
                        BRICK_WIDTH, BRICK_HEIGHT);
                brick.setFilled(true);
                brick.setColor(brickColors[Math.round(i / 2)]);
                add(brick);
            }
        }
    }

    public void setupPaddle() {
        paddle = new GRect(0, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFilled(true);
        add(paddle);
    }

    public void mouseMoved(MouseEvent event) {
        // if (paddle != null) {
        if (event.getX() >= 0 && event.getX() + PADDLE_WIDTH - PADDLE_WIDTH / 2 <= getWidth()) {

            paddle.setLocation(event.getX() - PADDLE_WIDTH / 2, getHeight() - PADDLE_Y_OFFSET);
        }
        // }
    }

    public void setUpBall() {
        ball = new GOval(getWidth() / 2 - BALL_RADIUS / 2, getHeight() / 3 - BALL_RADIUS / 2, BALL_RADIUS * 2,
                BALL_RADIUS * 2);
        ball.setFilled(true);
        add(ball);
        velocityY = 2;
        velocityX = 1 + Math.random() * 1;

        if (Math.random() > 0.5) {
            velocityX = -velocityX;
        }
    }

    public void countdown() {
        GLabel countdownLabel = new GLabel("3");
        countdownLabel.setLocation(getWidth() / 2 - countdownLabel.getWidth() / 2,
                getHeight() / 2 - countdownLabel.getHeight() / 2);
        countdownLabel.setFont("SansSerif-1");
        add(countdownLabel);
        countdownLabel.setColor(new Color(0, 0, 0, 0));
        for (int i = 0; i < 3; i++) {
            countdownLabel.setLabel(3 - i + "");
            for (int j = 0; j < 100; j++) {
                countdownLabel.setFont("SansSerif-" + j * 5);
                countdownLabel.setLocation(getWidth() / 2 - countdownLabel.getWidth() / 2,
                        getHeight() / 2 + countdownLabel.getHeight() / 4);
                Color tranparent = new Color(0, 0, 0, 255 - (j * 255 / 100));
                countdownLabel.setColor(tranparent);
                pause(5);
            }
        }
        remove(countdownLabel);
    }

    public void updateBall() {
        ball.move(velocityX, velocityY);
        if (ball.getY() + ball.getHeight() > getHeight()) {
            velocityY = 0;
            velocityX = 0;
            livesLeft -= 1;
            if (livesLeft >= 1) {
                remove(hearts[livesLeft]);
            }
            for (int i = 0; i < 3; i++) {
                ball.setColor(Color.red);
                pause(100);
                ball.setColor(Color.black);
                pause(100);
            }
            resetBall();
        }
        if (ball.getX() + ball.getWidth() > getWidth()) {
            velocityX = -velocityX;
        }
        if (ball.getY() < 0) {
            velocityY = -velocityY;
        }
        if (ball.getX() < 0) {
            velocityX = -velocityX;
        }
    }

    GObject collidePart;

    public void findCornerWithElement() {
        collidePart = null;
        if (getElementAt(ball.getX(), ball.getY()) != null) {
            collidePart = getElementAt(ball.getX(), ball.getY());
        } else if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY()) != null) {
            collidePart = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY());
        } else if (getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2) != null) {
            collidePart = getElementAt(ball.getX() + BALL_RADIUS * 2, ball.getY() + BALL_RADIUS * 2);
        } else if (getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2) != null) {
            collidePart = getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2);
        }

    }

    public void checkForCollisions() {
        findCornerWithElement();

        if (collidePart == null) {
            return;
        } else if (collidePart == paddle) {
            // ball.move(velocityX, -velocityY);
            velocityY = -velocityY;
        } else if (collidePart.getColor() != Color.black) {
            velocityY = -velocityY;
            score += 10;
            remove(collidePart);
            bricksLeft -= 1;
            if (bricksLeft <= 0) {

                for (int i = 0; i < 3; i++) {
                    ball.setColor(Color.green);
                    pause(100);
                    ball.setColor(Color.black);
                    pause(100);
                }
                showWinScreen();
            }
        }
    }

    public void animationLoop() {
        while (true) {
            pause(5);
            updateBall();
            checkForCollisions();
        }
    }
}

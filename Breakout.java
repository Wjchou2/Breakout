
import acm.graphics.*;
import acm.program.*;
import acm.util.MediaTools;

import java.applet.AudioClip;
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
    double velocityX;
    double velocityY;

    GRect paddle;
    GOval ball;
    GLabel normalMode;
    GLabel frenzyMode;
    GRect normalModeBG;
    GRect frenzyModeBG;
    GObject collidePart;

    int mode;
    int score;

    int livesLeft;
    int bricksLeft;

    boolean gameHasEnded;
    boolean hasStartedGame;
    boolean playAgain = false;
    GLabel playAgainLabel;
    GLabel scoreLabel;
    GRect playAgainLabelBG;
    Color[] brickColors = { Color.red, Color.orange, Color.yellow, Color.green, Color.cyan };
    GPolygon[] hearts = { new GPolygon(), new GPolygon(), new GPolygon() };

    public void run() {
        setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
        addMouseListeners();
        // Show Menu
        while (true) {
            mode = 0;
            score = 0;
            livesLeft = NUM_LIVES;
            bricksLeft = NBRICKS_PER_ROW * NBRICK_ROWS;
            gameHasEnded = false;
            hasStartedGame = false;
            playAgain = false;

            showStartMenu();
            // Wait for player to click a gamemode
            while (!hasStartedGame) {
                pause(10);
            }
            removeAll();
            setUpNewRound();
            animationLoop();
            createPlayAgainButton(getWidth() / 2, getHeight() / 2 + 150);
            // Wait For play again to be clicked
            while (!playAgain) {
                pause(10);
            }
            removeAll();
        }

    }

    public void setUpNewRound() {
        setupBricks();
        drawHearts();
        setupPaddle();
        createScoreLabel(getWidth() / 2 - 100, 80);
        countdown();
        setUpBall();
    }

    public void createPlayAgainButton(int x, int y) {
        playAgainLabel = new GLabel("Play Again!");
        playAgainLabel.setFont("SansSerif-30");
        playAgainLabel.setColor(Color.white);
        playAgainLabel.setLocation(x - playAgainLabel.getWidth() / 2, y - playAgainLabel.getHeight() / 2);

        playAgainLabelBG = new GRect(0, 0, playAgainLabel.getWidth() * 5 / 4, playAgainLabel.getHeight() * 3 / 2);
        playAgainLabelBG.setLocation(x - playAgainLabelBG.getWidth() / 2, y - playAgainLabelBG.getHeight());
        playAgainLabelBG.setFilled(true);
        add(playAgainLabelBG);
        add(playAgainLabel);
    }

    public void createScoreLabel(int x, int y) {
        scoreLabel = new GLabel("0");
        scoreLabel.setFont("SansSerif-50");
        scoreLabel.setColor(Color.black);
        scoreLabel.setLocation(x - scoreLabel.getWidth() / 2, y - scoreLabel.getHeight() / 2);
        add(scoreLabel);
    }

    public void updateScoreLabel(int x, int y) {
        scoreLabel.setLabel("" + score);
        scoreLabel.setLocation(x - scoreLabel.getWidth() / 2, y - scoreLabel.getHeight() / 2);
    }

    public void showStartingBrickBackground() {
        for (int i = 0; i < NBRICK_ROWS * 10; i++) {
            for (int j = 0; j < NBRICKS_PER_ROW; j++) {
                GRect brick = new GRect((BRICK_WIDTH + BRICK_SEP) * j, (BRICK_HEIGHT + BRICK_SEP) * i,
                        BRICK_WIDTH, BRICK_HEIGHT);
                brick.setFilled(true);
                brick.setColor(brickColors[i % 5]);
                add(brick);
            }
        }
    }

    public void showStartMenu() {
        setBackground(Color.white);
        showStartingBrickBackground();
        drawButtonWithBg("Normal Mode", getWidth() / 2, getHeight() / 2);
        drawButtonWithBgfrenzy("Crazy Mode", getWidth() / 2, getHeight() / 2 + 100);
        drawBreakoutLabel("Breakout", getWidth() / 2, 100);
    }

    public void drawButtonWithBg(String text, int x, int y) {
        normalMode = new GLabel(text);
        normalMode.setFont("SansSerif-30");
        normalMode.setColor(Color.white);
        normalMode.setLocation(x - normalMode.getWidth() / 2, y - normalMode.getHeight() / 2);

        normalModeBG = new GRect(0, 0, normalMode.getWidth() * 5 / 4, normalMode.getHeight() * 3 / 2);
        normalModeBG.setLocation(x - normalModeBG.getWidth() / 2, y - normalModeBG.getHeight());
        normalModeBG.setFilled(true);
        add(normalModeBG);
        add(normalMode);
    }

    public void drawButtonWithBgfrenzy(String text, int x, int y) {
        frenzyMode = new GLabel(text);
        frenzyMode.setFont("SansSerif-30");
        frenzyMode.setColor(Color.white);
        frenzyMode.setLocation(x - frenzyMode.getWidth() / 2, y - frenzyMode.getHeight() / 2);

        frenzyModeBG = new GRect(0, 0, frenzyMode.getWidth() * 5 / 4, frenzyMode.getHeight() * 3 / 2);
        frenzyModeBG.setLocation(x - frenzyModeBG.getWidth() / 2, y - frenzyModeBG.getHeight());
        frenzyModeBG.setFilled(true);
        add(frenzyModeBG);
        add(frenzyMode);
    }

    public void drawBreakoutLabel(String text, int x, int y) {
        GLabel BreakoutLabel = new GLabel(text);
        BreakoutLabel.setFont("SansSerif-70");
        BreakoutLabel.setColor(Color.black);
        BreakoutLabel.setLocation(x - BreakoutLabel.getWidth() / 2, y);
        add(BreakoutLabel);
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
        add(heart);
    }

    public void showEndScreen() {
        if (gameHasEnded == false) {
            removeAll();
            gameHasEnded = true;
            setBackground(Color.red);
            GLabel stats = new GLabel("You lost :(");
            stats.setFont("SansSerif-30");
            stats.setLocation(getWidth() / 2 - stats.getWidth() / 2, getHeight() / 2 - stats.getHeight() / 2);
            GLabel bricksDestroyedLabel = new GLabel("Your Score is " + score + "!");
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
        paddle = new GRect(0, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH * (mode == 1 ? 1 : 3), PADDLE_HEIGHT);
        paddle.setFilled(true);
        add(paddle);
    }

    public void mouseMoved(MouseEvent event) {
        GObject elm = getElementAt(event.getX(), event.getY());

        if (normalModeBG != null && elm == normalModeBG || elm == normalMode) {
            normalModeBG.setColor(new Color(55, 61, 60));
        } else if (frenzyModeBG != null && elm == frenzyMode || elm == frenzyModeBG) {

            frenzyModeBG.setColor(new Color(55, 61, 60));
        } else if (playAgainLabelBG != null && (elm == playAgainLabel || elm == playAgainLabelBG)) {
            playAgainLabelBG.setColor(new Color(55, 61, 60));
        } else {
            frenzyModeBG.setColor(Color.black);
            normalModeBG.setColor(Color.black);
            if (playAgainLabelBG != null) {
                playAgainLabelBG.setColor(Color.black);
            }
        }

        if (paddle != null) {
            if (event.getX() >= paddle.getWidth() / 2 && event.getX() + paddle.getWidth() / 2 <= getWidth()) {
                paddle.setLocation((event.getX() - paddle.getWidth() / 2), getHeight() - PADDLE_Y_OFFSET);
            }
        }
    }

    public void mouseClicked(MouseEvent event) {
        GObject elm = getElementAt(event.getX(), event.getY());
        if (elm == normalModeBG || elm == normalMode) {
            mode = 1;
            hasStartedGame = true;
        } else if (elm == frenzyMode || elm == frenzyModeBG) {
            mode = 5;
            hasStartedGame = true;
        } else if (elm == playAgainLabel || elm == playAgainLabelBG) {
            playAgain = true;
        }

    }

    public void setUpBall() {
        ball = new GOval(getWidth() / 2 - BALL_RADIUS / 2, getHeight() / 3 - BALL_RADIUS / 2,
                BALL_RADIUS * 2 * mode,
                BALL_RADIUS * 2 * mode);
        ball.setFilled(true);
        add(ball);
        velocityY = 2 * (mode == 1 ? 1 : (mode - 2));
        velocityX = 1 + Math.random() * 1 * mode;
        if (Math.random() > 0.5) {
            velocityX = -velocityX;
        }
    }

    public void ballHitBottom() {

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

    public void updateBall() {

        ball.move(velocityX, velocityY);
        if (ball.getY() + ball.getHeight() > getHeight()) {
            ballHitBottom();
        }
        if (ball.getX() + ball.getWidth() > getWidth()) {
            velocityX = -Math.abs(velocityX);
            // velocityX = -velocityX;
        }
        if (ball.getY() < 0) {
            velocityY = Math.abs(velocityY);

        }
        if (ball.getX() < 0) {
            velocityX = Math.abs(velocityX);

        }
    }

    public void resetBall() {
        if (livesLeft > 0 && !gameHasEnded) {
            remove(ball);
            countdown();
            setUpBall();
        } else {
            showEndScreen();
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

    public void findCornerWithElement() {
        collidePart = null;
        if (getElementAt(ball.getX(), ball.getY()) != null) {
            collidePart = getElementAt(ball.getX(), ball.getY());
        } else if (getElementAt(ball.getX() + BALL_RADIUS * 2 * mode, ball.getY()) != null) {
            collidePart = getElementAt(ball.getX() + BALL_RADIUS * 2 * mode, ball.getY());
        } else if (getElementAt(ball.getX() + BALL_RADIUS * 2 * mode, ball.getY() + BALL_RADIUS * 2 * mode) != null) {
            collidePart = getElementAt(ball.getX() + BALL_RADIUS * 2 * mode, ball.getY() + BALL_RADIUS * 2 * mode);
        } else if (getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2 * mode) != null) {
            collidePart = getElementAt(ball.getX(), ball.getY() + BALL_RADIUS * 2 * mode);
        }

    }

    public void checkForCollisions() {
        findCornerWithElement();
        if (collidePart == null) {
            return;
        } else if (collidePart == paddle) {
            velocityX = -1 * ((paddle.getX() + paddle.getWidth() / 2 - ball.getX() - ball.getWidth() / 2) / 20);

            velocityY = -Math.abs(velocityY);

        } else if (collidePart.getColor() != Color.black) {
            score += 100;
            updateScoreLabel(getWidth() / 2 - 100, 80);
            remove(collidePart);
            bricksLeft -= 1;
            if (bricksLeft <= 0) {
                flashBallGreen();
                showWinScreen();
            }
            if (mode <= 1) {
                velocityY = -velocityY;
            }
        }
    }

    public void flashBallGreen() {
        for (int i = 0; i < 3; i++) {
            ball.setColor(Color.green);
            pause(100);
            ball.setColor(Color.black);
            pause(100);
        }
    }

    public void animationLoop() {
        while (!gameHasEnded) {
            if (mode > 1) {
                Color ranColor = new Color(
                        (int) (Math.random() * 256),
                        (int) (Math.random() * 256),
                        (int) (Math.random() * 256));
                ball.setColor(ranColor);
            }
            pause(5);
            updateBall();
            checkForCollisions();
        }
    }
}

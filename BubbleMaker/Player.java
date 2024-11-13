import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player {
    // Movement properties
    private double velocityX = 0;
    private double velocityY = 0;
    private double velocityXMultiplier = 1.0;
    private double velocityYMultiplier = 1.0;
    private double xPosition;
    private double yPosition;

    // Constants
    private static final double ACCELERATION = 0.4;
    private static final double MAX_SPEED = 5.0;
    private static final double DECELERATION = 0.85;
    private static final double SIZE = 30;

    // Jump physics constants
    private static final double GRID_SIZE = 30;
    private static final double GRAVITY = 0.5;        // Adjusted gravity
    private static final double DESIRED_JUMP_HEIGHT = GRID_SIZE; // 5 grid squares
    private static final double JUMP_FORCE = -Math.sqrt(2 * GRAVITY * DESIRED_JUMP_HEIGHT); // -12.24744871391589
    private static final double MAX_FALL_SPEED = 12;  // Terminal velocity when falling

    // State flags
    private boolean onGround = false;
    private boolean moveLeft = false;
    private boolean moveRight = false;
    private boolean isJumping = false;
    private boolean collideRight = false;
    private boolean collideLeft = false;

    public Player(double x, double y) {
        this.xPosition = x;
        this.yPosition = y;
    }

    public void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case A:
                moveLeft = true;
                break;
            case D:
                moveRight = true;
                break;
            case SPACE:
                if (onGround && !isJumping) {
                    startJump();
                }
                break;
        }
    }

    public void handleKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case A:
                moveLeft = false;
                break;
            case D:
                moveRight = false;
                break;
            case SPACE:
                isJumping = false;
                break;
        }
    }

    private void startJump() {
        velocityY = JUMP_FORCE;
        onGround = false;
        isJumping = true;
    }

    private int leftover = 0;  // Add this as a class field

    public void update(double timeElapsed) {  // Modified to accept timeElapsed parameter
        // Process physics in fixed 10ms steps
        leftover += (int)(timeElapsed * 100000);
        int whole = leftover / 1000;
        leftover = leftover % 1000;
        // Process each physics step
        for (int step = 0; step < whole; step++) {
            // Horizontal movement
            if (!collideLeft && moveLeft) {
                velocityX -= ACCELERATION * 0.4;
            }
            if (!collideRight && moveRight) {
                velocityX += ACCELERATION * 0.4;
            }

            // Horizontal deceleration when no input
            if (!moveLeft && !moveRight) {
                velocityX *= 0.9; // 10% slowdown each step
            }

            // Apply gravity if not on ground
            if (!onGround) {
                velocityY += GRAVITY * 0.3; // Adjusted to match original gravity scale
            }

            // Handle jumping physics
            if (isJumping && onGround) {
                velocityY = JUMP_FORCE;
                onGround = false;
            }

            // Cap falling speed
            if (velocityY > MAX_FALL_SPEED) {
                velocityY = MAX_FALL_SPEED;
            }

            // Clamp horizontal velocity
            velocityX = Math.max(-MAX_SPEED, Math.min(MAX_SPEED, velocityX));

            // Clean up tiny movements
            if (Math.abs(velocityX) < 0.01) velocityX = 0;
            if (Math.abs(velocityY) < 0.01) velocityY = 0;
        }

        // Update position using multipliers for external forces (push/pull)
        xPosition += velocityX * velocityXMultiplier;
        yPosition += velocityY * velocityYMultiplier;

    }

    // Getters
    public double getSize() {
        return SIZE;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public double getX() {
        return xPosition;
    }

    public double getY() {
        return yPosition;
    }

    public double getVelocityXMultiplier() {
        return velocityXMultiplier;
    }

    public double getVelocityYMultiplier() {
        return velocityYMultiplier;
    }

    // Setters (Added these new setter methods)
    public void setX(double x) {
        this.xPosition = x;
    }

    public void setY(double y) {
        this.yPosition = y;
    }

    public void setVelocityX(double vx) {
        this.velocityX = vx;
    }

    public void setVelocityY(double vy) {
        this.velocityY = vy;
    }

    public void setVelocityXMultiplier(double xMultiplier) {
        this.velocityXMultiplier = xMultiplier;
    }

    public void setVelocityYMultiplier(double yMultiplier) {
        this.velocityYMultiplier = yMultiplier;
    }

    // Using this in GameRunner to detect collisions 
    public void setOnGround(boolean value) {
        this.onGround = value;
    }

    public boolean getOnGround() {
        return onGround;
    }

    public boolean getIsJumping() {
        return isJumping;
    }

    public void setIsJumping(boolean jump) {
        this.isJumping = jump;
    }

    public boolean getCollideLeft() {
        return this.collideLeft;
    }

    public boolean getCollideRight() {
        return this.collideRight;
    }

    public void setCollideLeft(boolean collide) {
        this.collideLeft = collide;
    }

    public void setCollideRight(boolean collide) {
        this.collideRight = collide;
    }

    // Draw the player
    public void draw(GraphicsContext gc, double screenX, double screenY) {
        gc.setFill(Color.BLUE);
        gc.fillRect(screenX, screenY, SIZE, SIZE);
    }
}
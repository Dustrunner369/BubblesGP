import javafx.scene.canvas.GraphicsContext;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

public class GameRunner {
    // Graphics and core components
    private static GraphicsContext gc;
    private Player player;
    private Screen screen;
    private Scene scene; 

    // Constants
    private static final int GRID_SIZE = 30;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 450;

    // Star configuration
    private ArrayList<Star> stars;
    private static final int TOTAL_STARS = 30;

    // Game state
    private boolean isGameOver = false;
    private boolean hasReachedGoal = false;

    // Animation Timer 
    private AnimationTimer animationTimer;

    // Drawing Player in middle of screen 
    double screenPlayerX = WIDTH / 2;
    double screenPlayerY = (HEIGHT + 40) / 2;

    public GameRunner() {
        try {
            player = new Player(screenPlayerX, screenPlayerY);
            screen = new Screen();
            initializeStars();
        } catch (Exception e) {
            System.err.println("Error initializing GameRunner: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeStars() {
        stars = new ArrayList<>();
        Random random = new Random();

        // Generate only shooting stars
        for (int i = 0; i < TOTAL_STARS; i++) {
            stars.add(new Star(
                    random.nextInt(WIDTH),
                    random.nextInt(HEIGHT),
                    3 + random.nextFloat() * 2  // Slightly larger size for better visibility
            ));
        }
    }

    public void start(Scene scene) {
        this.scene = scene; 
        System.out.println("Game Runner Started");

        isGameOver = false;
        hasReachedGoal = false;

        // Reset player position if needed
        resetPlayerToStart();

        scene.setOnKeyPressed(event -> {
            player.handleKeyPressed(event);
        });

        scene.setOnKeyReleased(event -> {
            player.handleKeyReleased(event);
        });

        scene.setOnMouseClicked(event -> {
            screen.handleClick(event);
        });
    
        animationTimer = new AnimationTimer() {
            private static final long TARGET_INTERVAL = 8_333_333; // ~8.33 milliseconds (120 FPS)
            private long lastUpdate = 0;
    
            @Override
            public void handle(long now) {
                // Calculate the time since the last frame in nanoseconds
                if (now - lastUpdate >= TARGET_INTERVAL) { 
                    gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
                    update();
                    checkCollisions();
                    render();
                    lastUpdate = now;
                }
            }
        };
        animationTimer.start();
    }

    private void resetPlayerToStart() {
        // Find start tile position
        for (Tile tile : DataSingleton.getInstance().getAllTiles().values()) {
            if (tile instanceof StartDecorator) {
                player.setX(tile.getX());
                player.setY(tile.getY());
            }
        }
    }

    public void stop() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    private long lastTime = System.nanoTime();

    private void update() {
        // Calculate elapsed time
        long currentTime = System.nanoTime();
        double timeElapsed = (currentTime - lastTime) / 1_000_000_000.0;
        lastTime = currentTime;

        // Update player with elapsed time
        player.update(timeElapsed);

        // Update stars with player movement
        for (Star star : stars) {
            star.update(player.getVelocityX(), player.getVelocityY());
        }

        // Update screen
        screen.update(player);

        // Update all tiles on screen
        for(int i = 0; i < screen.getVisibleTiles().size(); i++) {
            Tile currentTile = screen.getVisibleTiles().get(i);
            currentTile.update();

            // If tile is broken, erase it
            if(currentTile instanceof BreakableDecorator) {
                if(((BreakableDecorator) currentTile).isBroken() &&
                        ((BreakableDecorator) currentTile).getRemainingTime() < 0) {
                    screen.eraseTileAt(currentTile.getX(), currentTile.getY());
                    i--;
                }
            }
        }

        // Check and apply special tile effects
        checkVelocityModifiers();
        checkForceEffects();
    }

    private void render() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw stars
        for (Star star : stars) {
            star.draw(gc, screen.getScreenX(), screen.getScreenY());
        }

        screen.render(gc);
        player.draw(gc, screenPlayerX, screenPlayerY);
    }

    private void checkVelocityModifiers() {
        // Reset velocity multipliers at the start of each frame
        player.setVelocityXMultiplier(1.0);
        player.setVelocityYMultiplier(1.0);

        // Get the player's grid position
        int playerGridX = (int) Math.floor(player.getX() / 30);
        int playerGridY = (int) Math.floor(player.getY() / 30);

        // Maximum radius to check (can be adjusted)
        int maxRadius = 20;

        // Check all tiles in a square around the player
        for (int dx = -maxRadius; dx <= maxRadius; dx++) {
            for (int dy = -maxRadius; dy <= maxRadius; dy++) {
                // Get tile at this position
                Tile tile = screen.getTileAt((playerGridX + dx) * 30, (playerGridY + dy) * 30);

                // If tile exists and is a VelocityDecorator
                if (tile instanceof VelocityDecorator) {
                    VelocityDecorator velocityTile = (VelocityDecorator) tile;

                    // Calculate distance from player center to tile center
                    double playerCenterX = player.getX() + 15;
                    double playerCenterY = player.getY() + 15;
                    double tileCenterX = tile.getX() + 15;
                    double tileCenterY = tile.getY() + 15;

                    double distance = Math.sqrt(
                            Math.pow(playerCenterX - tileCenterX, 2) +
                                    Math.pow(playerCenterY - tileCenterY, 2)
                    );

                    // If player is within the tile's radius
                    if (distance <= velocityTile.getRadius() * 30) {
                        // Multiply current multipliers by this tile's modifiers
                        player.setVelocityXMultiplier(
                                player.getVelocityXMultiplier() * velocityTile.getVelocityX()
                        );
                        player.setVelocityYMultiplier(
                                player.getVelocityYMultiplier() * velocityTile.getVelocityY()
                        );
                    }
                }
            }
        }
    }

    private void checkForceEffects() {
        // Get the player's grid position
        int playerGridX = (int) Math.floor(player.getX() / 30);
        int playerGridY = (int) Math.floor(player.getY() / 30);

        // Maximum radius to check
        int maxRadius = 20;

        // Check all tiles in a square around the player
        for (int dx = -maxRadius; dx <= maxRadius; dx++) {
            for (int dy = -maxRadius; dy <= maxRadius; dy++) {
                // Get tile at this position
                Tile tile = screen.getTileAt((playerGridX + dx) * 30, (playerGridY + dy) * 30);

                // Handle Pull Decorators
                if (tile instanceof PullDecorator && ((PullDecorator) tile).isActive()) {
                    ((PullDecorator) tile).applyForce(player);
                }

                // Handle Push Decorators
                if (tile instanceof PushDecorator && ((PushDecorator) tile).isActive()) {
                    ((PushDecorator) tile).applyForce(player);
                }
            }
        }
    }

    private void checkCollisions() {
        boolean onGround = false;
        for(int i = 0; i < screen.getVisibleTiles().size(); i++) {
            Tile currentTile = screen.getVisibleTiles().get(i);

            double playerBottom = player.getY() + 30;
            double playerTop = player.getY();
            double playerLeft = player.getX();
            double playerRight = player.getX() + 30;

            double playerCenterX = player.getX() + 15;
            double playerCenterY = player.getY() + 15;
            
            double tileTop = currentTile.getY();
            double tileBottom = currentTile.getY() + 30;
            double tileLeft = currentTile.getX();
            double tileRight = currentTile.getX() + 30;

            if(currentTile.isCollidable()) {
                if (playerBottom + 2 < tileBottom && playerBottom + 2 > tileTop && 
                (playerLeft >= tileLeft && playerLeft <= tileRight - 4 || playerRight <= tileRight && playerRight >= tileLeft + 4)) {
                    player.setY(currentTile.getY() - 30);
                    player.setVelocityY(0);
                    player.setOnGround(true);
                    onGround = true;
                    handleCollision(currentTile);
                } else if (tileTop < playerTop && playerTop < tileBottom && (playerLeft > tileLeft && 
                           playerLeft < tileRight - 4 || playerRight < tileRight && playerRight > tileLeft + 4)) {
                    player.setVelocityX(0);
                    player.setVelocityY(0);
                    player.setY(currentTile.getY() + 30);
                    handleCollision(currentTile);
                }

                // Left and Right Side collisions
                if (playerLeft > tileLeft && playerLeft < tileRight && 
                    playerCenterY > tileTop && playerCenterY < tileBottom && 
                    !player.getCollideLeft()) {
                    player.setVelocityX(0);
                    player.setCollideLeft(true);
                    player.setX(currentTile.getX() + 30);
                    handleCollision(currentTile);
                } else if (playerRight < tileRight && playerRight > tileLeft && 
                         playerCenterY > tileTop && playerCenterY < tileBottom && 
                         !player.getCollideRight()) {
                    player.setVelocityX(0);
                    player.setCollideRight(true);
                    player.setX(currentTile.getX() - 30);
                    handleCollision(currentTile);
                } else {
                    player.setCollideLeft(false);
                    player.setCollideRight(false);
                }
            }
        }
        if(!onGround) {
            player.setOnGround(false);
        }
    }

    private void handleCollision(Tile tile) {
        tile.doAction();
    }

    public void setGraphicsContext(GraphicsContext gc) {
        this.gc = gc;
    }

    public Screen getScreen() {
        return screen;
    }

    public Player getPlayer() {
        return player;
    }

    public Scene getScene() { 
        return scene; 
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public boolean hasReachedGoal() {
        return hasReachedGoal;
    }
}
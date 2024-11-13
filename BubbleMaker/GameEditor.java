import javafx.scene.canvas.GraphicsContext;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class GameEditor {
    private static GraphicsContext gc;
    private Screen screen;
    private static final int TILE_SIZE = 30; 
    private static final int BUFFER_TILES = 2; 

    // Parallax background
    private ArrayList<Star> stars;
    private static final int STAR_COUNT = 200;

    // Screen Width and Height
    private static final int WIDTH = 800;
    private static final int HEIGHT = 450;
    
    // Animation Timer 
    private AnimationTimer animationTimer; 
    
    // For Game Editor Movement 
    private boolean moveLeft = false;
    private boolean moveRight = false;
    private boolean moveUp = false; 
    private boolean moveDown = false; 
    
    // Speed variables for screen movement
    private static final double ACCELERATION = 1;
    private static final double MAX_SPEED = 2.5;
    private static final double DECELERATION = 0.1;
    private double velocityX;
    private double velocityY;
    
    // Highlighted tile for editor
    private int highlightedTileX; 
    private int highlightedTileY; 

    public GameEditor() {
        screen = new Screen();
        velocityX = 0;
        velocityY = 0;
        stars = generateStars();
    }

    public Screen getScreen() {
        return screen;
    }

    public void start(Scene scene) {
        velocityX = 0;
        velocityY = 0; 
        System.out.println("Game Editor Started");
        
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);
        scene.setOnMouseMoved(this::handleMouseMoved); 

        scene.setOnMouseClicked(event -> {
            screen.handleClick(event);
        });

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
                update();
                render();
            }
        };
        animationTimer.start();
    }
    
    public void stop() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
    
    private void handleMouseMoved(MouseEvent event) { 
        double worldX = event.getX() + screen.getScreenX(); 
        double worldY = event.getY() + screen.getScreenY(); 
        highlightedTileX = (int) Math.floor(worldX / TILE_SIZE);
        highlightedTileY = (int) Math.floor(worldY / TILE_SIZE); 
    }
    
    private void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case W:
                moveUp = true;
                break;
            case A:
                moveLeft = true;
                break;
            case S:
                moveDown = true;
                break;
            case D:
                moveRight = true;
                break;
        }
    }

    private void handleKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case W:
                moveUp = false;
                break;
            case A:
                moveLeft = false;
                break;
            case S:
                moveDown = false;
                break;
            case D:
                moveRight = false;
                break;
        }
    }
       
    private ArrayList<Star> generateStars() {
        ArrayList<Star> starList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < STAR_COUNT; i++) {
            starList.add(new Star(
                    random.nextInt(WIDTH),
                    random.nextInt(HEIGHT),
                    random.nextFloat() * 2 + 1
            ));
        }
        return starList;
    }

    private void update() {
        // Update velocities based on movement flags and acceleration
        if (moveUp) {
            velocityY = Math.max(velocityY - ACCELERATION, -MAX_SPEED);
        } else if (velocityY < 0) {
            velocityY = Math.min(velocityY + DECELERATION, 0); 
        }

        if (moveDown) {
            velocityY = Math.min(velocityY + ACCELERATION, MAX_SPEED);
        } else if (velocityY > 0) {
            velocityY = Math.max(velocityY - DECELERATION, 0);
        }

        if (moveLeft) {
            velocityX = Math.max(velocityX - ACCELERATION, -MAX_SPEED);
        } else if (velocityX < 0) {
            velocityX = Math.min(velocityX + DECELERATION, 0);
        }

        if (moveRight) {
            velocityX = Math.min(velocityX + ACCELERATION, MAX_SPEED);
        } else if (velocityX > 0) {
            velocityX = Math.max(velocityX - DECELERATION, 0);
        }

        for (Star star : stars) {
            star.update(velocityX, velocityY);
        }
        
        screen.update(velocityX, velocityY);
    }

    private void render() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        drawGrid();
        screen.render(gc);
        
        // Draw highlighted tile
        double drawX = (highlightedTileX * TILE_SIZE) - screen.getScreenX();
        double drawY = (highlightedTileY * TILE_SIZE) - screen.getScreenY();
        gc.setStroke(Color.YELLOW);
        gc.strokeRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
    }
    
    private void drawGrid() {
        int px = (int) -screen.getScreenX();
        int py = (int) -screen.getScreenY();

        gc.setStroke(Color.WHITE);

        for (int x = px % TILE_SIZE; x < gc.getCanvas().getWidth(); x += TILE_SIZE) {
            gc.strokeLine(x, 0, x, gc.getCanvas().getHeight());
        }

        for (int y = py % TILE_SIZE; y < gc.getCanvas().getHeight(); y += TILE_SIZE) {
            gc.strokeLine(0, y, gc.getCanvas().getWidth(), y);
        }
    }
    
    public void setGraphicsContext(GraphicsContext gc) {
        this.gc = gc;
    }
}
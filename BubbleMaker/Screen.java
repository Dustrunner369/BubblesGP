import javafx.scene.input.MouseEvent;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Screen { 
    // Position tracking
    private double x, y; 
    private double clickedX, clickedY;
    
    // UI Constants
    private static final int TOP_BAR_HEIGHT = 40;
    private static final int TILE_SELECTOR_WIDTH = 120;
    private GraphicsContext gc;
    
    // Viewport and tile constants
    private static final int VIEWPORT_WIDTH = 800;
    private static final int VIEWPORT_HEIGHT = 450;
    private static final int TILE_SIZE = 30;
    private static final int BUFFER_TILES = 2;
    
    // Screen dimensions
    private static final int WIDTH = 800;
    private static final int HEIGHT = 450;

    private final StandardTileFactory standardTileFactory = new StandardTileFactory();
    private final TileConfigurationParser tileConfigurationParser = new TileConfigurationParser(standardTileFactory);

    private DataSingleton instance;
    private boolean gameRunning;

   public Screen() { 
      this.x = 0; 
      this.y = 0; 
   } 
   
   public Screen(double initialPlayerX, double initialPlayerY) { 
       double halfWidth = 400;   // Half of the screen's width
       double halfHeight = 225;  // Half of the screen's height
   
       // Center the screen on the player's initial position
       this.x = initialPlayerX - halfWidth;
       this.y = initialPlayerY - halfHeight;
   }
      
   
    // Handles mouse clicks for tile placement
    public void handleClick(MouseEvent event) {
        instance = DataSingleton.getInstance(); 
        gameRunning = instance.getGameRunning(); 
        if(!gameRunning) { 
            // Get raw mouse coordinates
            double mouseX = event.getX();
            double mouseY = event.getY();

            // Account for screen scroll position
            double worldX = mouseX + this.x;
            double worldY = mouseY + this.y;
            
            // Calculate grid-aligned positions
            double tileX = Math.floor(worldX / TILE_SIZE) * TILE_SIZE;
            double tileY = Math.floor(worldY / TILE_SIZE) * TILE_SIZE;
            
            // Validate click is within game area
            if (mouseY >= 0 && mouseX <= WIDTH) {
                // Check if erase mode is active
                if (TileSelectorButton.getSelectedTileType().equals("erase")) {
                    eraseTileAt(tileX, tileY);
                } else {
                    createTile(tileX, tileY);
                }
            }
        }
    }

    private void createTile(double x, double y) {
        HashMap<Long, Tile> allTiles = DataSingleton.getInstance().getAllTiles(); 
        boolean tileExists = false;
        
        for (Tile existingTile : allTiles.values()) {
            if (existingTile.getX() == x && existingTile.getY() == y) {
                tileExists = true;
                break;
            }
        }

        if (!tileExists) {
            try {
                Tile newTile = tileConfigurationParser.createTileFromConfig(TileSelectorButton.getSelectedButton().getTileConfig());
                newTile.setX(x);
                newTile.setY(y);
                DataSingleton.getInstance().addTile(newTile);
            } catch (IllegalArgumentException e) {
                System.err.println("Error creating tile: " + e.getMessage());
            }
        }
    }
    // Returns tiles that should be visible in the current viewport
    public ArrayList<Tile> getVisibleTiles() {
        // Convert screen coordinates to grid coordinates
        int startGridX = (int) Math.floor(x / TILE_SIZE) - BUFFER_TILES;
        int startGridY = (int) Math.floor(y / TILE_SIZE) - BUFFER_TILES;
        int endGridX = startGridX + (VIEWPORT_WIDTH / TILE_SIZE) + (BUFFER_TILES * 2);
        int endGridY = startGridY + (VIEWPORT_HEIGHT / TILE_SIZE) + (BUFFER_TILES * 2);

        // Return only tiles within the visible area plus buffer
        return new ArrayList<>(DataSingleton.getInstance().getAllTiles().entrySet().stream()
                .filter(entry -> {
                    Tile tile = entry.getValue();
                    int tileGridX = (int) Math.floor(tile.getX() / TILE_SIZE);
                    int tileGridY = (int) Math.floor(tile.getY() / TILE_SIZE);
                    return tileGridX >= startGridX && tileGridX <= endGridX &&
                            tileGridY >= startGridY && tileGridY <= endGridY;
                })
                .map(Map.Entry::getValue)
                .toList());
    }
    
    // Renders visible tiles
    public void render(GraphicsContext gc) {
        getVisibleTiles().forEach(tile -> {
            // Only draw if the tile would be visible on screen
            double tileScreenX = tile.getX() - x; 
            double tileScreenY = tile.getY() - y;            
            if (tileScreenX >= -TILE_SIZE && tileScreenX <= WIDTH &&
                tileScreenY >= -TILE_SIZE && tileScreenY <= HEIGHT) {
                tile.setScreenPosition(tileScreenX, tileScreenY);
                tile.draw(gc, x, y);
            }
        });
    }
   
    // Updates screen position based on the players position for gameRunner 
    public void update(Player player) { 
        double playerX = player.getX(); 
        double playerY = player.getY(); 
        
       double halfWidth = 400;   // Half of the screen's width
       double halfHeight = 490 / 2;  // Half of the screen's height
   
       // Center the screen on the player's initial position
       this.x = playerX - halfWidth;
       this.y = playerY - halfHeight;
    } 
    
    // Updates screen for editor using velocities. 
    public void update(double velocityX, double velocityY) { 
       this.x += velocityX; 
       this.y += velocityY;  
    } 


    // Erases a tile at the specified position
    public void eraseTileAt(double x, double y) {
        HashMap<Long, Tile> allTiles = DataSingleton.getInstance().getAllTiles();
        allTiles.entrySet().removeIf(entry -> {
            Tile tile = entry.getValue();
            return tile.getX() == x && tile.getY() == y;
        });
    }
    
    // Method to convert world coordinates to grid coordinates
    private int[] worldToGrid(double worldX, double worldY) {
        return new int[] {
            (int) Math.floor(worldX / TILE_SIZE),
            (int) Math.floor(worldY / TILE_SIZE)
        };
    }
    
    // Method to convert grid coordinates to world coordinates
    private double[] gridToWorld(int gridX, int gridY) {
        return new double[] {
            gridX * TILE_SIZE,
            gridY * TILE_SIZE
        };
    }
    
    // Efficient collision detection using spatial hashing
    public Tile getTileAt(double x, double y) {
        int gridX = (int) Math.floor(x / TILE_SIZE);
        int gridY = (int) Math.floor(y / TILE_SIZE);
        return DataSingleton.getInstance().getAllTiles().get(DataSingleton.getInstance().coordsToKey(gridX, gridY));
    }

    // Getters
    public double getScreenX() { 
        return this.x; 
    } 
    
    public void setY(double y) { 
      this.y = y;
    } 
    
    public void setX(double x) { 
      this.x = x;
    } 
   
    public double getScreenY() { 
        return this.y; 
    }
   
    public double getClickedX() { 
        return this.clickedX; 
    } 
   
    public double getClickedY() { 
        return this.clickedY; 
    }
    
    public static int getTileSize() {
        return TILE_SIZE;
    }
    
    public static int getViewportWidth() {
        return VIEWPORT_WIDTH;
    }
    
    public static int getViewportHeight() {
        return VIEWPORT_HEIGHT;
    }
}
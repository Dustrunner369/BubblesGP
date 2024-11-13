import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataSingleton {
    private static DataSingleton instance;
    private static boolean gameRunning;
    private static GameRunner gameRunner;
    private static GameEditor gameEditor;
    private static GraphicsContext gc;
    private static List<TileSelectorButton> tileSelectorButtons;
    
    // All the tiles in the whole game 
    private HashMap<Long, Tile> allTiles;
    
    private static final int VIEWPORT_WIDTH = 800;
    private static final int VIEWPORT_HEIGHT = 450;
    private static final int TILE_SIZE = 30;
   
    private DataSingleton() {
        gameRunning = false;
        allTiles = new HashMap<>();
        gameRunner = new GameRunner();
        gameEditor = new GameEditor();
    }

    public static DataSingleton getInstance() {
        if (instance == null) {
            instance = new DataSingleton();
        }
        return instance;
    }
    
    public void setGraphicsContext(GraphicsContext gc) {
        this.gc = gc;
        if (gameEditor != null) gameEditor.setGraphicsContext(gc);
        if (gameRunner != null) gameRunner.setGraphicsContext(gc);
    }
    
    public boolean getGameRunning() {
        return this.gameRunning;
    }
    
    public void setGameRunning(boolean gameState) {
        gameRunning = gameState;
    }
    
    public GameEditor getGameEditor() {
        return gameEditor;
    }
    
    public GameRunner getGameRunner() { 
        return gameRunner;
    } 

    public List<TileSelectorButton> getTileSelectorButtons () {
        return tileSelectorButtons;
    }

    public ArrayList<Tile> getVisibleTiles () {
        return gameRunner.getScreen().getVisibleTiles();
    }

    public void setTileSelectorButtons(List<TileSelectorButton> tileSelectorButtons) {
        this.tileSelectorButtons = tileSelectorButtons;
    }

    
    public void toggleGameState(Scene scene) {
        gameRunning = !gameRunning;
        if (gameRunning) {
            gameEditor.stop(); 
            gameRunner.start(scene); 
        } else {       
            gameRunner.stop(); 
            gameEditor.start(scene); 
        }
    }
    
    public long coordsToKey(int x, int y) {
        return ((long) x << 32) | (y & 0xFFFFFFFFL);
    }
    
    public void addTile(Tile tile) {
        int gridX = (int) Math.floor(tile.getX() / TILE_SIZE);
        int gridY = (int) Math.floor(tile.getY() / TILE_SIZE);
        allTiles.put(coordsToKey(gridX, gridY), tile);
    }
    
    public HashMap<Long, Tile> getAllTiles() { 
        return this.allTiles; 
    }

}
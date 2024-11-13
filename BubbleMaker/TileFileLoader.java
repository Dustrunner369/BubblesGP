import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TileFileLoader {
    private static final String SAVE_FILE = "save.txt";
    private static final String START_FILE = "start.txt";
    private static final int TILE_SIZE = 30;
    
    private final TileConfigurationParser configParser;
    private final DataSingleton dataSingleton;
    
    public TileFileLoader(TileConfigurationParser configParser) {
        this.configParser = configParser;
        this.dataSingleton = DataSingleton.getInstance();
    }
    
    public void loadTiles() {
        File saveFile = new File(SAVE_FILE);
        
        // Clear any existing tiles
        dataSingleton.getAllTiles().clear();
        
        if (saveFile.exists() && saveFile.length() > 0) {
            loadSavedTiles();
        }
    }
    
    private void loadSavedTiles() {
        try (Scanner scanner = new Scanner(new File(SAVE_FILE))) {
            while (scanner.hasNext()) {
                // Read grid coordinates and tile type
                int gridX = scanner.nextInt();
                int gridY = scanner.nextInt();
                String tileType = scanner.next();
                
                // Find matching configuration in start.txt
                String tileConfig = findTileConfig(tileType);
                
                if (tileConfig != null) {
                    try {
                        Tile newTile = configParser.createTileFromConfig(tileConfig);
                        
                        // Convert grid coordinates to pixel coordinates
                        double pixelX = gridX * TILE_SIZE;
                        double pixelY = gridY * TILE_SIZE;
                        
                        newTile.setX(pixelX);
                        newTile.setY(pixelY);
                        
                        // Store the tile
                        dataSingleton.addTile(newTile);
                        
                    } catch (Exception e) {
                        System.err.println("Error creating tile: " + e.getMessage());
                    }
                } else {
                    System.err.println("No configuration found for tile type: " + tileType);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error reading save file: " + e.getMessage());
        }
    }
    
    private String findTileConfig(String tileType) {
        try {
            File startFile = new File(START_FILE);
            Scanner fileScanner = new Scanner(startFile);
            
            while (fileScanner.hasNext()) {
                String line = fileScanner.nextLine().trim();
                Scanner lineScanner = new Scanner(line);
                
                if (!lineScanner.hasNext()) {
                    continue;
                }
                
                String currentType = lineScanner.next();
                
                if (currentType.equals(tileType)) {
                    return line;
                }
            }
            
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("Error reading start.txt: " + e.getMessage());
        }
        return null;
    }
}
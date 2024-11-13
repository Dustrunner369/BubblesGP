import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class TileFileWriter {
    private static final String LEVEL_FILE = "save.txt";

    public void saveLevel(HashMap<Long, Tile> tiles) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LEVEL_FILE))) {
            // Write each tile's position and type
            for (Tile tile : tiles.values()) {
                // Convert pixel coordinates to grid coordinates
                int gridX = (int) Math.floor(tile.getX() / 30);
                int gridY = (int) Math.floor(tile.getY() / 30);
                
                // Write tile data in format: x y type
                writer.printf("%d %d %s%n", gridX, gridY, tile.getName());
            }
        }
    }
}
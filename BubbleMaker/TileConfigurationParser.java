import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class TileConfigurationParser {
    private final TileFactory factory;

    public TileConfigurationParser(TileFactory factory) {
        this.factory = factory;
    }

    public Tile createTileFromConfig(String config) {
        String[] parts = config.split(" ");
        
        // Ensure we have minimum required parts
        if (parts.length < 12) {
            throw new IllegalArgumentException("Configuration must have at least 12 parts: name, colors (8), image, isCollidable, featureCount");
        }

        // Parse base tile components
        int currentIndex = 0;
        String name = parts[currentIndex++];
        
        // Parse colors
        Color fillColor = parseColor(
            Double.parseDouble(parts[currentIndex++]),
            Double.parseDouble(parts[currentIndex++]),
            Double.parseDouble(parts[currentIndex++]),
            Double.parseDouble(parts[currentIndex++])
        );
        
        Color outlineColor = parseColor(
            Double.parseDouble(parts[currentIndex++]),
            Double.parseDouble(parts[currentIndex++]),
            Double.parseDouble(parts[currentIndex++]),
            Double.parseDouble(parts[currentIndex++])
        );

        // Parse image and collidable
        String imagePath = "Assets/" + parts[currentIndex++];
        Image image = new Image(imagePath, true);
        boolean isCollidable = Boolean.parseBoolean(parts[currentIndex++]);
        
        // Get feature count
        int featureCount = Integer.parseInt(parts[currentIndex++]);
        
        // Create base tile
        Tile tile = new Tile(name, image, fillColor, outlineColor, isCollidable);
        
        // Process features
        while (featureCount > 0 && currentIndex < parts.length) {
            String feature = parts[currentIndex++].toLowerCase();
            
            switch (feature) {
                case "start":
                    tile = factory.createStartTile(tile);
                    featureCount--;
                    break;
                    
                case "goal":
                    tile = factory.createGoalTile(tile);
                    featureCount--;
                    break;
                    
                case "death":
                    tile = factory.createDeathTile(tile);
                    featureCount--;
                    break;
                    
                case "velmod":
                    if (currentIndex + 2 < parts.length) {
                        double radius = Double.parseDouble(parts[currentIndex++]);
                        double velocityX = Double.parseDouble(parts[currentIndex++]);
                        double velocityY = Double.parseDouble(parts[currentIndex++]);
                        tile = factory.createVelocityDecorator(tile, radius, velocityX, velocityY);
                        featureCount--;
                    }
                    break;
                    
                case "break":
                    if (currentIndex < parts.length) {
                        double time = Double.parseDouble(parts[currentIndex++]);
                        tile = factory.createBreakableDecorator(tile, time);
                        featureCount--;
                    }
                    break;
                    
                case "activate":
                    if (currentIndex < parts.length) {
                        String targetType = parts[currentIndex++]; // Read the target type (e.g., "bubbles")
                        tile = factory.createActivationDecorator(tile, targetType);
                        featureCount--;
                    }
                    break;
                    
                case "push":
                    if (currentIndex + 2 < parts.length) {
                        double radius = Double.parseDouble(parts[currentIndex++]);
                        double amount = Double.parseDouble(parts[currentIndex++]);
                        boolean defaultOn = Boolean.parseBoolean(parts[currentIndex++]);
                        tile = factory.createPushDecorator(tile, radius, amount, defaultOn);
                        featureCount--;
                    }
                    break;
                    
                case "pull":
                    if (currentIndex + 2 < parts.length) {
                        double radius = Double.parseDouble(parts[currentIndex++]);
                        double amount = Double.parseDouble(parts[currentIndex++]);
                        boolean defaultOn = Boolean.parseBoolean(parts[currentIndex++]);
                        tile = factory.createPullDecorator(tile, radius, amount, defaultOn);
                        featureCount--;
                    }
                    break;
                    
                case "timer":
                    if (currentIndex < parts.length) {
                        double time = Double.parseDouble(parts[currentIndex++]);
                        tile = factory.createTimerDecorator(tile, time);
                        featureCount--;
                    }
                    break;
            }
        }
        
        return tile;
    }
    
    private Color parseColor(double r, double g, double b, double a) {
        return Color.color(
            Math.min(Math.max(r, 0), 1),
            Math.min(Math.max(g, 0), 1),
            Math.min(Math.max(b, 0), 1),
            Math.min(Math.max(a, 0), 1)
        );
    }
}
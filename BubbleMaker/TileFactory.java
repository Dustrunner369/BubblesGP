import javafx.scene.image.Image;
import javafx.scene.paint.Color;

// Acts as a contract/menu for every tile object
public interface TileFactory {
    // Base tile creation
    Tile createBaseTile(String name, Image image, Color fillColor, Color outlineColor, boolean collidable);

    // Special tiles (start/goal/death)
    StartDecorator createStartTile(Tile tile);
    GoalDecorator createGoalTile(Tile tile);
    DeathDecorator createDeathTile(Tile tile);

    // Modifier tiles
    VelocityDecorator createVelocityDecorator(Tile tile, double radius, double velocityX, double velocityY);
    TimerDecorator createTimerDecorator(Tile tile, double timer);
    BreakableDecorator createBreakableDecorator(Tile tile, double time);

    // Force-based tiles
    PushDecorator createPushDecorator(Tile tile, double radius, double amount, boolean defaultOn);
    PullDecorator createPullDecorator(Tile tile, double radius, double amount, boolean defaultOn);

    // Activation/state tiles
    ActivationDecorator createActivationDecorator(Tile tile, String targetTileType);
    ToggleableDecorator createToggleableDecorator(Tile tile, String initialState);
}
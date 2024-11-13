import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class StandardTileFactory implements TileFactory {
    @Override
    public Tile createBaseTile(String name, Image image, Color fillColor, Color outlineColor, boolean collidable) {
        return new Tile(name, image, fillColor, outlineColor, collidable);
    }

    // Special tiles (start/goal/death)
    @Override
    public StartDecorator createStartTile(Tile tile) {
        return new StartDecorator(tile);
    }
    @Override
    public GoalDecorator createGoalTile(Tile tile) {
        return new GoalDecorator(tile);
    }
    @Override
    public DeathDecorator createDeathTile(Tile tile) {
        return new DeathDecorator(tile);
    }

    // Modifier tiles
    @Override
    public TimerDecorator   createTimerDecorator(Tile tile, double timer) {
        return new TimerDecorator(tile, timer);
    }
    @Override
    public VelocityDecorator createVelocityDecorator(Tile tile, double radius, double velocityX, double velocityY) {
        return new VelocityDecorator(tile, radius, velocityX, velocityY);
    }
    @Override
    public BreakableDecorator createBreakableDecorator(Tile tile, double time) {
        return new BreakableDecorator(tile, time);
    }

    // Force-based tiles
    @Override
    public PushDecorator createPushDecorator(Tile tile, double radius, double amount, boolean defaultOn) {
        return new PushDecorator(tile, radius, amount, defaultOn);
    }
    @Override
    public PullDecorator createPullDecorator(Tile tile, double radius, double amount, boolean defaultOn) {
        return new PullDecorator(tile, radius, amount, defaultOn);
    }

    // Activation/state tiles
    @Override
    public ActivationDecorator createActivationDecorator(Tile tile, String targetTileType) {
        return new ActivationDecorator(tile, targetTileType);
    }
    @Override
    public ToggleableDecorator createToggleableDecorator(Tile tile, String tileToToggle) {
        return new ToggleableDecorator(tile, tileToToggle);
    }
}
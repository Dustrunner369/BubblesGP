/// This is used in conjunction with the ActivationDecorator.
/// Tile types that are passed into the ActivationDecorator will be decorated with Toggleable.
public class ToggleableDecorator extends Tile {
    private final Tile decoratedTile;
    private String tileToToggle;

    public ToggleableDecorator(Tile decoratedTile, String tileToToggle) {
        super(decoratedTile.getName(),
            decoratedTile.getImage(),
            decoratedTile.getFillColor(),
            decoratedTile.getOutlineColor(),
            decoratedTile.isCollidable()
        );
        this.decoratedTile = decoratedTile;
        this.tileToToggle = tileToToggle;
    }

    //TODO Implement Logic
}
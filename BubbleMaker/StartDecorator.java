public class StartDecorator extends Tile {
    private final Tile decoratedTile; // Store the wrapped tile

    public StartDecorator(Tile decoratedTile) {
        super(decoratedTile.getName(),
            decoratedTile.getImage(),
            decoratedTile.getFillColor(),
            decoratedTile.getOutlineColor(),
            decoratedTile.isCollidable()
        );
        this.decoratedTile = decoratedTile;
    }

    // Additional start-specific methods can go here
    public Tile getDecoratedTile() {
        return decoratedTile;
    }
}
import java.util.ArrayList;

public class ActivationDecorator extends Tile {
    private final Tile decoratedTile;
    private final String tileType;
    private boolean isActive = true;

    public ActivationDecorator(Tile decoratedTile, String tileType) {
        // Call super with the decorated tile's properties
        super(decoratedTile.getName(),
                decoratedTile.getImage(),
                decoratedTile.getFillColor(),
                decoratedTile.getOutlineColor(),
                decoratedTile.isCollidable());
        this.decoratedTile = decoratedTile;
        this.tileType = tileType;
    }

    @Override
    public void doAction() {
        decoratedTile.doAction();
        ArrayList<Tile> visibleTiles = DataSingleton.getInstance().getVisibleTiles();

        for(Tile tile : visibleTiles) {
            if(tile.getName().equals(tileType)) {
                tile.toggleActive();
            }
        }
    }
}

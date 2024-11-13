public class VelocityDecorator extends Tile {
    private final Tile decoratedTile; // Store the wrapped tile
    private final double radius;
    private final double velocityX;
    private final double velocityY;

    public VelocityDecorator(Tile decoratedTile, double radius, double velocityX, double velocityY) {
        super(decoratedTile.getName(),
            decoratedTile.getImage(),
            decoratedTile.getFillColor(),
            decoratedTile.getOutlineColor(),
            decoratedTile.isCollidable()
        );
        this.decoratedTile = decoratedTile;
        this.radius = radius;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    // Getters
    public double getVelocityX() { return velocityX; }
    public double getVelocityY() { return velocityY; }
    public double getRadius() { return radius; }
}

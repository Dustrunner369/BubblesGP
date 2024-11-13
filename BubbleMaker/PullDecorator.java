public class PullDecorator extends Tile {
    private final Tile decoratedTile;
    private final double radius;
    private double amount;
    private boolean isActive;
    private double currentAmount;

    public PullDecorator(Tile decoratedTile, double radius, double amount, boolean defaultOn) {
        super(decoratedTile.getName(),
                decoratedTile.getImage(),
                decoratedTile.getFillColor(),
                decoratedTile.getOutlineColor(),
                decoratedTile.isCollidable());
        this.decoratedTile = decoratedTile;
        this.radius = radius;
        this.amount = amount;
        this.isActive = defaultOn;
        this.currentAmount = 0;
    }

    public void applyForce(Player player) {
        if (!isActive || currentAmount >= amount) {
            return;
        }

        // Calculate centers
        double playerCenterX = player.getX() + 15;
        double playerCenterY = player.getY() + 15;
        double tileCenterX = getX() + 15;
        double tileCenterY = getY() + 15;

        double distance = Math.sqrt(
                Math.pow(playerCenterX - tileCenterX, 2) +
                        Math.pow(playerCenterY - tileCenterY, 2)
        );

        // Only apply force if within radius
        if (distance <= radius * 30 && distance > 0) {
            // Calculate direction vectors (pulling towards tile center)
            double dirX = (tileCenterX - playerCenterX) / distance;
            double dirY = (tileCenterY - playerCenterY) / distance;

            // Calculate force based on distance (stronger when closer)
            double forceFactor = 1 - (distance / (radius * 30));
            double pullForce = 1.5 * forceFactor;

            // Apply force to player's velocity
            player.setVelocityX(player.getVelocityX() + dirX * pullForce);
            player.setVelocityY(player.getVelocityY() + dirY * pullForce);

            // Update accumulated amount
            currentAmount += pullForce;
            if (currentAmount >= amount) {
                isActive = false;
            }
        }
    }

    public boolean isActive() {
        return isActive && currentAmount < amount;
    }

    public double getRadius() {
        return radius;
    }
}
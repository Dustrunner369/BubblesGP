public class PushDecorator extends Tile {
    private final Tile decoratedTile;
    private final double radius;
    private double amount;
    private boolean isActive;
    private double currentAmount;

    public PushDecorator(Tile decoratedTile, double radius, double amount, boolean defaultOn) {
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
            // Calculate direction vectors (pushing away from tile center)
            double dirX = (playerCenterX - tileCenterX) / distance;
            double dirY = (playerCenterY - tileCenterY) / distance;
            System.out.println("dirX: " + dirX);


            // Calculate force based on distance (stronger when closer)
            double forceFactor = 1 - (distance / (radius * 30));
            double pushForce = 5 * forceFactor;

            // Apply force to player's velocity
            player.setVelocityX(player.getVelocityX() + dirX * pushForce);
            player.setVelocityY(player.getVelocityY() + dirY * pushForce);
//            System.out.println("X: " + player.getVelocityX() + dirX * pushForce);
//            System.out.println("Y: " + player.getVelocityY() + dirY * pushForce);
            // Update accumulated amount
            currentAmount += pushForce;
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
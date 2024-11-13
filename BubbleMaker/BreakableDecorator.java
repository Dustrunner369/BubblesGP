import javafx.scene.canvas.GraphicsContext;

public class BreakableDecorator extends Tile {
    private final Tile decoratedTile;
    private final double timerDuration;
    private double currentTime;
    private boolean isBroken;
    private boolean isTimerStarted;
    private double opacity;

    public BreakableDecorator(Tile decoratedTile, double timerDuration) {
        // Call super with the decorated tile's properties
        super(decoratedTile.getName(),
                decoratedTile.getImage(),
                decoratedTile.getFillColor(),
                decoratedTile.getOutlineColor(),
                decoratedTile.isCollidable());
        this.decoratedTile = decoratedTile;
        this.timerDuration = timerDuration;
        this.currentTime = 0;
        this.isBroken = false;
        this.isTimerStarted = false;
        this.opacity = 1.0;
    }

    @Override
    public void update() {
        //decoratedTile.update();

        if (isTimerStarted && !isBroken) {
            currentTime += 1.0 / 60.0;

            // Calculate opacity based on remaining time
            opacity = 1.0 - (currentTime / timerDuration);

            // Check if timer has finished
            if (currentTime >= timerDuration) {
                isBroken = true;
                opacity = 0.0;
            }
        }
    }

    @Override
    public void draw(GraphicsContext gc, double screenX, double screenY) {
        if (!isBroken) {
            // Save the current global alpha
            double currentAlpha = gc.getGlobalAlpha();

            // Calculate screen position
            double drawX = getX() - screenX;
            double drawY = getY() - screenY;

            // Draw with current opacity
            gc.setGlobalAlpha(opacity);

            // Draw background fill
            gc.setFill(getFillColor());
            gc.fillRect(drawX, drawY, 30, 30);

            // Draw image if exists
            if (getImage() != null) {
                gc.drawImage(getImage(), drawX, drawY, 30, 30);
            }

            // Draw outline
            gc.setStroke(getOutlineColor());
            gc.setLineWidth(1.0);
            gc.strokeRect(drawX, drawY, 30, 30);

            // Restore original alpha
            gc.setGlobalAlpha(currentAlpha);
        }
    }

    @Override
    public void doAction() {
        super.doAction();
        if (!isTimerStarted) {
            isTimerStarted = true;
        }
    }

    // Override necessary getters and setters
    @Override
    public double getX() {
        return decoratedTile.getX();
    }

    @Override
    public double getY() {
        return decoratedTile.getY();
    }

    @Override
    public void setX(double x) {
        decoratedTile.setX(x);
    }

    @Override
    public void setY(double y) {
        decoratedTile.setY(y);
    }

    // Additional methods specific to BreakableDecorator
    public boolean isBroken() {
        return isBroken;
    }

    public double getRemainingTime() {
        return timerDuration - currentTime;
    }

    public double getOpacity() {
        return opacity;
    }
}
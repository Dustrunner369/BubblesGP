public class TimerDecorator extends Tile {
    private final Tile decoratedTile;
    private final double timerDuration;
    private double currentTime;
    private boolean isActive = true;

    public TimerDecorator(Tile decoratedTile, double timerDuration) {
        // Call super with the decorated tile's properties
        super(decoratedTile.getName(),
                decoratedTile.getImage(),
                decoratedTile.getFillColor(),
                decoratedTile.getOutlineColor(),
                decoratedTile.isCollidable());
        this.decoratedTile = decoratedTile;
        this.timerDuration = timerDuration;
        this.currentTime = 0;
    }

    @Override
    public void update() {
        // Update timer logic
        currentTime += 1.0 / 60.0; // Assuming 60 FPS, increment by seconds

        if (currentTime >= timerDuration) {
            doAction();
            // Reset timer
            currentTime = 0;
        }
    }

    @Override
    public void doAction() {
        decoratedTile.doAction();
    }

    // Additional methods specific to TimerDecorator
    public boolean isCollidable() {
        return isActive;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public double getTimerDuration() {
        return timerDuration;
    }

    public void resetTimer() {
        currentTime = 0;
    }

    // Method to force trigger the timer (useful for testing or manual activation)
    public void forceTrigger() {
        isActive = !isActive;
        currentTime = 0;
    }
}
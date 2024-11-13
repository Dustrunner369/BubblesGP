import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;
import java.util.ArrayList;

public class Star {
    // Basic properties
    private double worldX;
    private double worldY;
    private double size;
    private boolean isActive;
    private Color starColor;
    private ArrayList<Particle> particles;
    private double velocityX;
    private double velocityY;
    
    // Screen dimensions
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 450;
    
    // Shooting star properties
    private static final double BASE_SPEED = .15; // Reduced from 4.0
    private static final int MAX_PARTICLES = 12;
    private static final double PARTICLE_SPAWN_RATE = 0.4; // Reduced from 0.8
    private static final Random RANDOM = new Random();
    private static final double ACTIVATION_CHANCE = 0.0015; // Reduced from 0.01
    
    private class Particle {
        double worldX, worldY;
        double size;
        double alpha;
        Color color;
        
        Particle(double x, double y, double size, Color color) {
            this.worldX = x;
            this.worldY = y;
            this.size = size;
            this.color = color;
            this.alpha = 0.5 + RANDOM.nextDouble() * 0.3;
        }
        
        void update() {
            worldX += velocityX;
            worldY += velocityY;
            size *= 0.95;
            alpha *= 0.95;
        }
        
        void draw(GraphicsContext gc, double screenOffsetX, double screenOffsetY) {
            gc.setGlobalAlpha(this.alpha);
            gc.setFill(this.color);
            gc.fillOval(this.worldX - screenOffsetX, this.worldY - screenOffsetY, this.size, this.size);
        }
    }

    public Star(double x, double y, double size) {
        this.worldX = x;
        this.worldY = y;
        this.size = size;
        this.isActive = false;
        this.particles = new ArrayList<>();
        initializeShootingStar();
    }
    
    private void initializeShootingStar() {
        double hue = RANDOM.nextDouble() * 360;
        this.starColor = Color.hsb(hue, 0.8, 1.0, 0.9);
        
        // Initialize in world coordinates
        this.worldX = -100 + RANDOM.nextDouble() * (SCREEN_WIDTH + 200);
        this.worldY = -50;
        
        // Calculate angle between 90 and 270 degrees (downward trajectories)
        double angle = 90 + RANDOM.nextDouble() * 180;
        double angleRad = Math.toRadians(angle);
        
        // Convert angle to velocity components
        velocityX = BASE_SPEED * Math.cos(angleRad);
        velocityY = BASE_SPEED * Math.sin(angleRad);
        
        particles.clear();
    }

    private void updateParticles() {
        particles.removeIf(p -> p.alpha < 0.1 || p.size < 0.5);
        
        if (isActive && particles.size() < MAX_PARTICLES && RANDOM.nextDouble() < PARTICLE_SPAWN_RATE) {
            particles.add(new Particle(worldX, worldY, size * (0.7 + RANDOM.nextDouble() * 0.3), starColor));
        }
        
        for (Particle p : particles) {
            p.update();
        }
    }
    
    public void update(double playerVelX, double playerVelY) {
        if (!isActive) {
            if (RANDOM.nextDouble() < ACTIVATION_CHANCE) {
                isActive = true;
                initializeShootingStar();
            }
        } else {
            worldX += velocityX;
            worldY += velocityY;
            
            updateParticles();
            
            if (worldY > SCREEN_HEIGHT * 2 || 
                worldX < -SCREEN_WIDTH || 
                worldX > SCREEN_WIDTH * 2) {
                isActive = false;
                particles.clear();
            }
        }
    }
    
    public void draw(GraphicsContext gc, double screenOffsetX, double screenOffsetY) {
        if (isActive) {
            double drawX = worldX - screenOffsetX;
            double drawY = worldY - screenOffsetY;
            
            if (drawX >= -size && drawX <= SCREEN_WIDTH + size &&
                drawY >= -size && drawY <= SCREEN_HEIGHT + size) {
                
                // Draw particles
                for (Particle p : particles) {
                    p.draw(gc, screenOffsetX, screenOffsetY);
                }
                
                // Draw the main shooting star
                gc.setGlobalAlpha(0.8);
                gc.setFill(starColor);
                gc.fillOval(drawX, drawY, size * 1.5, size);
                gc.setGlobalAlpha(1.0);
            }
        }
    }
}
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Tile {

   // Member Variables
   private static final int size = 30;
   private double x, y;
   private double initialX, initialY; 
   private final String name;
   private final Image image;
   private Color fillColor;
   private Color outlineColor;
   private boolean isCollidable;
   private boolean isActive = true;
   private double screenPosX, screenPosY; 
   
   // Added constant for image opacity
   private static final double IMAGE_OPACITY = 0.7; // Adjust this value between 0.0 and 1.0 to control transparency

   public Tile(String name, Image image, Color fillColor, Color outlineColor, boolean isCollidable) {
      this.name = name;
      this.image = image;
      this.fillColor = fillColor;
      this.outlineColor = outlineColor;
      this.isCollidable = isCollidable;
   }

   public Tile(Tile tileToCopy) {
      this.name = tileToCopy.getName();
      this.image = tileToCopy.getImage();
      this.fillColor = tileToCopy.getFillColor();
      this.outlineColor = tileToCopy.getOutlineColor();
      this.isCollidable = tileToCopy.isCollidable();
   }

   public void draw(GraphicsContext gc, double screenx, double screeny) {
      if(isActive) {
         double tempX = x - screenx;
         double tempY = y - screeny;

         // Save the current global alpha
         double currentAlpha = gc.getGlobalAlpha();

         // Draw the fill color first as the background
         gc.setGlobalAlpha(1.0); // Full opacity for background
         gc.setFill(fillColor);
         gc.fillRect(tempX, tempY, size, size);

         // Draw the image if one exists, scaled to fit the tile
         if (image != null) {
            // Set partial transparency for the image
            gc.setGlobalAlpha(IMAGE_OPACITY);
            gc.drawImage(image, tempX, tempY, size, size);
         }

         // Draw the outline last, with full opacity
         gc.setGlobalAlpha(1.0);
         gc.setStroke(outlineColor);
         gc.setLineWidth(1.0); // 1 pixel outline as specified
         gc.strokeRect(tempX, tempY, size, size);

         // Restore the original global alpha
         gc.setGlobalAlpha(currentAlpha);
      }
   }

   public void update() {}
   public void doAction() {}
   public void toggleActive() {
      isActive = !isActive;
      isCollidable = isActive;
   }

   // Getters
   public double getX() { 
      return this.x; 
   }
   public double getY() { 
      return this.y; 
   }
   public double getInitialX() { 
      return this.initialX; 
   } 
   public double getInitialY() { 
      return this.initialY; 
   } 
   public String getName() { return this.name; }
   public Image getImage() {
      return this.image;
   }
   public Color getFillColor() { return this.fillColor; }
   public Color getOutlineColor() { return this.outlineColor; }
   public boolean isCollidable() { return this.isCollidable; }
   public boolean getIsActive() { return this.isActive; }
   public double getScreenX() {
      return this.screenPosX;
   }
   public double getScreenY() {
      return this.screenPosY;
   }

   // Setters
   public void setX(double x) { 
      this.x = x; 
   }
   public void setY(double y) { 
      this.y = y; 
   } 
   public void setInitialX(double x) { 
      initialX = x; 
   }
   public void setInitialY(double Y) { 
      initialY = Y; 
   }
   public void setCollidable(boolean value) { 
      this.isCollidable = value; 
   }  
   public void setIsActive(boolean value) { this.isActive = value; }
   public void setScreenPosition(double tileScreenX, double tileScreenY) {
      this.screenPosX = tileScreenX;
      this.screenPosY = tileScreenY;
   }

   @Override 
   public String toString() { 
      return "Name: " +  name + " Image: " + image + " Color: " + fillColor + "outlineColor: " + outlineColor +
       " isCollidable " + isCollidable;
   }
}
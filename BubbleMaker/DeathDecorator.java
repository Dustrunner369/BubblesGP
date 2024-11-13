import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class DeathDecorator extends Tile {
    private final Tile decoratedTile;
    private DataSingleton instance; 
    private GameRunner gameRunner; 
    private Player player;
    private Screen screen; 
    private Scene scene; 
    private boolean activated = false; 

    public DeathDecorator(Tile decoratedTile) {
        // Call super with the decorated tile's properties
        super(decoratedTile.getName(),
                decoratedTile.getImage(),
                decoratedTile.getFillColor(),
                decoratedTile.getOutlineColor(),
                decoratedTile.isCollidable());
        this.decoratedTile = decoratedTile;
    }

    public void doAction() {
        if(decoratedTile != null) { 
            decoratedTile.doAction(); 
        }
        if (!activated) {
            activated = true;
            instance = DataSingleton.getInstance();
            gameRunner = instance.getGameRunner();
            player = gameRunner.getPlayer();
            screen = gameRunner.getScreen();
            scene = gameRunner.getScene();
            
            // Create text for game over message
            Text gameOverText = new Text("You Died!!");
            gameOverText.setFont(Font.font("Arial", 48));
            gameOverText.setFill(Color.RED);
            
            // Add shadow effect to the text
            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.BLACK);
            shadow.setOffsetX(3);
            shadow.setOffsetY(3);
            gameOverText.setEffect(shadow);
            
            Button quitButton = new Button("Quit Game");
            quitButton.setStyle(
                "-fx-background-color: #f44336;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-padding: 10px 20px;" +
                "-fx-background-radius: 5px;"
            );
            
            // Create layout
            VBox layout = new VBox(20);
            layout.setAlignment(Pos.CENTER);
            layout.setStyle(
                "-fx-background-color: #2C3E50;" + 
                "-fx-padding: 20px;"
            );
            
            // Add elements to layout
            layout.getChildren().addAll(
                gameOverText,
                quitButton
            );
            
            // Center the layout in the BorderPane
            BorderPane root = new BorderPane();
            root.setCenter(layout);
            root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);"); // Semi-transparent background
            
            quitButton.setOnAction(e -> {
                System.exit(0);
            });

            // Add the game over screen to the current scene
            BorderPane currentRoot = (BorderPane) scene.getRoot();
            currentRoot.setCenter(root);
            
            // Optional: Add fade-in animation
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
    }
}
